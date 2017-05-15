package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddStrengthModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_STRENGTH";

    private static int maxStrengthLevel;
    private static int diffCostPerLevelStrength;
    private static double selectionWeight;

    public AddStrengthModifier(){
    }

    @Override
    public boolean validForEntity(EntityLiving entity) {
        return !(entity instanceof IRangedAttackMob);
    }

    @Override
    public int getMaxInstances() {
        return maxStrengthLevel;
    }

    @Override
    public void makeChange(int numChanges, EntityLivingBase entity) {
        entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,Integer.MAX_VALUE,numChanges,false,true));

    }

    @Override
    public int costPerChange() {
        return diffCostPerLevelStrength;
    }

    @Override
    public double getWeight() {
        return selectionWeight;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static void readConfig(Configuration config) {
        Property addStrengthModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddStrengthModifier",true,"Enable the add strength modifier.  This adds the strength potion effect to mobs on spawn.");
        boolean addStrengthEnabled = addStrengthModifierEnabledProp.getBoolean();
        Property strengthLevelMaxLevelProp = config.get(IDENTIFIER,
                "StrengthModifierMaxLevel",3,"Maximum strength level added to the mob when this is triggered.  Each strength level is 1.5 hearts of extra damage per attack.");
        maxStrengthLevel = strengthLevelMaxLevelProp.getInt();
        Property difficultyCostPerStrengthLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerStrengthLevel",15,"Cost of each level of strength.");
        diffCostPerLevelStrength = difficultyCostPerStrengthLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "StrengthModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(addStrengthEnabled && maxStrengthLevel>0 && diffCostPerLevelStrength>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new AddStrengthModifier());
        }


    }
}
