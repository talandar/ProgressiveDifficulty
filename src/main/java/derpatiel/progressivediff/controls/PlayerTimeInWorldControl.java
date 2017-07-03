package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

public class PlayerTimeInWorldControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_LOG_IN_TIME";

    private double addedDifficultyPerDay;
    private MultiplePlayerCombineType type;
    private int maxAddedDifficulty;


    public PlayerTimeInWorldControl(double addedDifficultyPerDay, MultiplePlayerCombineType combineType, int maxAddedDifficulty){
        this.addedDifficultyPerDay = addedDifficultyPerDay;
        this.type = combineType;
        this.maxAddedDifficulty = maxAddedDifficulty;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        int decidedTicks = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,StatList.PLAY_ONE_MINUTE,details.entity,128);
        double days = ((double)decidedTicks)/(24000.0d);//minecraft day length
        int contribution = (int)(addedDifficultyPerDay * days);

        if(maxAddedDifficulty>=0){
            contribution = Math.min(contribution, maxAddedDifficulty);
        }

        return contribution;
    }

    public static void readConfig(Configuration config) {
        Property extraPlayersAffectsDifficultyEnabled = config.get(IDENTIFIER,
                "EnableTimeInWorldAddsDifficulty", true, "Difficulty is added based on the time players have been logged in to the world.");
        boolean timeAddsDifficulty = extraPlayersAffectsDifficultyEnabled.getBoolean();
        Property addedDifficultyPerMinecraftDayProp = config.get(IDENTIFIER,
                "PerDayPlaytimeAddedDifficulty", 0.2d, "Difficulty added to a mob for each minecraft day.");
        double addedDifficultyPerMinecraftDay = addedDifficultyPerMinecraftDayProp.getDouble();
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
        if (timeAddsDifficulty && addedDifficultyPerMinecraftDay > 0){
            DifficultyManager.addDifficultyControl(new PlayerTimeInWorldControl(addedDifficultyPerMinecraftDay,type,maxAddedDifficulty));
        }
    }
}
