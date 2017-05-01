package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import org.apache.commons.lang3.builder.Diff;

import java.util.List;

import static derpatiel.progressivediff.DifficultyConfiguration.*;

public class DifficultyManager {

    private static final List<DifficultyControl> controls = Lists.newArrayList();
    private static final List<DifficultyModifer> modifiers = Lists.newArrayList();

    public static void addDifficultyControl(DifficultyControl control){
        controls.add(control);
    }
    public static void addDifficultyModifier(DifficultyModifer modifier){
        modifiers.add(modifier);
    }

    public static void clearModifiersAndControls(){
        controls.clear();
        modifiers.clear();
    }

    public static int determineDifficultyForSpawnEvent(LivingSpawnEvent event){
        int difficulty = baseDifficulty;

        return difficulty;
    }


    public static void makeDifficultyChanges(LivingSpawnEvent event, int determinedDifficulty){

    }
}
