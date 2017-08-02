package derpatiel.progressivediff.integration.betterquesting;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.IQuestExpansion;
import betterquesting.api.api.QuestExpansion;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.rewards.IRewardRegistry;

@QuestExpansion
public class DifficultyExpansion implements IQuestExpansion {
    @Override
    public void loadExpansion() {
        IRewardRegistry rewardReg = QuestingAPI.getAPI(ApiReference.REWARD_REG);
        rewardReg.registerReward(FactoryRewardDifficulty.INSTANCE);
    }
}
