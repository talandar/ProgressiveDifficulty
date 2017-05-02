package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyConfiguration;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddResistanceModifier extends DifficultyModifier {

    private int minResistanceLevel;
    private int maxResistanceLevel;
    private int diffCostPerLevelResistance;

    public AddResistanceModifier(int maxResistanceLevel, int diffCostPerLevelResistance){
        this.minResistanceLevel = 1;
        this.maxResistanceLevel = maxResistanceLevel;
        this.diffCostPerLevelResistance = diffCostPerLevelResistance;
    }


    @Override
    public int getMinChange() {
        return minResistanceLevel * diffCostPerLevelResistance;
    }

    @Override
    public int getMaxChange() {
        return maxResistanceLevel * diffCostPerLevelResistance;
    }

    @Override
    public void makeChange(int changeValue, EntityLivingBase entity) {
        int addLevel = changeValue/diffCostPerLevelResistance;
        entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,Integer.MAX_VALUE,addLevel,false,true));

    }

    public static void readConfig(Configuration config) {
        Property addResistanceModifierEnabledProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "EnableAddResistanceModifier",true,"Enable the add resistance modifier.  This adds the resistance potion effect to mobs on spawn.");
        boolean addResistanceEnabled = addResistanceModifierEnabledProp.getBoolean();
        Property resistanceLevelMaxLevelProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "ResistanceModifierMaxLevel",2,"Maximum resistance level added to the mob when this is triggered.  Remember, each resistance level is a flat 20% damage reduction.  Level 5+ will make the mob invincible.");
        int maxResistLevel = resistanceLevelMaxLevelProp.getInt();
        Property difficultyCostPerResistanceLevelProp = config.get(DifficultyConfiguration.CATEGORY_MODIFIERS,
                "DifficultyCostPerResistLevel",10,"Cost of each level of resistance.");
        int diffCostPerResistLevel = difficultyCostPerResistanceLevelProp.getInt();
        if(addResistanceEnabled && maxResistLevel>0 && diffCostPerResistLevel>0) {
            DifficultyManager.addDifficultyModifier(new AddResistanceModifier(maxResistLevel, diffCostPerResistLevel));
        }


    }
}
