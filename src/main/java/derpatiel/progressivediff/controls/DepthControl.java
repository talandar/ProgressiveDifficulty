package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyConfiguration;
import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class DepthControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_DEPTH";

    private double addedPerBlock;

    public DepthControl(double addedPerBlock){
        this.addedPerBlock = addedPerBlock;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        int depth = 64-(int)details.entity.getPosition().getY();
        if(depth<=0){
            return 0;
        }else{
            return (int)(depth * addedPerBlock);
        }
    }

    public static void readConfig(Configuration config) {
        Property doesDepthControlDifficulty = config.get(IDENTIFIER,
                "DepthEffectsDifficulty", true, "Depth of spawn changes the difficulty of a mob.  Lower Y value means higher difficulty.  Y>=64 (ocean level and above) is unaffected.");
        boolean depthControlsDifficulty = doesDepthControlDifficulty.getBoolean();
        Property addedDifficultyPerBlockDepthProp = config.get(IDENTIFIER,
                "DepthAddedDifficulty", 0.2d, "Difficulty added to a mob for each level below Y=64 it spawns at.");
        double addedDifficultyPerBlockDepth = addedDifficultyPerBlockDepthProp.getDouble();
        if (depthControlsDifficulty && addedDifficultyPerBlockDepth > 0){
            DifficultyManager.addDifficultyControl(new DepthControl(addedDifficultyPerBlockDepth));
        }
    }
}
