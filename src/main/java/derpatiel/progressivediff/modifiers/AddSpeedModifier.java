package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddSpeedModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_SPEED";

    private static int maxSpeedLevel;
    private static int diffCostPerLevelSpeed;
    private static double selectionWeight;

    public AddSpeedModifier(){
    }

    @Override
    public int getMaxInstances() {
        return maxSpeedLevel;
    }

    @Override
    public void makeChange(int numChanges, EntityLivingBase entity) {
        entity.addPotionEffect(new PotionEffect(MobEffects.SPEED,Integer.MAX_VALUE,numChanges,false,true));

    }

    @Override
    public int costPerChange() {
        return diffCostPerLevelSpeed;
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
        Property addSpeedModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddSpeedModifier",true,"Enable the add Speed modifier.  This adds the Speed potion effect to mobs on spawn.");
        boolean addSpeedEnabled = addSpeedModifierEnabledProp.getBoolean();
        Property SpeedLevelMaxLevelProp = config.get(IDENTIFIER,
                "SpeedModifierMaxLevel",3,"Maximum Speed level added to the mob when this is triggered.  Each Speed level increases walking speed by 20%.");
        maxSpeedLevel = SpeedLevelMaxLevelProp.getInt();
        Property difficultyCostPerSpeedLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerSpeedLevel",10,"Cost of each level of Speed.");
        diffCostPerLevelSpeed = difficultyCostPerSpeedLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "SpeedModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(addSpeedEnabled && maxSpeedLevel>0 && diffCostPerLevelSpeed>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new AddSpeedModifier());
        }


    }
}
