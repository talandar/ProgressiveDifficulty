package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.DifficultyModifier;
import derpatiel.progressivediff.MobUpkeepController;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Jim on 4/30/2017.
 */
public class SlowingGazeModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_SLOW_GAZE";

    private int maxLevel;
    private int diffCostPerLevel;
    private double selectionWeight;

    public SlowingGazeModifier(int maxLevel, int diffCostPerLevel, double selectionWeight){
        this.maxLevel = maxLevel;
        this.diffCostPerLevel = diffCostPerLevel;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxLevel;
    }

    @Override
    public void handleUpkeepEvent(int numChanges, EntityLiving entity) {
        if(entity.getAttackTarget() instanceof EntityPlayer && entity.canEntityBeSeen(entity.getAttackTarget())){
            EntityPlayer player = (EntityPlayer)entity.getAttackTarget();
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,MobUpkeepController.POTION_EFFECT_LENGTH,numChanges,false,true));
        }
    }

    @Override
    public int costPerChange() {
        return diffCostPerLevel;
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
        Property modifierEnabledProp = config.get(IDENTIFIER,
                "EnableSlowingGazeModifier",true,"Enable the Slowing Gaze modifier.  This modifier adds the slowing potion effect to the target player of the mob, if the mob can see the player");
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property SpeedLevelMaxLevelProp = config.get(IDENTIFIER,
                "SlowModifierMaxLevel",3,"Maximum Slow level added to the target player when this is triggered.");
        int maxLevel = SpeedLevelMaxLevelProp.getInt();
        Property difficultyCostPerSpeedLevelProp = config.get(IDENTIFIER,
                "DifficultyCostPerSlowLevel",30,"Cost of each level of Slow applied to the target player.");
        int diffCostPerLevel = difficultyCostPerSpeedLevelProp.getInt();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new SlowingGazeModifier(maxLevel,diffCostPerLevel,selectionWeight));
        }
        return returns;
    };
}
