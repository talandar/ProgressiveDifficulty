package derpatiel.progressivediff;

public abstract class DifficultyControl {
    public abstract int getChangeForSpawn(SpawnEventDetails details);
    public abstract String getIdentifier();
}
