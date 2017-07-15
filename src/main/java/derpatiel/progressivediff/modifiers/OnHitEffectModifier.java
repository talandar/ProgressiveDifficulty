package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Loader;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class OnHitEffectModifier extends DifficultyModifier {

    private int maxInstances;
    private int costPerLevel;
    private double selectionWeight;
    private Potion effect;
    private int duration;
    private String identifier;

    public OnHitEffectModifier(Potion effect, int duration, int maxInstances, int costPerLevel, double selectionWeight, String identifier){
        this.maxInstances = maxInstances;
        this.costPerLevel = costPerLevel;
        this.selectionWeight = selectionWeight;
        this.effect = effect;
        this.duration = duration;
        this.identifier = identifier;
    }

    @Override
    public int getMaxInstances() {
        return maxInstances;
    }

    @Override
    public int costPerChange() {
        return costPerLevel;
    }

    @Override
    public double getWeight() {
        return selectionWeight;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void handleDamageEvent(LivingAttackEvent event) {
        super.handleDamageEvent(event);
        if(!(event.getEntity() instanceof EntityLivingBase))
            return;
        EntityLivingBase hitEntity = (EntityLivingBase)event.getEntity();
        EntityLiving cause = (EntityLiving)event.getSource().getTrueSource();
        int level = MobNBTHandler.getModifierLevel(cause,identifier);
        hitEntity.addPotionEffect(new PotionEffect(effect, duration,level,false,false));
    }

    public static Function<Configuration,List<DifficultyModifier>> getFromConfig = config -> {
        List<DifficultyModifier> returns = Lists.newArrayList();

        //SLOWNESS;
        String ID_SLOW = "MOD_SLOW_ON_HIT";
        Property modifierEnabledProp = config.get(ID_SLOW,
                "EnableSlowOnHitModifier",true,"Enable the slow on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property MaxLevelProp = config.get(ID_SLOW,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        int maxLevel = MaxLevelProp.getInt();
        Property difficultyCostPerLevelProp = config.get(ID_SLOW,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        int diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        Property effectDurationProp = config.get(ID_SLOW,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        int duration = effectDurationProp.getInt();
        Property selectionWeightProp = config.get(ID_SLOW,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.SLOWNESS,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_SLOW));
        }

        //MINING_FATIGUE;
        String ID_FATIGUE = "MOD_FATIGUE_ON_HIT";
        modifierEnabledProp = config.get(ID_FATIGUE,
                "EnableFatigueOnHitModifier",true,"Enable the mining fatigue on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        MaxLevelProp = config.get(ID_FATIGUE,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        maxLevel = MaxLevelProp.getInt();
        difficultyCostPerLevelProp = config.get(ID_FATIGUE,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_FATIGUE,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_FATIGUE,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.MINING_FATIGUE,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_FATIGUE));
        }

        //BLINDNESS;
        String ID_BLIND = "MOD_BLIND_ON_HIT";
        modifierEnabledProp = config.get(ID_BLIND,
                "EnableFatigueOnHitModifier",true,"Enable the blindness on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        difficultyCostPerLevelProp = config.get(ID_BLIND,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_BLIND,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_BLIND,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.BLINDNESS,duration,1,diffCostPerLevel,selectionWeight,ID_BLIND));
        }
        //HUNGER;
        String ID_HUNGER = "MOD_HUNGER_ON_HIT";
        modifierEnabledProp = config.get(ID_HUNGER,
                "EnableHungerOnHitModifier",true,"Enable the hunger on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        MaxLevelProp = config.get(ID_HUNGER,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        maxLevel = MaxLevelProp.getInt();
        difficultyCostPerLevelProp = config.get(ID_HUNGER,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_HUNGER,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_HUNGER,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.HUNGER,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_HUNGER));
        }

        //WEAKNESS;
        String ID_WEAKNESS = "MOD_WEAKNESS_ON_HIT";
        modifierEnabledProp = config.get(ID_WEAKNESS,
                "EnableWeaknessOnHitModifier",true,"Enable the weakness on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        MaxLevelProp = config.get(ID_WEAKNESS,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        maxLevel = MaxLevelProp.getInt();
        difficultyCostPerLevelProp = config.get(ID_WEAKNESS,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_WEAKNESS,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_WEAKNESS,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.WEAKNESS,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_WEAKNESS));
        }
        //POISON;
        String ID_POISON = "MOD_POISON_ON_HIT";
        modifierEnabledProp = config.get(ID_POISON,
                "EnableWeaknessOnHitModifier",true,"Enable the poison on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        MaxLevelProp = config.get(ID_POISON,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        maxLevel = MaxLevelProp.getInt();
        difficultyCostPerLevelProp = config.get(ID_POISON,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_POISON,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_POISON,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.POISON,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_POISON));
        }
        //WITHER;
        String ID_WITHER = "MOD_WITHER_ON_HIT";
        modifierEnabledProp = config.get(ID_WITHER,
                "EnableWitherOnHitModifier",true,"Enable the wither on hit modifier.  Adds the potion effect to targets hit by a mob with this modifier.");
        modifierEnabled = modifierEnabledProp.getBoolean();
        MaxLevelProp = config.get(ID_WITHER,
                "ModifierMaxLevel",3,"Maximum level of this effect added to the target player when this is triggered.");
        maxLevel = MaxLevelProp.getInt();
        difficultyCostPerLevelProp = config.get(ID_WITHER,
                "DifficultyCostPerLevel",10,"Cost of each level of the effect applied to the target player.");
        diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        effectDurationProp = config.get(ID_WITHER,
                "EffectDuration",40,"Duration of the effect when applied to the target.");
        duration = effectDurationProp.getInt();
        selectionWeightProp = config.get(ID_WITHER,
                "ModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        if(modifierEnabled && maxLevel>0 && diffCostPerLevel>0 && selectionWeight>0) {
            returns.add(new OnHitEffectModifier(MobEffects.WITHER,duration,maxLevel,diffCostPerLevel,selectionWeight,ID_WITHER));
        }


        return returns;
    };
}
