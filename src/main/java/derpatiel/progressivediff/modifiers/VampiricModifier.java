package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Jim on 4/30/2017.
 */
public class VampiricModifier extends DifficultyModifier {

    public static final String IDENTIFIER = "MOD_VAMPIRIC";

    private int maxInstances;
    private int costForVampiric;
    private double selectionWeight;

    public VampiricModifier(int maxInstances, int costForVampiric, double selectionWeight){
        this.maxInstances = maxInstances;
        this.costForVampiric = costForVampiric;
        this.selectionWeight = selectionWeight;
    }

    @Override
    public int getMaxInstances() {
        return maxInstances;
    }

    @Override
    public void handleDamageEvent(LivingAttackEvent event){
        EntityLiving attackingMob = (EntityLiving)event.getSource().getTrueSource();
        int level = MobNBTHandler.getModifierLevel(attackingMob,IDENTIFIER);
        float damage = event.getAmount();
        float healDamage = damage * 0.2f * level;
        attackingMob.heal(healDamage);
    }

    @Override
    public int costPerChange() {
        return costForVampiric;
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
                "EnableVampiricModifier",true,"Enable the vampiric modifier.  Each level of the vampiric modifier returns 20% of damage done to the mob as health, before damage reduction from armor or similar.");
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "VampiricModifierWeight",1.0d,"Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",5,"Cost of the modifier.");
        int costForVampiric = difficultyCostProp.getInt();
        Property maxInstanceProp = config.get(IDENTIFIER,
                "MaxInstances", 5, "Maximum number of instances of the modifier that could be applied.");
        int maxInstances = maxInstanceProp.getInt();
        if(modifierEnabled && costForVampiric>0 && selectionWeight>0 && maxInstances>0) {
            returns.add(new VampiricModifier(maxInstances,costForVampiric,selectionWeight));
        }
        return returns;
    };
}
