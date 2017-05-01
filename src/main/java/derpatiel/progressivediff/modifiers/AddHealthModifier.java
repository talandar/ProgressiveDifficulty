package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyModifer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddHealthModifier extends DifficultyModifer {

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
        return 0;
    }

    @Override
    public int getMaxChange() {
        return 1000;
    }

    @Override
    public void makeChange(int changeValue, EntityLivingBase entity) {
        int addedHealth = changeValue/diffCostPerHealth;
        IAttributeInstance maxHealthAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        maxHealthAttribute.setBaseValue(maxHealthAttribute.getBaseValue() + addedHealth);
        entity.setHealth(entity.getMaxHealth());

    }
}
