package derpatiel.progressivediff.controls;

import betterquesting.api.api.QuestExpansion;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.jdoc.IJsonDoc;
import betterquesting.api.misc.IFactory;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.utils.JsonHelper;
import betterquesting.core.BetterQuesting;
import betterquesting.questing.rewards.RewardRegistry;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.ProgressiveDifficulty;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.integration.betterquesting.FactoryRewardDifficulty;
import derpatiel.progressivediff.integration.betterquesting.RewardDifficulty;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import derpatiel.progressivediff.util.PlayerNBTHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class BetterQuestingController extends DifficultyControl {

    public static final String IDENTIFIER = "CONTROL_BETTERQUESTING";

    private MultiplePlayerCombineType type;

    public BetterQuestingController(MultiplePlayerCombineType type){
        this.type = type;
    }



    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        int difficulty = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,details.entity,128,player-> PlayerNBTHandler.getDifficultyForPlayerForController(player,IDENTIFIER));
        return difficulty;
    }

    public static Function<Configuration,List<DifficultyControl>> getFromConfig = config -> {
        List<DifficultyControl> returns = Lists.newArrayList();
        Property enableBetterQuestingControllerProp = config.get(IDENTIFIER,
                "EnableBetterQuestingController", true, "Difficulty is added based on quests completed in Better Questing.");
        boolean enableModifier = enableBetterQuestingControllerProp.getBoolean();
        Property multiplePlayerComboTypeProp = config.get(IDENTIFIER,
                "MultiplePlayerCombinationType", MultiplePlayerCombineType.AVERAGE.toString(),
                "When there are multiple players within the spawn area (128 block radius), use this to decide what value time to use.  Valid values: "+MultiplePlayerCombineType.getValidValuesString()+" defaults to AVERAGE.");
        String comboTypeStr = multiplePlayerComboTypeProp.getString();
        MultiplePlayerCombineType type = MultiplePlayerCombineType.AVERAGE;
        try{
            type = MultiplePlayerCombineType.valueOf(comboTypeStr);
        }catch(Exception e){
            LOG.error("Invalid Multiple Player Combination type found for control with identifier "+IDENTIFIER+", found "+comboTypeStr+", using AVERAGE instead.");
        }
        if (enableModifier){
            returns.add(new BetterQuestingController(type));
        }
        return returns;
    };

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public void registerDifficultyReward(){
        RewardRegistry.INSTANCE.registerReward(FactoryRewardDifficulty.INSTANCE);
    }
}
