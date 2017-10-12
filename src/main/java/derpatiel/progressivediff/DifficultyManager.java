package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.controls.*;
import derpatiel.progressivediff.modifiers.*;
import derpatiel.progressivediff.util.LOG;
import net.minecraft.command.CommandTitle;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DifficultyManager {

    private static File rootProgDiffConfigDir=null;

    private static final List<Function<Configuration, List<DifficultyModifier>>> modifierConstructors = Lists.newArrayList();
    private static final List<Function<Configuration, List<DifficultyControl>>> controlConstructors = Lists.newArrayList();

    private static final Map<Integer,Map<EntityLiving,SpawnEventDetails>> eventsThisTickByDimension = Maps.newHashMap();

    private static final Map<Integer,TreeSet<Region>> regionsByDim = Maps.newHashMap();
    private static final Map<String,Region> regionsByName = Maps.newHashMap();
    private static Region defaultRegion;

    //general configs
    public static boolean enabled;
    public static boolean debugLogSpawns = false;
    public static boolean isBlacklistMode = true;
    public static final List<String> mobBlackWhiteList = Lists.newArrayList();
    public static boolean useIMobFilter = true;

    private static String[] defaultBlacklist = new String[]{
            "Donkey",
            "Mule",
            "Bat",
            "Pig",
            "Sheep",
            "Cow",
            "Chicken",
            "Squid",
            "Wolf",
            "MushroomCow",
            "SnowMan",
            "Ozelot",
            "VillagerGolem",
            "Horse",
            "Rabbit",
            "Llama",
            "Parrot",
            "Villager"
    };

    //TODO some kind of conversion/upgrade script for 1.x configs to 2.x configs

    public static void registerModifier(Function<Configuration, List<DifficultyModifier>> constructor) {
        modifierConstructors.add(constructor);
    }

    public static List<DifficultyModifier> buildModifiersFromConfig(Configuration config){
        List<DifficultyModifier> modifiers = Lists.newArrayList();
        for(Function<Configuration,List<DifficultyModifier>> constructor : modifierConstructors){
            modifiers.addAll(constructor.apply(config));
        }
        return modifiers;
    }
    public static List<DifficultyControl> buildControlsFromConfig(Configuration config){
        List<DifficultyControl> controls = Lists.newArrayList();
        for(Function<Configuration,List<DifficultyControl>> constructor : controlConstructors){
            controls.addAll(constructor.apply(config));
        }
        return controls;
    }

    public static void registerControl(Function<Configuration, List<DifficultyControl>> constructor) {
        controlConstructors.add(constructor);
    }

    public static void setBaseConfigDir(File modConfigurationDirectory) {
        rootProgDiffConfigDir = new File(modConfigurationDirectory, ProgressiveDifficulty.MODID);
        if (!rootProgDiffConfigDir.exists()) {
            rootProgDiffConfigDir.mkdirs();
        }
    }

    public static File getConfigDir(){
        return rootProgDiffConfigDir;
    }

    public static void syncConfig() {
        regionsByDim.clear();
        regionsByName.clear();
        if(!rootProgDiffConfigDir.exists()){
            rootProgDiffConfigDir.mkdirs();
        }
        File defaultConfigFile = new File(rootProgDiffConfigDir, ProgressiveDifficulty.MODID+".cfg");
        Configuration defaultConfig = new Configuration(defaultConfigFile);
        //read root config.
        try {
            defaultConfig.load();
            Property isDifficultyChangeEnabledProp = defaultConfig.get(Configuration.CATEGORY_GENERAL,
                    "DifficultyControlEnabled", true, "Allow ProgressiveDifficulty to control difficulty of mob spawns.");
            boolean controlEnabled = isDifficultyChangeEnabledProp.getBoolean();
            Property debugLogSpawnsProp = defaultConfig.get(Configuration.CATEGORY_GENERAL,
                    "debugSpawnDetails", false, "Send messages to the log detailing computed costs of mobs and which modifiers have been chosen for them.");
            debugLogSpawns = debugLogSpawnsProp.getBoolean();

            Property blacklistProp = defaultConfig.get(Configuration.CATEGORY_GENERAL,"BlacklistMode",true,"All mobs are modified, except those that are in the blacklist.  If set to false, only those in the mob list are modified.  Boss-type mobs are never modified.");
            isBlacklistMode = blacklistProp.getBoolean();

            Property mobListProp = defaultConfig.get(Configuration.CATEGORY_GENERAL,"MobList",defaultBlacklist,"List of mobs, either blacklist or whitelisted for modification by this mod.  See BlacklistMode.");
            mobBlackWhiteList.clear();
            mobBlackWhiteList.addAll(Sets.newHashSet(mobListProp.getStringList()));

            Property iMobFilterProp = defaultConfig.get(Configuration.CATEGORY_GENERAL,"UseIMobFilter",true,"Only modify creatures that implement the IMob interface.  You almost certainly want this, unless a modded mob is not being modified.  This allows easy filtering of passive mobs.  If you set this to false, make sure you add all passive mobs to the blacklist!");
            useIMobFilter = iMobFilterProp.getBoolean();

            enabled=controlEnabled;
            if (!controlEnabled) {
                //mod is effectively disabled.
                return;
            }
        } catch (Exception e) {
            //failed to read config!?
            LOG.error("FAILED TO READ BASE CONFIG FOR ProgressiveDifficulty.  Message was: " + e.getMessage());
            StringWriter stream = new StringWriter();
            PrintWriter writer = new PrintWriter(stream);
            e.printStackTrace(writer);
            LOG.error(stream.toString());
        } finally {
            if (defaultConfig.hasChanged()) {
                defaultConfig.save();
            }
        }

        List<String> regionNames = Lists.newArrayList();
        File[] subFiles = rootProgDiffConfigDir.listFiles();
        if(subFiles!=null){
            regionNames.addAll(Arrays.stream(subFiles)
                    .filter(path->path.isDirectory())
                    .map(file->file.getName())
                    .collect(Collectors.toList()));
        }

        if(!regionNames.contains("default")){
            regionNames.add("default");
        }
        for(String regionName : regionNames){
            Region region = new Region(regionName);
            region.readConfig();
            int dimId = region.getDimensionId();
            regionsByDim.computeIfAbsent(dimId, key -> new TreeSet<Region>()).add(region);
            regionsByName.put(regionName,region);
        }
        defaultRegion = regionsByName.get("default");
    }

    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn checkSpawnEvent) {
        if(enabled) {
            SpawnEventDetails details = new SpawnEventDetails();
            if (shouldModifyEntity(checkSpawnEvent.getEntityLiving())) {
                details.entity = (EntityLiving) checkSpawnEvent.getEntityLiving();
                details.spawnEvent = checkSpawnEvent;
                details.fromSpawner = checkSpawnEvent.isSpawner();
                eventsThisTickByDimension.computeIfAbsent(details.entity.world.provider.getDimension(), thing -> new HashMap<>()).put(details.entity, details);
            }
        }
    }

    public static void onWorldTick(int dimensionId){
        eventsThisTickByDimension.computeIfAbsent(dimensionId, thing -> new HashMap<>()).clear();
    }

    public static void registerBaseModControlsAndModifiers(){
        //controls
        registerControl(DepthControl.getFromConfig);
        registerControl(FromSpawnerControl.getFromConfig);
        registerControl(AdditionalPlayersControl.getFromConfig);
        registerControl(PlayerTimeInWorldControl.getFromConfig);
        registerControl(DistanceFromSpawnControl.getFromConfig);
        registerControl(AdvancementControl.getFromConfig);
        registerControl(AllMobsKilledControl.getFromConfig);
        registerControl(SpecificMobKilledControl.getFromConfig);
        registerControl(BlocksBrokenControl.getFromConfig);

        //modifiers
        registerModifier(AddHealthModifier.getFromConfig);
        registerModifier(AddResistanceModifier.getFromConfig);
        registerModifier(AddDamageModifier.getFromConfig);
        registerModifier(AddSpeedModifier.getFromConfig);
        registerModifier(CreeperChargeModifier.getFromConfig);
        registerModifier(PiercingModifier.getFromConfig);
        registerModifier(AddRegenerationModifier.getFromConfig);
        registerModifier(FieryModifier.getFromConfig);
        registerModifier(VampiricModifier.getFromConfig);
        registerModifier(SlowingGazeModifier.getFromConfig);
        registerModifier(HungryGazeModifier.getFromConfig);
        registerModifier(WeakGazeModifier.getFromConfig);
        registerModifier(FatigueGazeModifier.getFromConfig);
        registerModifier(OnHitEffectModifier.getFromConfig);
        registerModifier(PotionCloudModifier.getFromConfig);
    }

    public static boolean shouldModifyEntity(EntityLivingBase entity){
        if(entity==null || !entity.isNonBoss() || entity instanceof EntityPlayer)
            return false;
        if(useIMobFilter && !(entity instanceof IMob))
            return false;
        if(mobBlackWhiteList.contains(EntityList.getEntityString(entity))){
            return !isBlacklistMode;
        }
        return isBlacklistMode;
    }

    public static void onJoinWorldEvent(EntityJoinWorldEvent joinWorldEvent) {
        //we actually got to this step, so lets do something with it.
        //note: we check this conversion up in the caller of this class.  should be safe.
        EntityLiving mobToSpawn = (EntityLiving) joinWorldEvent.getEntity();
        SpawnEventDetails details = eventsThisTickByDimension.computeIfAbsent(joinWorldEvent.getEntity().world.provider.getDimension(), thing -> new HashMap<>()).get(mobToSpawn);
        if (details != null) {
            //find region
            int dimension = joinWorldEvent.getWorld().provider.getDimension();
            Region homeRegion = getRegionForPosition(dimension,joinWorldEvent.getEntity().getPosition());
            int difficulty = homeRegion.determineDifficultyForSpawnEvent(details);
            if(difficulty<0 && homeRegion.doesNegativeDifficultyPreventSpawn()){
                int chance = joinWorldEvent.getWorld().rand.nextInt(100);
                if(Math.abs(difficulty)>=chance){
                    if(mobToSpawn.isBeingRidden() || mobToSpawn.isRiding()){
                        LOG.info("RIDER!: "+mobToSpawn.getClass());
                    }
                    //also remove anything riding or being ridden (zombies on chickens, skeletons on spiders)
                    if(mobToSpawn.isBeingRidden()){
                        for(Entity passenger : mobToSpawn.getPassengers()) {
                            passenger.setDropItemsWhenDead(false);
                            passenger.setDead();
                        }
                    } else if(mobToSpawn.isRiding()){
                        Entity mount = mobToSpawn.getRidingEntity();
                        mount.setDropItemsWhenDead(false);
                        mount.setDead();
                    }
                    joinWorldEvent.setCanceled(true);
                    return;
                }
            }
            if(difficulty>=homeRegion.getThreshold()) {
                homeRegion.makeDifficultyChanges(mobToSpawn, difficulty, joinWorldEvent.getWorld().rand);
            }
        }
    }

    public static Region getRegionForPosition(int dimension, BlockPos pos){
        TreeSet<Region> regionsInDim = regionsByDim.get(dimension);
        if(regionsInDim==null || regionsInDim.isEmpty()){
            return defaultRegion;
        }else {
            for (Region region : regionsInDim) {
                if (region.isPosInRegion(pos)) {
                    return region;
                }
            }
        }
        return defaultRegion;
    }

    public static Region getRegionByName(String regionName) {
        return regionsByName.getOrDefault(regionName,regionsByName.get("default"));
    }
}

