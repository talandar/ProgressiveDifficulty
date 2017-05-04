package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyConfiguration;
import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 5/1/2017.
 */
public class FromSpawnerControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_SPAWNER";

    private int addedDifficulty;

    public FromSpawnerControl(int addedDifficulty){
        this.addedDifficulty = addedDifficulty;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        return details.fromSpawner ? addedDifficulty : 0;
    }

    public static void readConfig(Configuration config) {
        Property addedDifficultyFromSpawnerProp = config.get(IDENTIFIER,
                "SpawnerAddedDifficulty", 10,"Difficulty added to a mob if it is from a spawner.");
        int addedDifficultyIfSpawner = addedDifficultyFromSpawnerProp.getInt();
        if(addedDifficultyIfSpawner!=0){
            DifficultyManager.addDifficultyControl(new FromSpawnerControl(addedDifficultyIfSpawner));
        }
    }
}
