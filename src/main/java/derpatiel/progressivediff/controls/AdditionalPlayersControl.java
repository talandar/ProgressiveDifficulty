package derpatiel.progressivediff.controls;

import derpatiel.progressivediff.DifficultyControl;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

public class AdditionalPlayersControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_EXTRA_PLAYERS";

    private int addedPerExtraPlayer;

    public AdditionalPlayersControl(int addedPerExtraPlayer){
        this.addedPerExtraPlayer = addedPerExtraPlayer;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<EntityPlayer> players = details.entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, details.entity.getEntityBoundingBox().expand(128,128,128));
        if(players.size()>0) {
            return addedPerExtraPlayer * (players.size() - 1);
        }
        return 0;
    }

    public static void readConfig(Configuration config) {
        Property extraPlayersAffectsDifficultyEnabled = config.get(IDENTIFIER,
                "ExtraPlayersAffectsDifficulty", true, "Extra Players in the spawn radius changes the difficulty of a mob.  Each player past the first adds difficulty.");
        boolean extraPlayersAddsDifficulty = extraPlayersAffectsDifficultyEnabled.getBoolean();
        Property addedDifficultyPerExtraPlayerProp = config.get(IDENTIFIER,
                "ExtraPlayerAddedDifficulty", 20, "Difficulty added to a mob for each player past the first in the spawn radius (128 blocks).");
        int addedDifficultyPerExtraPlayer = addedDifficultyPerExtraPlayerProp.getInt();
        if (extraPlayersAddsDifficulty && addedDifficultyPerExtraPlayer > 0){
            DifficultyManager.addDifficultyControl(new AdditionalPlayersControl(addedDifficultyPerExtraPlayer));
        }
    }
}
