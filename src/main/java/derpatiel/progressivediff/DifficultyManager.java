package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.modifiers.AddHealthModifier;
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

    public static int determineDifficultyForSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        int difficulty = baseDifficulty;

        for(DifficultyControl control : controls){
            difficulty = control.getChangeForEvent(event,difficulty);
        }

        return difficulty;
    }


    public static void makeDifficultyChanges(LivingSpawnEvent event, int determinedDifficulty){
        if (determinedDifficulty > 100){
            //for now, just put it all on the first one
            modifiers.get(0).makeChange(determinedDifficulty-100,event.getEntityLiving());
        }

    }
}
