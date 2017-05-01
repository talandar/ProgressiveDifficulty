package derpatiel.progressivediff;

import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public abstract class DifficultyControl {
    public int getChangeForEvent(LivingSpawnEvent.CheckSpawn event, int currentDifficulty){
        return currentDifficulty;
    }
    public int getChangeForEvent(LivingSpawnEvent.SpecialSpawn event, int currentDifficulty){
        return currentDifficulty;
    }
    public abstract int getSortWeight();
}
