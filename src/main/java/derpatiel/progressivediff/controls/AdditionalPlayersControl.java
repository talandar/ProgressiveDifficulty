package derpatiel.progressivediff.controls;

import com.google.common.collect.Lists;
import derpatiel.progressivediff.api.DifficultyControl;
import derpatiel.progressivediff.OldManager;
import derpatiel.progressivediff.SpawnEventDetails;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;
import java.util.function.Function;

public class AdditionalPlayersControl extends DifficultyControl {

    private static final String IDENTIFIER = "CONTROL_EXTRA_PLAYERS";

    private int addedPerExtraPlayer;
    private int maxExtraPlayers;

    public AdditionalPlayersControl(int addedPerExtraPlayer, int maxExtraPlayersCounted){
        this.addedPerExtraPlayer = addedPerExtraPlayer;
        this.maxExtraPlayers = maxExtraPlayersCounted;
    }

    @Override
    public int getChangeForSpawn(SpawnEventDetails details) {
        List<EntityPlayer> players = details.entity.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, details.entity.getEntityBoundingBox().grow(128));
        int extraPlayers = players.size() - 1;
        if(maxExtraPlayers>0){
            extraPlayers = Math.min(extraPlayers,maxExtraPlayers);
        }
        if(extraPlayers>0) {
            return addedPerExtraPlayer * extraPlayers;
        }
        return 0;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    public static Function<Configuration,List<DifficultyControl>> getFromConfig = config -> {
        List<DifficultyControl> returns = Lists.newArrayList();
        Property extraPlayersAffectsDifficultyEnabled = config.get(IDENTIFIER,
                "ExtraPlayersAffectsDifficulty", true, "Extra Players in the spawn radius changes the difficulty of a mob.  Each player past the first adds difficulty.");
        boolean extraPlayersAddsDifficulty = extraPlayersAffectsDifficultyEnabled.getBoolean();
        Property addedDifficultyPerExtraPlayerProp = config.get(IDENTIFIER,
                "ExtraPlayerAddedDifficulty", 20, "Difficulty added to a mob for each player past the first in the spawn radius (128 blocks).");
        int addedDifficultyPerExtraPlayer = addedDifficultyPerExtraPlayerProp.getInt();
        Property maxExtraPlayersProp = config.get(IDENTIFIER,
                "MaxExtraPlayersCounted",-1,"The maximum number of extra players to count.  If this is set to one, the extra difficulty gets applied at most once, and so on.  A Negative number or zero disables the maximum.");
        int maxExtraPlayersCounted = maxExtraPlayersProp.getInt();
        if (extraPlayersAddsDifficulty && addedDifficultyPerExtraPlayer > 0){
            returns.add(new AdditionalPlayersControl(addedDifficultyPerExtraPlayer,maxExtraPlayersCounted));
        }
        return returns;
    };
}
