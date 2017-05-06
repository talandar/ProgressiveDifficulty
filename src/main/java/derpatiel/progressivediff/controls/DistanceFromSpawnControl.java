package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 5/1/2017.
 */
public class DistanceFromSpawnControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_SPAWN_DISTANCE";

    private int addedDifficultyPerHundredBlocks;

    public DistanceFromSpawnControl(int addedDifficultyPerHundredBlocks){
        this.addedDifficultyPerHundredBlocks = addedDifficultyPerHundredBlocks;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        BlockPos thisSpawnLoc = details.entity.getPosition();
        BlockPos spawnPoint = details.entity.getEntityWorld().getSpawnPoint();
        double distanceFromSpawn = thisSpawnLoc.getDistance(spawnPoint.getX(),spawnPoint.getY(),spawnPoint.getZ());
        double hundredsOfBlocksFromSpawn = distanceFromSpawn/100.0d;
        return (int)(hundredsOfBlocksFromSpawn * addedDifficultyPerHundredBlocks);

    }

    public static void readConfig(Configuration config) {
        Property distanceFromSpawnAddsProp = config.get(IDENTIFIER,
                "DistanceFromSpawnAddsDifficulty", true,"Distance from the spawn of the world adds difficulty to mobs.");
        boolean enabled = distanceFromSpawnAddsProp.getBoolean();
        Property addedDifficultyPerHundredBlocksProp = config.get(IDENTIFIER,
                "AddedDifficultyPerHundredBlocks",1,"Add this much difficulty per hundred blocks away from spawn.");
        int addedDifficultyPerHundredBlocks = addedDifficultyPerHundredBlocksProp.getInt();
        if(enabled && addedDifficultyPerHundredBlocks>0){
            DifficultyManager.addDifficultyControl(new DistanceFromSpawnControl(addedDifficultyPerHundredBlocks));
        }
    }
}
