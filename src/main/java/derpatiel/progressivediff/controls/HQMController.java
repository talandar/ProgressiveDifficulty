package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.MultiplePlayerCombineType;
import derpatiel.progressivediff.SpawnEventDetails;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.PlayerAreaStatAccumulator;
import hardcorequesting.reputation.Reputation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;


public class HQMController extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_HQM_REP";

    private MultiplePlayerCombineType type;
    private String reputationName;

    public HQMController(MultiplePlayerCombineType type, String reputationName){
        this.type = type;
        this.reputationName = reputationName;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<Reputation> reps = Reputation.getReputationList();
        for(Reputation rep : reps){
            if(rep.getName().equalsIgnoreCase(reputationName)){
                int value = PlayerAreaStatAccumulator.getStatForPlayersInArea(type,details.entity,128,player->{ return rep.getValue(player); });
                return value;
            }
        }
        LOG.error("Could not find value in HQM for reputation with name "+reputationName+".  Please check configs against HQM setup.");
        return 0;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static void loadConfig(Configuration config){
        Property enableHQMRepControllerProp = config.get(IDENTIFIER,
                "EnableHQMRepController", true, "Difficulty is added based on a reputation in HQM.");
        boolean enableModifier = enableHQMRepControllerProp.getBoolean();
        Property reputationNameProp = config.get(IDENTIFIER,
                "ReputationName", "", "Name of the reputation type to use as difficulty.");
        String repName = reputationNameProp.getString();
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
        if (enableModifier && repName.length() > 0){
            DifficultyManager.addDifficultyControl(new HQMController(type,repName));
        }

    }
}
