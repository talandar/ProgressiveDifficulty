package derpatiel.progressivediff;

import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityList;
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
            String regionName = MobNBTHandler.getEntityRegion(entity);
            Map<String,Integer> changes = MobNBTHandler.getChangeMap(entity);
            Region region = DifficultyManager.getRegion(regionName);
            String mobId = EntityList.getEntityString(entity);
            for(String change : changes.keySet()){
                try {
                    DifficultyModifier modifier = region.getModifierForMobById(mobId,change);
                    if (modifier != null) {
                        modifier.handleUpkeepEvent(changes.get(change), entity);
                    }
                }catch(Exception e){
                    LOG.warn("Error applying modifier at upkeep.  Mob was "+entity.getName()+", Modifier was "+change+".  Please report to Progressive Difficulty Developer!");
                    LOG.warn("\tCaught Exception had message "+e.getMessage());
                }
            }
        }
    }
}
