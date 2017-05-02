package derpatiel.progressivediff;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class SpawnEventDetails {

    public EntityLiving entity;
    public boolean fromSpawner;

    public LivingSpawnEvent.CheckSpawn spawnEvent;
}
