package derpatiel.progressivediff;

import derpatiel.progressivediff.controls.DepthControl;
import derpatiel.progressivediff.controls.FromSpawnerControl;
import derpatiel.progressivediff.modifiers.AddHealthModifier;
import derpatiel.progressivediff.modifiers.AddResistanceModifier;
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

    public static void syncConfig(){
        try {
            config.load();
            DifficultyManager.clearModifiersAndControls();

            //load properties
            Property isDifficultyChangeEnabledProp = config.get(Configuration.CATEGORY_GENERAL,
                    "DifficultyControlEnabled",true,"Allow ProgressiveDifficulty to control difficulty of mob spawns.");
            controlEnabled = isDifficultyChangeEnabledProp.getBoolean();
            Property baseDifficultyProp = config.get(Configuration.CATEGORY_GENERAL,
                    "BaseDifficulty",100,"Base Difficulty before any modifiers are added. 100 is baseline vanilla.");
            baseDifficulty = baseDifficultyProp.getInt();


            //controls
            DepthControl.readConfig(config);
            FromSpawnerControl.readConfig(config);

            //modifiers
            AddHealthModifier.readConfig(config);
            AddResistanceModifier.readConfig(config);

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
