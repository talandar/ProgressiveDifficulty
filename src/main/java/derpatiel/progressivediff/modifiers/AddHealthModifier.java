package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

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
}
