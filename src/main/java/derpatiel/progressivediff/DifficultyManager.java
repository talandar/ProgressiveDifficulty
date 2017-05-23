package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import java.util.*;

import static derpatiel.progressivediff.DifficultyConfiguration.*;

public class DifficultyManager {

    private static double[] cumulativeWeight;
    private static String[] modifierKey;
    private static double totalWeight;

    private static final List<DifficultyControl> controls = Lists.newArrayList();
    private static final Map<String,DifficultyModifier> modifiers = Maps.newHashMap();

    private static final Map<Integer,Map<EntityLiving,SpawnEventDetails>> eventsThisTickByDimension = Maps.newHashMap();

    public static void addDifficultyControl(DifficultyControl control){
        controls.add(control);
    }
    public static void addDifficultyModifier(DifficultyModifier modifier){
        modifiers.put(modifier.getIdentifier(),modifier);
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
        difficulty-=EntityFilter.getMobSpawnCost(details.entity);
        for(DifficultyControl control : controls){
            difficulty+=control.getChangeForSpawn(details);
        }

        return difficulty;
    }


    private static void makeDifficultyChanges(EntityLiving entity, int determinedDifficulty, Random rand) {
        Map<String, Integer> thisSpawnModifiers = Maps.newHashMap();
        int initialDifficulty = determinedDifficulty;
        int failCount = 0;
        while (determinedDifficulty > allowedMargin && failCount < maxFailCount) {
            DifficultyModifier pickedModifier = pickModifierFromList(rand);
            boolean failed = true;
            if (pickedModifier.costPerChange() <= (determinedDifficulty + allowedMargin) && pickedModifier.validForEntity(entity)) {
                //add mod to list, IFF not past max
                int numAlreadyInList = thisSpawnModifiers.computeIfAbsent(pickedModifier.getIdentifier(), result -> 0);
                if (numAlreadyInList < pickedModifier.getMaxInstances()) {
                    thisSpawnModifiers.put(pickedModifier.getIdentifier(), 1 + thisSpawnModifiers.get(pickedModifier.getIdentifier()));
                    //reduce remainder of difficulty
                    determinedDifficulty -= pickedModifier.costPerChange();
                    failed = false;
                    failCount = 0;
                }
            }
            if (failed) {
                failCount++;
            }
        }

        String log = "For spawn of " + EntityList.getEntityString(entity) + " with difficulty " + initialDifficulty + ", ("+determinedDifficulty+" remaining) decided to use: ";
        for (String modId : thisSpawnModifiers.keySet()) {
            int numToApply = thisSpawnModifiers.get(modId);
            modifiers.get(modId).makeChange(numToApply, entity,false);
            log = log + modId + " " + numToApply + " times, ";
        }
        LOG.info(log);
        if(thisSpawnModifiers.size()>0) {
            MobNBTHandler.setChangeMap(entity,thisSpawnModifiers);
        }
    }

    public static DifficultyModifier getModifierById(String id){
        return modifiers.get(id);
    }

    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn checkSpawnEvent) {
        if(DifficultyConfiguration.controlEnabled) {
            SpawnEventDetails details = new SpawnEventDetails();
            if (EntityFilter.shouldModifyEntity(checkSpawnEvent.getEntityLiving())) {
                details.entity = (EntityLiving) checkSpawnEvent.getEntityLiving();
                details.spawnEvent = checkSpawnEvent;
                details.fromSpawner = false;
                eventsThisTickByDimension.computeIfAbsent(details.entity.world.provider.getDimension(), thing -> new HashMap<>()).put(details.entity, details);
            }
        }
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
            if(difficulty<0 && DifficultyConfiguration.negativeDifficultyPreventsSpawn){
                int chance = joinWorldEvent.getWorld().rand.nextInt(100);
                if(Math.abs(difficulty)>=chance){
                    joinWorldEvent.setCanceled(true);
                    return;
                }
            }
            if(difficulty>=threshold) {
                makeDifficultyChanges(mobToSpawn, difficulty, joinWorldEvent.getWorld().rand);
            }
        }
    }

    public static void generateWeightMap() {
        cumulativeWeight = new double[modifiers.size()];
        modifierKey = new String[modifiers.size()];
        totalWeight = 0.0d;
        int count=0;
        for(DifficultyModifier modifier : modifiers.values()){
            totalWeight+=modifier.getWeight();
            cumulativeWeight[count]=totalWeight;
            modifierKey[count]=modifier.getIdentifier();
            count++;
        }
    }
    private static DifficultyModifier pickModifierFromList(Random rand) {
        double weightToFind = rand.nextDouble() * totalWeight;
        for(int i=0;i<cumulativeWeight.length;i++){
            if(weightToFind<cumulativeWeight[i]){
                return modifiers.get(modifierKey[i]);
            }
        }
        return null;
    }
}
