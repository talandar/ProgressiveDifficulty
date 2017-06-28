package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * Created by Jim on 4/30/2017.
 */
public class FieryModifier extends DifficultyModifier {

    public static final String IDENTIFIER = "MOD_FIRE_ASPECT";

    private static int costForFireAspect;
    private static double selectionWeight;

    public FieryModifier(){
    }

    @Override
    public int getMaxInstances() {
        return 1;
    }

    @Override
    public void makeChange(int numInstances, EntityLiving entity, boolean isUpkeep) {
        //NOOP - event driven
    }

    public static void handleDamageEvent(LivingAttackEvent event){
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

    public static void readConfig(Configuration config){
        Property modifierEnabledProp = config.get(IDENTIFIER,
                "EnableFireAspectModifier",true,"Enable the fire aspect modifier.  This allows mobs to do damage that ignites the player.");
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "FireAspectModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",5,"Cost of the modifier.");
        costForFireAspect = difficultyCostProp.getInt();
        if(modifierEnabled && costForFireAspect>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new FieryModifier());
        }
    }
}
