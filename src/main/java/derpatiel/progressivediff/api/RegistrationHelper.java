package derpatiel.progressivediff.api;

import derpatiel.progressivediff.DifficultyManager;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.function.Function;

public class RegistrationHelper {

    public static void registerControl(Function<Configuration,List<DifficultyControl>> controlConstructionFunction){
        DifficultyManager.registerControl(controlConstructionFunction);
    }

    public static void registerModifier(Function<Configuration,List<DifficultyModifier>> modifierConstructionFunction) {
        DifficultyManager.registerModifier(modifierConstructionFunction);
    }
}
