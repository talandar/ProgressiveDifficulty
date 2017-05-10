package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

public class AllMobsKilledControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_KILLEDMOBS";

    private MultiplePlayerCombineType type;
    private double difficultyPerHundredKills;

    public AllMobsKilledControl(MultiplePlayerCombineType type, double difficultyPerHundredKills){
        this.type = type;
        this.difficultyPerHundredKills = difficultyPerHundredKills;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<EntityPlayerMP> playersInRange = details.entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayerMP.class, details.entity.getEntityBoundingBox().expand(128,128,128));
        int killedMobs = 0;
        if(playersInRange.size()>0) {
            switch (type) {
                case AVERAGE:
                    int avgSum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int killed = player.getStatFile().readStat(StatList.MOB_KILLS);
                        avgSum += killed;
                    }
                    killedMobs = avgSum / playersInRange.size();
                    break;
                case CLOSEST:
                    EntityPlayerMP closestPlayer = (EntityPlayerMP) details.entity.getEntityWorld().getClosestPlayerToEntity(details.entity, 128.0d);
                    killedMobs = closestPlayer.getStatFile().readStat(StatList.MOB_KILLS);
                    break;
                case MAX:
                    int max = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int killed = player.getStatFile().readStat(StatList.MOB_KILLS);
                        if (killed > max) {
                            max = killed;
                        }
                    }
                    killedMobs = max;
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (EntityPlayerMP player : playersInRange) {
                        int killed = player.getStatFile().readStat(StatList.MOB_KILLS);
                        if (killed < min) {
                            min = killed;
                        }
                    }
                    killedMobs = min;
                    break;
                case SUM:
                    int sum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int killed = player.getStatFile().readStat(StatList.MOB_KILLS);
                        sum += killed;
                    }
                    killedMobs = sum;
                    break;
            }
        }
        return (int)(((double)killedMobs * difficultyPerHundredKills) / 100);
    }

    public static void readConfig(Configuration config) {
        Property mobsKilledAffectsDifficultyEnabledProp = config.get(IDENTIFIER,
                "EnableMobsKilledAffectsDifficulty", true, "Difficulty is added based on the number of mobs players have killed.");
        boolean enableModifier = mobsKilledAffectsDifficultyEnabledProp.getBoolean();
        Property addedDifficultyPerHundredKillsProp = config.get(IDENTIFIER,
                "PerHundredKillsAddedDifficulty", 10, "Difficulty added to a mob for every 100 kills of any mob.");
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
        if (enableModifier && addedDifficultyPerHundredKills > 0){
            DifficultyManager.addDifficultyControl(new AllMobsKilledControl(type,addedDifficultyPerHundredKills));
        }
    }
}
