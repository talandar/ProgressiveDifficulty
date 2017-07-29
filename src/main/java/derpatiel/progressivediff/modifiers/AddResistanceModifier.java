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
public class AddResistanceModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_RESISTANCE";

    private int maxResistanceLevel;
    private int diffCostPerLevelResistance;
    private double selectionWeight;

    public AddResistanceModifier(int maxResistanceLevel, int diffCostPerLevelResistance, double selectionWeight){
        this.maxResistanceLevel = maxResistanceLevel;
        this.diffCostPerLevelResistance = diffCostPerLevelResistance;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxResistanceLevel;
    }

    @Override
    public void handleUpkeepEvent(int numChanges, EntityLiving entity) {
        entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, MobUpkeepController.POTION_EFFECT_LENGTH,numChanges,false,true));

    }

    @Override
    public int costPerChange() {
        return diffCostPerLevelResistance;
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
        Property addResistanceModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddResistanceModifier",true,"Enable the add resistance modifier.  This adds the resistance potion effect to mobs on spawn.");
        boolean addResistanceEnabled = addResistanceModifierEnabledProp.getBoolean();
        Property resistanceLevelMaxLevelProp = config.get(IDENTIFIER,
                "ResistanceModifierMaxLevel",2,"Maximum resistance level added to the mob when this is triggered.  Each resistance level is a flat 20% damage reduction.  Level 5+ will make the mob invincible.");
        int maxResistanceLevel = resistanceLevelMaxLevelProp.getInt();
        Property difficultyCostPerResistanceLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerResistLevel",20,"Cost of each level of resistance.");

        int diffCostPerLevelResistance = difficultyCostPerResistanceLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "ResistanceModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if(addResistanceEnabled && maxResistanceLevel>0 && diffCostPerLevelResistance>0 && selectionWeight>0) {
            return Lists.newArrayList(new AddResistanceModifier(maxResistanceLevel, diffCostPerLevelResistance, selectionWeight));
        }

        return Lists.newArrayList();
    };
}
