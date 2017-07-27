package derpatiel.progressivediff.controls;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import net.minecraft.advancements.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class AdvancementControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_ACHIEVEMENTS";

    private Map<String,Integer> addedDifficultyAchievementMap;
    private MultiplePlayerCombineType type;


    public AdvancementControl(Map<String,Integer> addedDifficultyAchievementMap, MultiplePlayerCombineType combineType){
        this.addedDifficultyAchievementMap = addedDifficultyAchievementMap;
        this.type = combineType;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        int decidedPoints = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,details.entity,128,(player)->getAchievementPointsForPlayer(player));
        return decidedPoints;
    }

    private int getAchievementPointsForPlayer(EntityPlayerMP player) {
        int sum=0;
        PlayerAdvancements playerProgress = player.getAdvancements();
        AdvancementManager advancementManager = player.getServerWorld().getAdvancementManager();
        for(String advancementId : addedDifficultyAchievementMap.keySet()){
                Advancement advancement = advancementManager.getAdvancement(new ResourceLocation(advancementId));
                if(playerProgress.getProgress(advancement).isDone()){
                    sum += addedDifficultyAchievementMap.get(advancementId);
                }
            }
        return sum;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration,List<DifficultyControl>> getFromConfig = config -> {
        List<DifficultyControl> returns = Lists.newArrayList();
        Property playerAchievementsAffectDifficultyProp = config.get(IDENTIFIER,
                "EnableAchievementsAffectDifficulty", true, "Difficulty is added based on achievements the player has.");
        boolean achievementsAddDifficulty = playerAchievementsAffectDifficultyProp.getBoolean();
        Property achievementValueMapProp = config.get(IDENTIFIER,
                "AchievementValues", new String[]{}, "List of achievments and the difficulty they add.");
        String[] achieveMap = achievementValueMapProp.getStringList();
        Property multiplePlayerComboTypeProp = config.get(IDENTIFIER,
                "MultiplePlayerCombinationType",MultiplePlayerCombineType.AVERAGE.toString(),
                "When there are multiple players within the spawn area (128 block radius), use this to decide what value to use.  Valid values: "+MultiplePlayerCombineType.getValidValuesString()+" defaults to AVERAGE.");
        String comboTypeStr = multiplePlayerComboTypeProp.getString();
        MultiplePlayerCombineType type = MultiplePlayerCombineType.AVERAGE;

        Map<String,Integer> nameMap = Maps.newHashMap();
        for(String line : achieveMap){
            int index = line.lastIndexOf(":");
            if(index<0){
                LOG.error("invalid entry in AchievementValues key.  Requires strings of format \"achievementid:value\", found "+line);
                continue;
            }
            String name = line.substring(0,index);
            String valStr = line.substring(index+1);

            int value = 0;
            try {
                value = Integer.parseInt(valStr);
            }catch(NumberFormatException nfe){
                LOG.error("Invalid value format for achievement "+name+", requires integer, was "+valStr);
            }
            nameMap.put(name,value);
        }

        try{
            type = MultiplePlayerCombineType.valueOf(comboTypeStr);
        }catch(Exception e){
            LOG.error("Invalid Multiple Player Combination type found for control with identifier "+IDENTIFIER+", found "+comboTypeStr+", using AVERAGE instead.");
        }
        if (achievementsAddDifficulty && nameMap.size() > 0){
            returns.add(new AdvancementControl(nameMap,type));
        }
        return returns;
    };
}
