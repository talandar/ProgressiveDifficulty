package derpatiel.progressivediff.api;

import derpatiel.progressivediff.SpawnEventDetails;

public abstract class DifficultyControl {
    public abstract int getChangeForSpawn(SpawnEventDetails details);
    public abstract String getIdentifier();
}
