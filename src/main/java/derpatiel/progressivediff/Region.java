package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Region implements Comparable<Region>{


    private final String name;
    private int minX;//-30000000
    private int maxX;// 30000000
    private int minY;// 0
    private int maxY;// 256
    private int minZ;//-30000000
    private int maxZ;// 30000000

    private int dimensionId;

    private int baseDifficulty;
    private int allowedMargin;
    private int maxFailCount;
    private int threshold;
    private boolean negativeDifficultyPreventsSpawn;


    private RegionMobConfig defaultConfig;
    private final Map<String,RegionMobConfig> byMobConfig = Maps.newHashMap();
    private final Map<String, Integer> mobSpawnCosts = Maps.newHashMap();


    public Region(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public BigInteger getVolume(){
        BigInteger xRange = BigInteger.valueOf(maxX-minX);
        BigInteger yRange = BigInteger.valueOf(maxY-minY);
        BigInteger zRange = BigInteger.valueOf(maxZ-minZ);
        return xRange.multiply(yRange).multiply(zRange);
    }


    private RegionMobConfig getConfigForMob(String mobId){
        return byMobConfig.getOrDefault(mobId,defaultConfig);
    }

    public void readConfig(){
        File regionFolder = new File(DifficultyManager.getConfigDir(),getName());
        File regionConfigFile = new File(regionFolder,"region.cfg");
        File defaultRegionConfigFile = new File(regionFolder,"default.cfg");
        File[] filesInRegion = regionFolder.listFiles();
        List<File> mobConfigFiles = Lists.newArrayList();
        if(filesInRegion!=null) {
            mobConfigFiles.addAll(Arrays.stream(filesInRegion)
                    .filter(
                            file -> {
                                return !(file.getName().endsWith("region.cfg") || file.getName().endsWith("default.cfg"));
                            }).collect(Collectors.toList()));
        }
        Configuration regionConfig = new Configuration(regionConfigFile);
        readRegionConfig(regionConfig);
        Configuration defaultConfiguration = new Configuration(defaultRegionConfigFile);
        try {
            defaultConfiguration.load();
            defaultConfig = new RegionMobConfig(defaultConfiguration);
        }finally {
            if(defaultConfiguration.hasChanged()){
                defaultConfiguration.save();
            }
        }

        for(File mobConfigFile : mobConfigFiles){
            String mobId = mobConfigFile.getName();
            mobId = mobId.substring(0,mobId.lastIndexOf("."));
            Configuration config = new Configuration(mobConfigFile);
            try {
                config.load();
                RegionMobConfig mobConfig = new RegionMobConfig(config);
                byMobConfig.put(mobId,mobConfig);
            }finally {
                if(config.hasChanged()){
                    config.save();
                }
            }
        }
    }

    @Override
    public int compareTo(Region o) {
        return this.getVolume().compareTo(o.getVolume());
    }

    public int getMobBaseSpawnCost(EntityLivingBase entity){
        return mobSpawnCosts.getOrDefault(EntityList.getEntityString(entity),0);
    }

    private void readRegionConfig(Configuration regionConfig){
        try {
            regionConfig.load();
            Property baseDifficultyProp = regionConfig.get(Configuration.CATEGORY_GENERAL,
                    "BaseDifficulty", 0, "Base Difficulty before any modifiers are added. 0 is baseline vanilla.  If this is negative, mobs will be easier, Decreasing this has an effect of making the game ");
            baseDifficulty = baseDifficultyProp.getInt();
            Property allowedMarginProp = regionConfig.get(Configuration.CATEGORY_GENERAL,
                    "AllowedMargin", 5, "If the difficulty of a mob is this close to the target, stop looking.  Larger values will cause more variance in mob difficulty, but smaller values may cause excessive computation attempting to find an exact match.");
            allowedMargin = Math.abs(allowedMarginProp.getInt());
            Property maxFailCountProp = regionConfig.get(Configuration.CATEGORY_GENERAL,
                    "MaxAllowedFailures", 5, "Allow this many failures while trying to apply modifiers.  Higher values might cause modifier determination to take a long time, but allows closer control over difficulty.");
            maxFailCount = Math.abs(maxFailCountProp.getInt());
            Property thresholdProp = regionConfig.get(Configuration.CATEGORY_GENERAL,
                    "ModificationThresold", 0, "Set a threshold that limits when difficulty modifiers will be applied.  Values significantly above 'AllowedMargin' would cause many mobs to be unmodified, but ones that are modified to be significantly modified.");
            threshold = thresholdProp.getInt();

            Property negativeDifficultyPreventsSpawnProp = regionConfig.get(Configuration.CATEGORY_GENERAL,
                    "PreventLowDifficultySpawns", true, "Spawns with a negative calculated difficulty for any reason (usually \"MobBaseDifficulty\"), will have a chance of not spawning at all.  The chance of it not spawning is equal to the negative difficulty as a percent.  (-50 has a 50/50 chance of spawning, -101 will never spawn)");
            negativeDifficultyPreventsSpawn = negativeDifficultyPreventsSpawnProp.getBoolean();

            Property mobSpawnMapProp = regionConfig.get(Configuration.CATEGORY_GENERAL,"MobBaseDifficulty",generateDefaultSpawnCosts(),"A set of mob costs, of the format \"<mobRegistryName>:<cost>\".  " +
                    "Provides bonus difficulty points to the mob before spawning if the number is positive.  If the number is negative, subtract that much difficulty from the mod before applying modifiers.  If the result after all controls is still negative, the value is used as the chance out of 100 that the mob spawn is cancelled entirely.");
            mobSpawnCosts.clear();
            Arrays.stream(mobSpawnMapProp.getStringList()).forEach(entry->{
                int index = entry.lastIndexOf(":");
                if(index<0) {
                    LOG.error("Problem reading line for mob spawn cost.  Needed \"<mobRegistryName>:<cost>\", but string was " + entry);
                }else{
                    String name = entry.substring(0,index);
                    String valStr = entry.substring(index+1);
                    try {
                        int value = Integer.parseInt(valStr);
                        mobSpawnCosts.put(name,value);
                    }catch(Exception e){
                        LOG.error("Problem reading line for mob spawn cost.  Second parameter should have been integer, but was "+valStr);
                    }
                }
                mobSpawnCosts.put(entry,0);
            });

            if(name.equals("default")) {
                minX = -30000000;
                minZ = -30000000;
                minY = 0;

                maxX = 30000000;
                maxZ = 30000000;
                maxY = 256;

                dimensionId=0;
            }else{
                Property minXBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "minXBoundary", -30000000, "minimum x for bounding box of this region.");
                minX = minXBoundaryProp.getInt();
                Property maxXBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "maxXBoundary", 30000000, "maximum x for bounding box of this region.");
                maxX = maxXBoundaryProp.getInt();

                Property minYBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "minYBoundary", 0, "minimum y for bounding box of this region.");
                minY = minYBoundaryProp.getInt();
                Property maxYBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "maxYBoundary", 256, "maximum y for bounding box of this region.");
                maxY = maxYBoundaryProp.getInt();

                Property minZBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "minZBoundary", -30000000, "minimum z for bounding box of this region.");
                minZ = minZBoundaryProp.getInt();
                Property maxZBoundaryProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "maxZBoundary", 30000000, "maximum z for bounding box of this region.");
                maxZ = maxZBoundaryProp.getInt();

                Property dimensionIdProp = regionConfig.get(Configuration.CATEGORY_GENERAL, "dimesionId",0,"What dimension this region exists in.");
                dimensionId = dimensionIdProp.getInt();
            }

        }finally {
            if(regionConfig.hasChanged()){
                regionConfig.save();
            }
        }
    }

    private static String[] generateDefaultSpawnCosts(){
        List<String> lines = Lists.newArrayList();
        for(EntityEntry entry : ForgeRegistries.ENTITIES.getValues()){
            if(EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                lines.add(entry.getName()+":0");
            }
        }
        return lines.toArray(new String[lines.size()]);
    }

    public boolean isPosInRegion(BlockPos position) {
        boolean inRegion =
                position.getX() >= minX
                && position.getX() <= maxX
                && position.getY() >= minY
                && position.getY() <= maxY
                && position.getZ() >= minZ
                && position.getZ() <= maxZ;
        return  inRegion;
    }

    public DifficultyModifier getModifierForMobById(String mobId, String change) {
        RegionMobConfig mobConfig = getConfigForMob(mobId);
        return mobConfig.modifiers.getOrDefault(change,null);
    }

    public boolean doesNegativeDifficultyPreventSpawn() {
        return negativeDifficultyPreventsSpawn;
    }

    public int getThreshold() {
        return threshold;
    }

    public int determineDifficultyForSpawnEvent(SpawnEventDetails details){
        int difficulty = baseDifficulty;
        difficulty+=getMobBaseSpawnCost(details.entity);
        String mobId = EntityList.getEntityString(details.entity);
        RegionMobConfig mobConfig = getConfigForMob(mobId);
        for(DifficultyControl control : mobConfig.controls){
            difficulty+=control.getChangeForSpawn(details);
        }
        return difficulty;
    }

    public Map<String, Integer> determineDifficultyForFakedSpawnEvent(SpawnEventDetails spawnDetails){
        Map<String,Integer> details = Maps.newHashMap();
        details.put("MOB_BASE",getMobBaseSpawnCost(spawnDetails.entity));
        details.put("REGION_BASE",baseDifficulty);
        String mobId = EntityList.getEntityString(spawnDetails.entity);
        RegionMobConfig mobConfig = getConfigForMob(mobId);
        for(DifficultyControl control : mobConfig.controls){
            details.put(control.getIdentifier(),control.getChangeForSpawn(spawnDetails));
        }
        return details;
    }


    public void makeDifficultyChanges(EntityLiving entity, int determinedDifficulty, Random rand) {
        Map<String, Integer> thisSpawnModifiers = Maps.newHashMap();
        int initialDifficulty = determinedDifficulty;
        int failCount = 0;
        String mobId = EntityList.getEntityString(entity);
        RegionMobConfig mobConfig = getConfigForMob(mobId);
        while (determinedDifficulty > allowedMargin && failCount < maxFailCount) {
            DifficultyModifier pickedModifier = mobConfig.pickModifierFromList(rand);
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

        String log = "For spawn of " + EntityList.getEntityString(entity)
                + " in region "+ getName()
                + " with difficulty " + initialDifficulty + ", ("+determinedDifficulty+" remaining) decided to use: ";
        for (String modId : thisSpawnModifiers.keySet()) {
            int numToApply = thisSpawnModifiers.get(modId);
            mobConfig.modifiers.get(modId).handleSpawnEvent(numToApply, entity);
            log = log + modId + " " + numToApply + " times, ";
        }
        if(DifficultyManager.debugLogSpawns) {
            LOG.info(log);
        }
        MobNBTHandler.setChangeMap(entity,getName(),thisSpawnModifiers);
    }

    public int getDimensionId(){
        return dimensionId;
    }


    private class RegionMobConfig{

        private double[] cumulativeWeight;
        private String[] modifierKey;
        private double totalWeight;

        private final List<DifficultyControl> controls = Lists.newArrayList();
        private final Map<String,DifficultyModifier> modifiers = Maps.newHashMap();


        public  RegionMobConfig(Configuration config){
            for(DifficultyModifier modifier : DifficultyManager.buildModifiersFromConfig(config)){
                modifiers.put(modifier.getIdentifier(),modifier);
            }
            controls.addAll(DifficultyManager.buildControlsFromConfig(config));
            generateWeightMap();
        }

        private void generateWeightMap() {
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

        private DifficultyModifier pickModifierFromList(Random rand) {
            double weightToFind = rand.nextDouble() * totalWeight;
            for(int i=0;i<cumulativeWeight.length;i++){
                if(weightToFind<cumulativeWeight[i]){
                    return modifiers.get(modifierKey[i]);
                }
            }
            return null;
        }

    }


}
