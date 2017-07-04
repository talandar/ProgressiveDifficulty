package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class MobUpkeepController {

    private static final int UPKEEP_INTERVAL = 10;//upkeep every half second
    public static final int POTION_EFFECT_LENGTH = 15;//.75 second potion effect length (a tiny bit longer than the upkeep, in case of lag or something)

    private static int upkeepCount=0;

    public static void tick(World world){
        upkeepCount++;
        if(upkeepCount>=UPKEEP_INTERVAL){
            upkeepCount=0;
            doUpkeep(world);
        }
    }

    private static void doUpkeep(World world){
        List<EntityLiving> modifiedEntities = MobNBTHandler.getModifiedEntities(world);

        for(EntityLiving entity : modifiedEntities){
            Map<String,Integer> changes = MobNBTHandler.getChangeMap(entity);
            for(String change : changes.keySet()){
                DifficultyModifier modifier = DifficultyManager.getModifierById(change);
                if(modifier!=null) {
                    modifier.handleUpkeepEvent(changes.get(change),entity);
                }
            }
        }
    }
}
