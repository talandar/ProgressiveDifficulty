package derpatiel.progressivediff;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public abstract class DifficultyControl {
    public abstract int getChangeForSpawn(SpawnEventDetails details);
}
