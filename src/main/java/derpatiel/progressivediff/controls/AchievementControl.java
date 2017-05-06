package derpatiel.progressivediff.controls;

import com.google.common.collect.Maps;
import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.Map;

public class AchievementControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_ACHIEVEMENTS";
    private static final String[] defaultAchievementValues = new String[]{
            "achievement.killEnemy:5",
            "achievement.portal:5",
            "achievement.killWither:20"
    };

    private Map<Achievement,Integer> addedDifficultyAchievementMap;
    private MultiplePlayerCombineType type;


    public AchievementControl(Map<Achievement,Integer> addedDifficultyAchievementMap, MultiplePlayerCombineType combineType){
        this.addedDifficultyAchievementMap = addedDifficultyAchievementMap;
        this.type = combineType;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<EntityPlayerMP> playersInRange = details.entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayerMP.class, details.entity.getEntityBoundingBox().expand(128,128,128));
        int decidedPoints = 0;
        if(playersInRange.size()>0) {
            switch (type) {
                case AVERAGE:
                    int avgSum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int points = getAchievementPointsForPlayer(player);
                        avgSum += points;
                    }
                    decidedPoints = avgSum / playersInRange.size();
                    break;
                case CLOSEST:
                    EntityPlayerMP closestPlayer = (EntityPlayerMP) details.entity.getEntityWorld().getClosestPlayerToEntity(details.entity, 128.0d);
                    decidedPoints = getAchievementPointsForPlayer(closestPlayer);
                    break;
                case MAX:
                    int max = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int points = getAchievementPointsForPlayer(player);
                        if (points > max) {
                            max = points;
                        }
                    }
                    decidedPoints = max;
                    break;
                case MIN:
                    int min = Integer.MAX_VALUE;
                    for (EntityPlayerMP player : playersInRange) {
                        int points = getAchievementPointsForPlayer(player);
                        if (points < min) {
                            min = points;
                        }
                    }
                    decidedPoints = min;
                    break;
                case SUM:
                    int sum = 0;
                    for (EntityPlayerMP player : playersInRange) {
                        int points = getAchievementPointsForPlayer(player);
                        sum += points;
                    }
                    decidedPoints = sum;
                    break;
            }
        }
        return decidedPoints;
    }

    private int getAchievementPointsForPlayer(EntityPlayerMP player) {
        int sum=0;
        for(Achievement achievement : addedDifficultyAchievementMap.keySet()){
            if(player.hasAchievement(achievement))
                sum+=addedDifficultyAchievementMap.get(achievement);
        }
        return sum;
    }

    public static void readConfig(Configuration config) {
        Property playerAchievementsAffectDifficultyProp = config.get(IDENTIFIER,
                "EnableAchievementsAffectDifficulty", true, "Difficulty is added based on achievements the player has.");
        boolean achievementsAddDifficulty = playerAchievementsAffectDifficultyProp.getBoolean();
        Property achievementValueMapProp = config.get(IDENTIFIER,
                "AchievementValues", defaultAchievementValues, "List of achievments and the difficulty they add.");
        String[] achieveMap = achievementValueMapProp.getStringList();
        Property multiplePlayerComboTypeProp = config.get(IDENTIFIER,
                "MultiplePlayerCombinationType",MultiplePlayerCombineType.AVERAGE.toString(),
                "When there are multiple players within the spawn area (128 block radius), use this to decide what value to use.  Valid values: "+MultiplePlayerCombineType.getValidValuesString()+" defaults to AVERAGE.");
        String comboTypeStr = multiplePlayerComboTypeProp.getString();
        MultiplePlayerCombineType type = MultiplePlayerCombineType.AVERAGE;

        Map<Achievement,Integer> map = Maps.newHashMap();
        for(String line : achieveMap){
            String[] parts = line.split(":");
            String name = parts[0];
            int value = Integer.parseInt(parts[1]);
        }
        for(Achievement a : AchievementList.ACHIEVEMENTS){
            System.out.println(a.statId);
        }
        //TODO: parse achieveMap, handle errors

        try{
            type = MultiplePlayerCombineType.valueOf(comboTypeStr);
        }catch(Exception e){
            LOG.error("Invalid Multiple Player Combination type found for control with identifier "+IDENTIFIER+", found "+comboTypeStr+", using AVERAGE instead.");
        }
        if (achievementsAddDifficulty && map.size() > 0){
            DifficultyManager.addDifficultyControl(new AchievementControl(map,type));
        }
    }
}
