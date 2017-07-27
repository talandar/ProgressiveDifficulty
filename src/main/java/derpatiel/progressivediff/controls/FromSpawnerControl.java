package derpatiel.progressivediff.controls;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

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

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration,List<DifficultyControl>> getFromConfig = config -> {
        List<DifficultyControl> returns = Lists.newArrayList();
        Property addedDifficultyFromSpawnerProp = config.get(IDENTIFIER,
                "SpawnerAddedDifficulty", 10,"Difficulty added to a mob if it is from a spawner.");
        int addedDifficultyIfSpawner = addedDifficultyFromSpawnerProp.getInt();
        if(addedDifficultyIfSpawner!=0){
            returns.add(new FromSpawnerControl(addedDifficultyIfSpawner));
        }
        return returns;
    };
}
