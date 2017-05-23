package derpatiel.progressivediff;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import java.util.Map;

public class MobUpkeepController {

    private static final int UPKEEP_INTERVAL = 20;

    private static final Map<Integer,Map<String,Integer>> storedChangeMap = Maps.newHashMap();

    private static int upkeepCount=0;

    public static void register(EntityLiving mob, Map<String,Integer> changes){
        storedChangeMap.put(mob.getEntityId(),changes);
    }

    public static void tick(World world){
        upkeepCount++;
        if(upkeepCount>=UPKEEP_INTERVAL){
            upkeepCount=0;
            doUpkeep(world);
        }
    }

    private static void doUpkeep(World world){
        for(int entityId : storedChangeMap.keySet()) {
            Entity entity = world.getEntityByID(entityId);
            if(entity==null || entity.isDead || !(entity instanceof EntityLiving)){
                continue;
            }
            Map<String,Integer> changes = storedChangeMap.get(entityId);
            for(String change : changes.keySet()){
                DifficultyModifier modifier = DifficultyManager.getModifierById(change);
                modifier.makeChange(changes.get(change),(EntityLiving)entity,true);
            }
        }
    }
}
