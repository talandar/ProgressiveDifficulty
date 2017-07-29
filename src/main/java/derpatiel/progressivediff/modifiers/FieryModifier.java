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
public class FieryModifier extends DifficultyModifier {

    public static final String IDENTIFIER = "MOD_FIRE_ASPECT";

    private int costForFireAspect;
    private double selectionWeight;

    public FieryModifier(int costForFireAspect, double selectionWeight){
        this.costForFireAspect = costForFireAspect;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return 1;
    }

    @Override
    public void handleDamageEvent(LivingAttackEvent event){
        event.getEntity().setFire(3);
        event.getSource().setFireDamage();
    }

    @Override
    public int costPerChange() {
        return costForFireAspect;
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
        Property modifierEnabledProp = config.get(IDENTIFIER,
                "EnableFireAspectModifier",true,"Enable the fire aspect modifier.  This allows mobs to do damage that ignites the player.");
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "FireAspectModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",5,"Cost of the modifier.");
        int costForFireAspect = difficultyCostProp.getInt();
        if(modifierEnabled && costForFireAspect>0 && selectionWeight>0) {
            returns.add(new FieryModifier(costForFireAspect,selectionWeight));
        }
        return returns;
    };
}
