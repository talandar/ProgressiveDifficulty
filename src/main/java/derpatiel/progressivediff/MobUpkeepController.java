package derpatiel.progressivediff;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;

import java.util.Map;

public class MobUpkeepController {

    private static final int UPKEEP_INTERVAL = 20;

    private static final Map<EntityLiving,Map<String,Integer>> storedChangeMap = Maps.newHashMap();

    private static int upkeepCount=0;

    public static void register(EntityLiving mob, Map<String,Integer> changes){
        storedChangeMap.put(mob,changes);
    }

    public static void tick(){
        upkeepCount++;
        if(upkeepCount>=UPKEEP_INTERVAL){
            upkeepCount=0;
            doUpkeep();
        }
    }

    private static void doUpkeep(){

    }
}
