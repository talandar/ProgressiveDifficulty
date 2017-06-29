package derpatiel.progressivediff;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public abstract class DifficultyModifier {

    public abstract int getMaxInstances();
    public void handleUpkeepEvent(int numInstances, EntityLiving entity){}
    public void handleDamageEvent(LivingAttackEvent event){}
    public void handleSpawnEvent(int numInstances, EntityLiving entity){}
    public abstract int costPerChange();
    public abstract double getWeight();
    public abstract String getIdentifier();
    public boolean validForEntity(EntityLiving entity){
        return true;
    }
}
