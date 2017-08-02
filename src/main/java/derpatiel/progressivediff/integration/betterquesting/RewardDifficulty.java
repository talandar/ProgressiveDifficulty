package derpatiel.progressivediff.integration.betterquesting;

import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.jdoc.IJsonDoc;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.utils.JsonHelper;
import com.google.gson.JsonObject;
import derpatiel.progressivediff.controls.BetterQuestingController;
import derpatiel.progressivediff.util.PlayerNBTHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RewardDifficulty implements IReward {

    int difficulty;
    boolean visible;


    @Override
    public String getUnlocalisedName() {
        return "progressivedifficulty.integration.betterquest.reward.difficulty";
    }

    @Override
    public ResourceLocation getFactoryID() {
        return FactoryRewardDifficulty.INSTANCE.getRegistryName();
    }

    @Override
    public boolean canClaim(EntityPlayer entityPlayer, IQuest iQuest) {
        return true;
    }

    @Override
    public void claimReward(EntityPlayer entityPlayer, IQuest iQuest) {
        if(entityPlayer.world.isRemote)
        {
            return;
        }
        PlayerNBTHandler.incrementDifficultyForPlayerForController(entityPlayer, BetterQuestingController.IDENTIFIER,this.difficulty);
    }

    @Override
    public IJsonDoc getDocumentation() {
        return null;
    }

    @Override
    public IGuiEmbedded getRewardGui(int posX, int posY, int sizeX, int sizeY, IQuest quest) {
        return new GuiRewardDifficulty(this,posX,posY,sizeX,sizeY);
    }

    @Override
    public GuiScreen getRewardEditor(GuiScreen guiScreen, IQuest iQuest) {
        return null;
    }

    @Override
    public JsonObject writeToJson(JsonObject jsonObject, EnumSaveType enumSaveType) {
        jsonObject.addProperty("difficulty", difficulty);
        jsonObject.addProperty("visible",visible);
        return jsonObject;
    }

    @Override
    public void readFromJson(JsonObject jsonObject, EnumSaveType enumSaveType) {
        difficulty = JsonHelper.GetNumber(jsonObject, "difficulty", 0).intValue();
        visible = JsonHelper.GetBoolean(jsonObject,"visible",false);
    }
}
