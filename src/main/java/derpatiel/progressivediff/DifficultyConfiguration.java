package derpatiel.progressivediff;

import derpatiel.progressivediff.controls.*;
import derpatiel.progressivediff.modifiers.*;
import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class DifficultyConfiguration {
    //the config file itself from Forge
    public static Configuration config;

    public static final String CATEGORY_MODIFIERS = "difficulty_modifiers";
    public static final String CATEGORY_CONTROLS = "difficulty_controls";

    //lots and lots of static variables here

    //general
    public static boolean controlEnabled;
    public static int baseDifficulty;
    public static int allowedMargin;
    public static int maxFailCount;

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


            //controls
            DepthControl.readConfig(config);
            FromSpawnerControl.readConfig(config);
            AdditionalPlayersControl.readConfig(config);
            PlayerTimeInWorldControl.readConfig(config);
            DistanceFromSpawnControl.readConfig(config);
            AchievementControl.readConfig(config);

            //modifiers
            AddHealthModifier.readConfig(config);
            AddResistanceModifier.readConfig(config);
            AddStrengthModifier.readConfig(config);
            AddSpeedModifier.readConfig(config);
            CreeperChargeModifier.readConfig(config);

            EntityFilter.loadConfig(config);


            DifficultyManager.generateWeightMap();

        }catch(Exception e){
            //failed to read config!?
            LOG.error("FAILED TO READ CONFIG FOR ProgressiveDifficulty!");
        }finally{
            if(config.hasChanged()){
                config.save();
            }
        }
    }

}
