package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import derpatiel.progressivediff.MobUpkeepController;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddRegenerationModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_REGENERATION";

    private int maxRegenLevel;
    private int diffCostPerLevelRegen;
    private double selectionWeight;

    public AddRegenerationModifier(int maxRegenLevel, int diffCostPerLevelRegen, double selectionWeight){
        this.maxRegenLevel = maxRegenLevel;
        this.diffCostPerLevelRegen = diffCostPerLevelRegen;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxRegenLevel;
    }

    @Override
    public void handleUpkeepEvent(int numChanges, EntityLiving entity) {
        PotionEffect existingEffect = entity.getActivePotionEffect(MobEffects.REGENERATION);
        if(existingEffect==null) {
            entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, numChanges, false, true));
        }

    }

    @Override
    public boolean validForEntity(EntityLiving entity) {
        return !entity.isEntityUndead();
    }

    @Override
    public int costPerChange() {
        return diffCostPerLevelRegen;
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
        Property addRegenerationModifierEnabledProp = config.get(IDENTIFIER,
                "EnableAddRegenerationModifier",true,"Enable the add regeneration modifier.  This adds the regeneration potion effect to mobs on spawn.");
        boolean addRegenerationEnabled = addRegenerationModifierEnabledProp.getBoolean();
        Property regenerationLevelMaxLevelProp = config.get(IDENTIFIER,
                "RegenerationModifierMaxLevel",2,"Maximum regeneration level added to the mob when this is triggered.");
        int maxRegenLevel = regenerationLevelMaxLevelProp.getInt();
        Property difficultyCostPerRegenerationLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerRegenerationLevel",20,"Cost of each level of regeneration.");

        int diffCostPerLevelRegen = difficultyCostPerRegenerationLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "RegenerationModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if(addRegenerationEnabled && maxRegenLevel>0 && diffCostPerLevelRegen>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new AddRegenerationModifier(maxRegenLevel,diffCostPerLevelRegen,selectionWeight));
        }


    }
}
