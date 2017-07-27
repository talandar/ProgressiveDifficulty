package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.api.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddDamageModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_EXTRA_DAMAGE";

    private int maxExtraDamage;
    private int diffCostPerDamage;
    private double selectionWeight;

    public AddDamageModifier(int maxExtraDamage, int diffCostPerDamage, double selectionWeight) {
        this.maxExtraDamage = maxExtraDamage;
        this.diffCostPerDamage = diffCostPerDamage;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public boolean validForEntity(EntityLiving entity) {
        return !(entity instanceof IRangedAttackMob);
    }

    @Override
    public int getMaxInstances() {
        return maxExtraDamage;
    }

    @Override
    public void handleSpawnEvent(int numInstances, EntityLiving entity) {
        IAttributeInstance maxDamageAttribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        if(maxDamageAttribute!=null) {
            maxDamageAttribute.setBaseValue(maxDamageAttribute.getBaseValue() + numInstances);
        }
    }

    @Override
    public int costPerChange() {
        return diffCostPerDamage;
    }

    @Override
    public double getWeight() {
        return selectionWeight;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration, List<DifficultyModifier>> getFromConfig = config -> {

        Property addStrengthModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddDamageModifier", true, "Enable the add damage modifier.  This adds damage points (half-hearts) to the mobs damage.");
        boolean addStrengthEnabled = addStrengthModifierEnabledProp.getBoolean();
        Property strengthLevelMaxLevelProp = config.get(IDENTIFIER,
                "DamageModifierMaxLevel", 5, "Maximum extra damage added to the mob when this is triggered.");
        int maxStrengthLevel = strengthLevelMaxLevelProp.getInt();
        Property difficultyCostPerStrengthLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerDamage", 6, "Cost of each damage point.");
        int diffCostPerLevelStrength = difficultyCostPerStrengthLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "DamageModifierWeight", 1.0d, "Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if (addStrengthEnabled && maxStrengthLevel > 0 && diffCostPerLevelStrength > 0 && selectionWeight > 0) {
            return Lists.newArrayList(new AddDamageModifier(maxStrengthLevel, diffCostPerLevelStrength, selectionWeight));
        }
        return Lists.newArrayList();
    };

}
