package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

public class AllMobsKilledControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_KILLEDMOBS";

    private MultiplePlayerCombineType type;
    private double difficultyPerHundredKills;
    private int maximumDifficultyContribution;

    public AllMobsKilledControl(MultiplePlayerCombineType type, double difficultyPerHundredKills, int maximumDifficultyContribution){
        this.type = type;
        this.difficultyPerHundredKills = difficultyPerHundredKills;
        this.maximumDifficultyContribution = maximumDifficultyContribution;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        int killedMobs = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,StatList.MOB_KILLS,details.entity,128);
        int contribution = (int)(((double)killedMobs * difficultyPerHundredKills) / 100);
        if(maximumDifficultyContribution>=0){
            contribution = Math.min(contribution,maximumDifficultyContribution);
        }
        return contribution;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static void readConfig(Configuration config) {
        Property mobsKilledAffectsDifficultyEnabledProp = config.get(IDENTIFIER,
                "EnableMobsKilledAffectsDifficulty", true, "Difficulty is added based on the number of mobs players have killed.");
        boolean enableModifier = mobsKilledAffectsDifficultyEnabledProp.getBoolean();
        Property addedDifficultyPerHundredKillsProp = config.get(IDENTIFIER,
                "PerHundredKillsAddedDifficulty", 1.0d, "Difficulty added to a mob for every 100 kills of any mob.");
        double addedDifficultyPerHundredKills = addedDifficultyPerHundredKillsProp.getDouble();
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
            DifficultyManager.addDifficultyControl(new AllMobsKilledControl(type,addedDifficultyPerHundredKills,maxAddedDifficulty));
        }
    }
}
