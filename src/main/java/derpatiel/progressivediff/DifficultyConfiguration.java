package derpatiel.progressivediff;

import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class DifficultyConfiguration {
    //the config file itself from Forge
    public static Configuration config;

    public static final String CATEGORY_MODIFIERS = "difficultyModifiers";
    public static final String CATEGORY_CONTROLS = "difficultyControls";

    //lots and lots of static variables here
    public static boolean controlEnabled;
    public static boolean depthControlsDifficulty;
    public static double addedDifficultyPerBlockDepth;

    public static void syncConfig(){
        try {
            config.load();

            //load properties
            Property isDifficultyChangeEnabledProp = config.get(Configuration.CATEGORY_GENERAL,
                    "DifficultyControlEnabled",true,"Allow ProgressiveDifficulty to control difficulty of mob spawns.");
            controlEnabled = isDifficultyChangeEnabledProp.getBoolean();

            Property doesDepthControlDifficulty = config.get(CATEGORY_CONTROLS,
                    "DepthEffectsDifficulty",true,"Depth of spawn changes the difficulty of a mob.  Lower Y value means higher difficulty.  Y>=64 (ocean level and above) is unaffected.");
            depthControlsDifficulty = doesDepthControlDifficulty.getBoolean();

            Property addeDifficultyPerBlockDepthProp = config.get(CATEGORY_CONTROLS,
                    "DepthAddedDifficulty",1.0d,"Difficulty added to a mob for each level below Y=64 it spawns at.");
            addedDifficultyPerBlockDepth = addeDifficultyPerBlockDepthProp.getDouble();

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
