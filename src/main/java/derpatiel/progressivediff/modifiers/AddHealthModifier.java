package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyConfiguration;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddHealthModifier extends DifficultyModifier {

    private int minAddedHealth;
    private int maxAddedHealth;
    private int diffCostPerHealth;

    public AddHealthModifier(int minAddedHealth, int maxAddedHealth, int diffCostPerHealth){
        this.minAddedHealth = minAddedHealth;
        this.maxAddedHealth = maxAddedHealth;
        this.diffCostPerHealth = diffCostPerHealth;
    }


    @Override
    public int getMinChange() {
        return minAddedHealth * diffCostPerHealth;
    }

    @Override
    public int getMaxChange() {
        return maxAddedHealth * diffCostPerHealth;
    }

    @Override
    public void makeChange(int changeValue, EntityLivingBase entity) {
        int addedHealth = changeValue/diffCostPerHealth;
        IAttributeInstance maxHealthAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        maxHealthAttribute.setBaseValue(maxHealthAttribute.getBaseValue() + addedHealth);
        entity.setHealth(entity.getMaxHealth());

    }

    public static void readConfig(Configuration config){
        Property addHealthModifierEnabledProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "EnableAddHealthModifier",true,"Enable the add health modifier.  This adds health to mobs on spawn.");
        boolean addHealthModEnabled = addHealthModifierEnabledProp.getBoolean();
        Property healthModifierMinAddedHealthProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "HealthModifierMinAddedHealth",1,"Minimum amount of health added to the mob when this is triggered.");
        int minAddedHealth = healthModifierMinAddedHealthProp.getInt();
        Property healthModifierMaxAddedHealthProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "HealthModifierMaxAddedHealth",10,"Maximum amount of health added to the mob when this is triggered.");
        int maxAddedHealth = healthModifierMaxAddedHealthProp.getInt();
        Property difficultyCostPerHealthProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "DifficultyCostPerHealth",1,"Cost of each extra point of health.  Larger values will mean more difficult mobs will have less health, while smaller values will cause more difficult mobs to have lots of extra health.");
        int diffCostPerHealth = difficultyCostPerHealthProp.getInt();
        if(addHealthModEnabled && maxAddedHealth>0 && maxAddedHealth>minAddedHealth && diffCostPerHealth>0) {
            DifficultyManager.addDifficultyModifier(new AddHealthModifier(minAddedHealth, maxAddedHealth, diffCostPerHealth));
        }
    }
}
