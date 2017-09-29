package derpatiel.progressivediff.modifiers;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.MobUpkeepController;
import derpatiel.progressivediff.api.DifficultyModifier;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

public class PotionCloudModifier extends DifficultyModifier {

    private int maxInstances;
    private int costPerLevel;
    private double selectionWeight;
    private Potion effect;
    private String identifier;
    boolean onlyEffectsPlayers;

    public PotionCloudModifier(Potion effect, int maxInstances, int costPerLevel, double selectionWeight, String identifier){
        this.maxInstances = maxInstances;
        this.costPerLevel = costPerLevel;
        this.selectionWeight = selectionWeight;
        this.effect = effect;
        this.identifier = identifier;
        this.onlyEffectsPlayers = onlyEffectsPlayers;
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
    public void handleUpkeepEvent(int numInstances, EntityLiving entity) {
        //called every half-second (MobUpkeepController.UPKEEP_INTERVAL)
        spawnLingeringCloud(numInstances, entity);
        //TODO: block mobs from taking damage from this if they spawned it :/
    }


    private void spawnLingeringCloud(int amplifier, EntityLiving mob) {

        PlayerEffectingOnlyEntityAreaEffectCloud entityareaeffectcloud = new PlayerEffectingOnlyEntityAreaEffectCloud(mob.world, mob.posX, mob.posY, mob.posZ);
        entityareaeffectcloud.setRadius(2.5F);
        entityareaeffectcloud.setRadiusOnUse(-0.5F);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setDuration(MobUpkeepController.POTION_EFFECT_LENGTH);//TODO is this a good time?
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new PotionEffect(this.effect,entityareaeffectcloud.getDuration(),amplifier));
        entityareaeffectcloud.setOwner(mob);
        mob.world.spawnEntity(entityareaeffectcloud);
    }

    public static Function<Configuration,List<DifficultyModifier>> getFromConfig = config -> {
        List<DifficultyModifier> returns = Lists.newArrayList();

        constructFromConfig("MOD_DAMAGE_CLOUD",
                MobEffects.INSTANT_DAMAGE,
                "EnableDamageCloudModifier",
                "Enable the damaging cloud.  Mobs with this effect create lingering clouds of the instant damage potion effect.",
                3,
                10,
                1.0d,
                returns,
                config);

        Potion[] effects = new Potion[]{
                MobEffects.HUNGER,
                MobEffects.MINING_FATIGUE,
                MobEffects.SLOWNESS,
                MobEffects.WEAKNESS,
                MobEffects.BLINDNESS,
                MobEffects.LEVITATION,
                MobEffects.NAUSEA
                ,
        };

        return returns;
    };
    private static void constructFromConfig(String ID,
                                 Potion effect,
                                 String enableKey,
                                 String enableComment,
                                 int maxLevelDefault,
                                 int defaultDifficultyCost,
                                 double defaultWeight,
                                 List<DifficultyModifier> returns,
                                 Configuration config) {
        Property modifierEnabledProp = config.get(ID,
                enableKey, true, enableComment);
        boolean modifierEnabled = modifierEnabledProp.getBoolean();
        Property MaxLevelProp = config.get(ID,
                "ModifierMaxLevel", maxLevelDefault, "Maximum level of this effect added to the target player when entering the cloud.");
        int maxLevel = MaxLevelProp.getInt();
        Property difficultyCostPerLevelProp = config.get(ID,
                "DifficultyCostPerLevel", defaultDifficultyCost, "Cost of each level of the effect applied to the target player.");
        int diffCostPerLevel = difficultyCostPerLevelProp.getInt();
        Property selectionWeightProp = config.get(ID,
                "ModifierWeight", defaultWeight, "Weight that affects how often this modifier is selected.");
        double selectionWeight = selectionWeightProp.getDouble();
        if (modifierEnabled && maxLevel > 0 && diffCostPerLevel > 0 && selectionWeight > 0) {
            returns.add(new PotionCloudModifier(effect, maxLevel, diffCostPerLevel, selectionWeight, ID));
        }
    }

    /**
     * marks this cloud as a cloud that does not effect the "owner".  For the purposes of this mod, the owner is
     * the mob that spawns the cloud.  This way, we can have damaging clouds without applying damage to the mob
     */
    public class PlayerEffectingOnlyEntityAreaEffectCloud extends EntityAreaEffectCloud {

        public PlayerEffectingOnlyEntityAreaEffectCloud(World worldIn, double x, double y, double z)
        {
            super(worldIn,x,y,z);
        }
    }
}
