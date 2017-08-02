package derpatiel.progressivediff.integration.betterquesting;

import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.misc.IFactory;
import com.google.gson.JsonObject;
import derpatiel.progressivediff.ProgressiveDifficulty;
import net.minecraft.util.ResourceLocation;

public class FactoryRewardDifficulty implements IFactory<RewardDifficulty> {

    public static final FactoryRewardDifficulty INSTANCE = new FactoryRewardDifficulty();

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ProgressiveDifficulty.MODID,"difficulty");
    }

    @Override
    public RewardDifficulty createNew() {
        return new RewardDifficulty();
    }

    @Override
    public RewardDifficulty loadFromJson(JsonObject jsonObject) {
        RewardDifficulty rewardDifficulty = new RewardDifficulty();
        rewardDifficulty.readFromJson(jsonObject, EnumSaveType.CONFIG);
        return rewardDifficulty;
    }
}
