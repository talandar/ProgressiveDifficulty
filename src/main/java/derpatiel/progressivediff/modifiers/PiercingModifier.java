package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.api.DifficultyModifier;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Jim on 4/30/2017.
 */
public class PiercingModifier extends DifficultyModifier {

    public static final String IDENTIFIER = "MOD_PIERCING";

    private int costForPiercing;
    private double selectionWeight;

    public PiercingModifier(int costForPiercing, double selectionWeight){
        this.costForPiercing = costForPiercing;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return 1;
    }

    @Override
    public void handleDamageEvent(LivingAttackEvent event){
        event.getSource().setDamageBypassesArmor();
        event.getSource().setDamageIsAbsolute();
    }

    @Override
    public int costPerChange() {
        return costForPiercing;
    }

    @Override
    public double getWeight() {
        return selectionWeight;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration,List<DifficultyModifier>> getFromConfig = config -> {
        List<DifficultyModifier> returns = Lists.newArrayList();
        Property piercingModifierEnabledProp = config.get(IDENTIFIER,
                "EnablePiercingModifier",true,"Enable the piercing modifier.  This allows mobs to do damage that ignores armor.");
        boolean piercingModifierEnabled = piercingModifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "PiercingModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",5,"Cost of the modifier.  Low values will cause mobs to ignore armor more often.");
        int costForPiercing = difficultyCostProp.getInt();
        if(piercingModifierEnabled && costForPiercing>0 && selectionWeight>0) {
            returns.add(new PiercingModifier(costForPiercing,selectionWeight));
        }
        return returns;
    };
}
