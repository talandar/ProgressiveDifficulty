package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
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

    private int addedDifficultyPerDay;
    private MultiplePlayerCombineType type;


    public PlayerTimeInWorldControl(int addedDifficultyPerDay, MultiplePlayerCombineType combineType){
        this.addedDifficultyPerDay = addedDifficultyPerDay;
        this.type = combineType;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<EntityPlayerMP> playersInRange = details.entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayerMP.class, details.entity.getEntityBoundingBox().expand(128,128,128));
        int decidedTicks = 0;
        if(playersInRange.size()>0) {
            switch (type) {
                case AVERAGE:
                    int avgSum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int ticksPlayed = player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
                        avgSum += ticksPlayed;
                    }
                    decidedTicks = avgSum / playersInRange.size();
                    break;
                case CLOSEST:
                    EntityPlayerMP closestPlayer = (EntityPlayerMP) details.entity.getEntityWorld().getClosestPlayerToEntity(details.entity, 128.0d);
                    decidedTicks = closestPlayer.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
                    break;
                case MAX:
                    int max = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int ticksPlayed = player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
                        if (ticksPlayed > max) {
                            max = ticksPlayed;
                        }
                    }
                    decidedTicks = max;
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (EntityPlayerMP player : playersInRange) {
                        int ticksPlayed = player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
                        if (ticksPlayed < min) {
                            min = ticksPlayed;
                        }
                    }
                    decidedTicks = min;
                    break;
                case SUM:
                    int sum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int ticksPlayed = player.getStatFile().readStat(StatList.PLAY_ONE_MINUTE);
                        sum += ticksPlayed;
                    }
                    decidedTicks = sum;
                    break;
            }
        }
        double days = ((double)decidedTicks)/(24000.0d);//minecraft day length
        return (int)(addedDifficultyPerDay * days);
    }

    public static void readConfig(Configuration config) {
        Property extraPlayersAffectsDifficultyEnabled = config.get(IDENTIFIER,
                "EnableTimeInWorldAddsDifficulty", true, "Difficulty is added based on the time players have been logged in to the world.");
        boolean timeAddsDifficulty = extraPlayersAffectsDifficultyEnabled.getBoolean();
        Property addedDifficultyPerMinecraftDayProp = config.get(IDENTIFIER,
                "PerDayPlaytimeAddedDifficulty", 1, "Difficulty added to a mob for each minecraft day.");
        int addedDifficultyPerMinecraftDay = addedDifficultyPerMinecraftDayProp.getInt();
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
        if (timeAddsDifficulty && addedDifficultyPerMinecraftDay > 0){
            DifficultyManager.addDifficultyControl(new PlayerTimeInWorldControl(addedDifficultyPerMinecraftDay,type));
        }
    }
}
