package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.DifficultyModifier;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.swing.text.html.parser.Entity;

/**
 * Created by Jim on 4/30/2017.
 */
public class CreeperChargeModifier extends DifficultyModifier {

    private static final String IDENTIFIER = "MOD_CREEPER_PRECHARGE";

    private static int diffCost;
    private static double selectionWeight;

    public CreeperChargeModifier(){
    }

    @Override
    public int getMaxInstances() {
        return 1;
    }

    @Override
    public void makeChange(int numInstances, EntityLiving entity, boolean isUpkeep) {
        if(!isUpkeep) {
            if (entity instanceof EntityCreeper) {
                EntityCreeper creeper = (EntityCreeper) entity;
                //this avoids our creepers being on fire when they spawn
                creeper.setFire(0);
                creeper.onStruckByLightning(null);
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    @Override
    public boolean validForEntity(EntityLiving entity) {
        return entity instanceof EntityCreeper;
    }

    @Override
    public int costPerChange() {
        return diffCost;
    }

    @Override
    public double getWeight() {
        return selectionWeight;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static void readConfig(Configuration config){
        Property prechargeEnabledProp = config.get(IDENTIFIER,
                "EnableCreeperPrecharge",true,"Enable the creeper precharge modifier.  This causes creepers to spawn charged, as if hit by lightning.");
        boolean prechargeEnabled = prechargeEnabledProp.getBoolean();
        Property selectionWeightProp = config.get(IDENTIFIER,
                "CreeperPrechargeWeight",1.0d,"Weight that affects how often this modifier is selected.");
        selectionWeight = selectionWeightProp.getDouble();
        Property difficultyCostProp = config.get(IDENTIFIER,
                "DifficultyCost",40,"Cost of applying the charge to the creeper.");
        diffCost = difficultyCostProp.getInt();
        if(prechargeEnabled && diffCost>0 && selectionWeight>0) {
            DifficultyManager.addDifficultyModifier(new CreeperChargeModifier());
        }
    }
}
