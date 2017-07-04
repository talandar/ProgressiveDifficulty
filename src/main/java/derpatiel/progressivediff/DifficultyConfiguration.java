package derpatiel.progressivediff;

import derpatiel.progressivediff.controls.*;
import derpatiel.progressivediff.modifiers.*;
import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DifficultyConfiguration {
    //the config file itself from Forge
    public static Configuration config;

    //general
    public static boolean controlEnabled;
    public static int baseDifficulty;
    public static int allowedMargin;
    public static int maxFailCount;
    public static boolean negativeDifficultyPreventsSpawn;
    public static int threshold;
    public static boolean debugLogSpawns;

    public static void syncConfig(){
        try {
            config.load();
            DifficultyManager.clearModifiersAndControls();

            //load properties
            Property isDifficultyChangeEnabledProp = config.get(Configuration.CATEGORY_GENERAL,
                    "DifficultyControlEnabled",true,"Allow ProgressiveDifficulty to control difficulty of mob spawns.");
            controlEnabled = isDifficultyChangeEnabledProp.getBoolean();
            Property baseDifficultyProp = config.get(Configuration.CATEGORY_GENERAL,
                    "BaseDifficulty",0,"Base Difficulty before any modifiers are added. 0 is baseline vanilla.  If this is negative, mobs will be easier, Decreasing this has an effect of making the game ");
            baseDifficulty = baseDifficultyProp.getInt();
            Property allowedMarginProp = config.get(Configuration.CATEGORY_GENERAL,
                    "AllowedMargin",5,"If the difficulty of a mob is this close to the target, stop looking.  Larger values will cause more variance in mob difficulty, but smaller values may cause excessive computation attempting to find an exact match.");
            allowedMargin = Math.abs(allowedMarginProp.getInt());
            Property maxFailCountProp = config.get(Configuration.CATEGORY_GENERAL,
                    "MaxAllowedFailures",5,"Allow this many failures while trying to apply modifiers.  Higher values might cause modifier determination to take a long time, but allows closer control over difficulty.");
            maxFailCount = Math.abs(maxFailCountProp.getInt());
            Property thresholdProp = config.get(Configuration.CATEGORY_GENERAL,
                    "ModificationThresold",0,"Set a threshold that limits when difficulty modifiers will be applied.  Values significantly above 'AllowedMargin' would cause many mobs to be unmodified, but ones that are modified to be significantly modified.");
            threshold = thresholdProp.getInt();

            Property negativeDifficultyPreventsSpawnProp = config.get(Configuration.CATEGORY_GENERAL,
                    "PreventLowDifficultySpawns",true,"Spawns with a negative calculated difficulty for any reason (usually \"MobBaseDifficulty\"), will have a chance of not spawning at all.  The chance of it not spawning is equal to the negative difficulty as a percent.  (-50 has a 50/50 chance of spawning, -101 will never spawn)");
            negativeDifficultyPreventsSpawn=negativeDifficultyPreventsSpawnProp.getBoolean();

            Property debugLogSpawnsProp = config.get(Configuration.CATEGORY_GENERAL,
                    "debugSpawnDetails",false,"Send messages to the log detailing computed costs of mobs and which modifiers have been chosen for them.");
            debugLogSpawns = debugLogSpawnsProp.getBoolean();


            //controls
            DepthControl.readConfig(config);
            FromSpawnerControl.readConfig(config);
            AdditionalPlayersControl.readConfig(config);
            PlayerTimeInWorldControl.readConfig(config);
            DistanceFromSpawnControl.readConfig(config);
            //AchievementControl.readConfig(config);
            AllMobsKilledControl.readConfig(config);
            SpecificMobKilledControl.readConfig(config);
            BlocksBrokenControl.readConfig(config);

            //modifiers
            AddHealthModifier.readConfig(config);
            AddResistanceModifier.readConfig(config);
            AddStrengthModifier.readConfig(config);
            AddSpeedModifier.readConfig(config);
            CreeperChargeModifier.readConfig(config);
            PiercingModifier.readConfig(config);
            AddRegenerationModifier.readConfig(config);
            FieryModifier.readConfig(config);
            VampiricModifier.readConfig(config);
            SlowingGazeModifier.readConfig(config);
            HungryGazeModifier.readConfig(config);
            WeakGazeModifier.readConfig(config);
            FatigueGazeModifier.readConfig(config);

            EntityFilter.loadConfig(config);


            DifficultyManager.generateWeightMap();

        }catch(Exception e){
            //failed to read config!?
            LOG.error("FAILED TO READ CONFIG FOR ProgressiveDifficulty.  Message was: "+e.getMessage());
            StringWriter stream = new StringWriter();
            PrintWriter writer = new PrintWriter(stream);
            e.printStackTrace(writer);
            LOG.error(stream.toString());
        }finally{
            if(config.hasChanged()){
                config.save();
            }
        }
    }

}
