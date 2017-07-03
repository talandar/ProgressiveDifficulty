package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import net.minecraft.entity.EntityList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class BlocksBrokenControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_BLOCKS_BROKEN";

    private MultiplePlayerCombineType type;
    private double difficultyPerHundredBlocks;
    private int maxAddedDifficulty;

    public BlocksBrokenControl(MultiplePlayerCombineType type, double difficultyPerHundredBlocks, int maxAddedDifficulty){
        this.type = type;
        this.difficultyPerHundredBlocks = difficultyPerHundredBlocks;
        this.maxAddedDifficulty = maxAddedDifficulty;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {

        int brokenBlocks = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,details.entity,128,(player)->{
            int accum = 0;
            for(StatBase brokenStat : StatList.MINE_BLOCK_STATS) {
                accum+=player.getStatFile().readStat(brokenStat);
            }
            return accum;
        });

        int contribution = (int)(((double)brokenBlocks * difficultyPerHundredBlocks) / 100);

        if(maxAddedDifficulty>=0){
            contribution = Math.min(contribution,maxAddedDifficulty);
        }

        return contribution;
    }

    public static void readConfig(Configuration config) {
        Property mobsKilledAffectsDifficultyEnabledProp = config.get(IDENTIFIER,
                "EnableBlocksBrokenAffectsDifficulty", true, "Difficulty is added based on the number of blocks broken by the player.");
        boolean enableModifier = mobsKilledAffectsDifficultyEnabledProp.getBoolean();
        Property addedDifficultyPerHundredKillsProp = config.get(IDENTIFIER,
                "PerHundredBlocksAddedDifficulty", 0.01d, "Difficulty added to a mob for every 100 broken blocks.");
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
            DifficultyManager.addDifficultyControl(new BlocksBrokenControl(type,addedDifficultyPerHundredKills,maxAddedDifficulty));
        }
    }
}
