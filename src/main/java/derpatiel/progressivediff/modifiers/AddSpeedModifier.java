package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.MobUpkeepController;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddSpeedModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_SPEED";

    private int maxSpeedLevel;
    private int diffCostPerLevelSpeed;
    private double selectionWeight;

    public AddSpeedModifier(int maxSpeedLevel, int diffCostPerLevelSpeed, double selectionWeight){
        this.maxSpeedLevel = maxSpeedLevel;
        this.diffCostPerLevelSpeed = diffCostPerLevelSpeed;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxSpeedLevel;
    }

    @Override
    public void handleUpkeepEvent(int numChanges, EntityLiving entity) {
        entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, MobUpkeepController.POTION_EFFECT_LENGTH,numChanges,false,true));
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

    public static Function<Configuration,List<DifficultyModifier>> getFromConfig = config -> {
        List<DifficultyModifier> returns = Lists.newArrayList();
        Property addSpeedModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddSpeedModifier",true,"Enable the add Speed modifier.  This adds the Speed potion effect to mobs on spawn.");
        boolean addSpeedEnabled = addSpeedModifierEnabledProp.getBoolean();
        Property SpeedLevelMaxLevelProp = config.get(IDENTIFIER,
                "SpeedModifierMaxLevel",3,"Maximum Speed level added to the mob when this is triggered.  Each Speed level increases walking speed by 20%.");
        int maxSpeedLevel = SpeedLevelMaxLevelProp.getInt();
        Property difficultyCostPerSpeedLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerSpeedLevel",10,"Cost of each level of Speed.");
        int diffCostPerLevelSpeed = difficultyCostPerSpeedLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "SpeedModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if(addSpeedEnabled && maxSpeedLevel>0 && diffCostPerLevelSpeed>0 && selectionWeight>0) {
            returns.add(new AddSpeedModifier(maxSpeedLevel,diffCostPerLevelSpeed,selectionWeight));
        }

        return returns;
    };
}
