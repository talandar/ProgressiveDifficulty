package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class PiercingModifier extends DifficultyModifier {

    public static final String IDENTIFIER = "MOD_PIERCING";

    private static int costForPiercing;
    private static double selectionWeight;

    public PiercingModifier(){
    }

    @Override
    public int getMaxInstances() {
        return 1;
    }

    @Override
    public void makeChange(int numInstances, EntityLiving entity, boolean isUpkeep) {
        //NOOP
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

    public static void readConfig(Configuration config){
        Property piercingModifierEnabledProp = config.get(IDENTIFIER,
                "EnablePiercingModifier",true,"Enable the piercing modifier.  This allows mobs to do damage that ignores armor.");
        boolean piercingModifierEnabled = piercingModifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "PiercingModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",5,"Cost of the modifier.  Low values will cause mobs to ignore armor more often.");
        costForPiercing = difficultyCostProp.getInt();
        if(piercingModifierEnabled && costForPiercing>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new PiercingModifier());
        }
    }
}
