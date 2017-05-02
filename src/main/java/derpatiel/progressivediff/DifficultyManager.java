package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static derpatiel.progressivediff.DifficultyConfiguration.*;

public class DifficultyManager {

    private static final List<DifficultyControl> controls = Lists.newArrayList();
    private static final List<DifficultyModifier> modifiers = Lists.newArrayList();

    private static final Map<Integer,Map<EntityLiving,SpawnEventDetails>> eventsThisTickByDimension = Maps.newHashMap();

    public static void addDifficultyControl(DifficultyControl control){
        controls.add(control);
    }
    public static void addDifficultyModifier(DifficultyModifier modifier){
        modifiers.add(modifier);
    }

    public static void clearModifiersAndControls(){
        controls.clear();
        modifiers.clear();
    }

    public static void onWorldTick(int dimensionId){
        eventsThisTickByDimension.computeIfAbsent(dimensionId, thing -> new HashMap<>()).clear();
    }

    private static int determineDifficultyForSpawnEvent(SpawnEventDetails details){
        int difficulty = baseDifficulty;

        for(DifficultyControl control : controls){
            difficulty+=control.getChangeForSpawn(details);
        }

        return difficulty;
    }


    private static void makeDifficultyChanges(EntityLiving entity, int determinedDifficulty){
        if (determinedDifficulty > 100){
            //for now, just put it all on the first one
            modifiers.get(0).makeChange(determinedDifficulty-100,entity);
        }

    }

    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn checkSpawnEvent) {
        SpawnEventDetails details = new SpawnEventDetails();
        details.entity = (EntityLiving)checkSpawnEvent.getEntityLiving();
        details.spawnEvent = checkSpawnEvent;
        details.fromSpawner=false;
        eventsThisTickByDimension.computeIfAbsent(details.entity.world.provider.getDimension(), thing -> new HashMap<>()).put(details.entity,details);
    }

    public static void onSpecialSpawnEvent(LivingSpawnEvent.SpecialSpawn specialSpawnEvent) {
        SpawnEventDetails details = eventsThisTickByDimension.computeIfAbsent(specialSpawnEvent.getEntityLiving().world.provider.getDimension(), thing -> new HashMap<>()).get(specialSpawnEvent.getEntityLiving());
        if(details!=null){
            details.fromSpawner=true;
        }

    }

    public static void onJoinWorldEvent(EntityJoinWorldEvent joinWorldEvent) {
        //we actually got to this step, so lets do something with it.
        //note: we check this conversion up in the caller of this class.  should be safe.
        EntityLiving mobToSpawn = (EntityLiving)joinWorldEvent.getEntity();
        SpawnEventDetails details = eventsThisTickByDimension.computeIfAbsent(joinWorldEvent.getEntity().world.provider.getDimension(), thing -> new HashMap<>()).get(mobToSpawn);
        if(details!=null) {
            int difficulty = determineDifficultyForSpawnEvent(details);
            makeDifficultyChanges(mobToSpawn, difficulty);
        }


    }
}
