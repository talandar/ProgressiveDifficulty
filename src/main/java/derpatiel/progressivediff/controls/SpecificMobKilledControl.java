package derpatiel.progressivediff.controls;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.*;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import net.minecraft.entity.EntityList;
import net.minecraft.stats.StatBase;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.List;
import java.util.function.Function;

public class SpecificMobKilledControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_KILLED_SPECIFIC_MOB";

    private MultiplePlayerCombineType type;
    private double difficultyPerHundredKills;
    private int maxAddedDifficulty;

    public SpecificMobKilledControl(MultiplePlayerCombineType type, double difficultyPerHundredKills, int maxAddedDifficulty){
        this.type = type;
        this.difficultyPerHundredKills = difficultyPerHundredKills;
        this.maxAddedDifficulty = maxAddedDifficulty;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {

        EntityList.EntityEggInfo eggInfo = EntityRegistry.getEntry(details.entity.getClass()).getEgg();
        if(eggInfo==null) {
            if(DifficultyManager.debugLogSpawns){
                LOG.info("Tried to get kills for mob with class "+details.entity.getClass()+", but not spawn egg found.  Cannot count kills for this mob for difficulty.");
            }
            return 0;
        }
        StatBase stat = eggInfo.killEntityStat;
        int killedMobs = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,stat,details.entity,128);

        int contribution = (int)(((double)killedMobs * difficultyPerHundredKills) / 100);

        if(maxAddedDifficulty>=0){
            contribution = Math.min(contribution,maxAddedDifficulty);
        }

        return contribution;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration,List<DifficultyControl>> getFromConfig = config -> {
        List<DifficultyControl> returns = Lists.newArrayList();
        Property mobsKilledAffectsDifficultyEnabledProp = config.get(IDENTIFIER,
                "EnableSpecificMobKilledAffectsDifficulty", true, "Difficulty is added based on the number of the mob to be spawned that players have killed.");
        boolean enableModifier = mobsKilledAffectsDifficultyEnabledProp.getBoolean();
        Property addedDifficultyPerHundredKillsProp = config.get(IDENTIFIER,
                "PerHundredKillsAddedDifficulty", 1, "Difficulty added to a mob for every 100 kills of the mob to be spawned.");
        int addedDifficultyPerHundredKills = addedDifficultyPerHundredKillsProp.getInt();
        Property multiplePlayerComboTypeProp = config.get(IDENTIFIER,
                "MultiplePlayerCombinationType",MultiplePlayerCombineType.AVERAGE.toString(),
                "When there are multiple players within the spawn area (128 block radius), use this to decide what value time to use.  Valid values: "+MultiplePlayerCombineType.getValidValuesString()+" defaults to AVERAGE.");
        String comboTypeStr = multiplePlayerComboTypeProp.getString();
        MultiplePlayerCombineType type = MultiplePlayerCombineType.AVERAGE;
        try{
            type = MultiplePlayerCombineType.valueOf(comboTypeStr);
        }catch(Exception e){
            LOG.error("Invalid Multiple Player Combination type found for control with identifier "+IDENTIFIER+", found "+comboTypeStr+", using AVERAGE instead.");
        }
        Property maxDifficultyContributionProp = config.get(IDENTIFIER,
                "MaximumDifficultyContribution",-1,"Maximum difficulty this controller can contribute to the mobs score.  Negative values disable this maximum.");
        int maxAddedDifficulty = maxDifficultyContributionProp.getInt();
        if (enableModifier && addedDifficultyPerHundredKills > 0){
            returns.add(new SpecificMobKilledControl(type,addedDifficultyPerHundredKills,maxAddedDifficulty));
        }
        return returns;
    };
}
