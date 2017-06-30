package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddHealthModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_HEALTH";

    private int maxAddedHealth;
    private int diffCostPerHealth;
    private double selectionWeight;

    public AddHealthModifier(int maxAddedHealth, int diffCostPerHealth, double selectionWeight){
        this.maxAddedHealth = maxAddedHealth;
        this.diffCostPerHealth = diffCostPerHealth;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxAddedHealth;
    }

    @Override
    public void handleSpawnEvent(int numInstances, EntityLiving entity) {
        IAttributeInstance maxHealthAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        maxHealthAttribute.setBaseValue(maxHealthAttribute.getBaseValue() + numInstances);
        entity.setHealth(entity.getMaxHealth());
    }

    @Override
    public int costPerChange() {
        return diffCostPerHealth;
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
        Property addHealthModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddHealthModifier",true,"Enable the add health modifier.  This adds health to mobs on spawn.");
        boolean addHealthModEnabled = addHealthModifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "HealthModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        Property healthModifierMaxAddedHealthProp = config.get(IDENTIFIER,
                "HealthModifierMaxAddedHealth",20,"Maximum amount of health added to the mob.");
        int maxAddedHealth = healthModifierMaxAddedHealthProp.getInt();
        Property difficultyCostPerHealthProp = config.get(IDENTIFIER,
                "DifficultyCostPerHealth",5,"Cost of each extra point of health.  Larger values will mean more difficult mobs will have less health, while smaller values will cause more difficult mobs to have lots of extra health.");
        int diffCostPerHealth = difficultyCostPerHealthProp.getInt();
        if(addHealthModEnabled && maxAddedHealth>0 && diffCostPerHealth>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new AddHealthModifier(maxAddedHealth,diffCostPerHealth,selectionWeight));
        }
    }
}
