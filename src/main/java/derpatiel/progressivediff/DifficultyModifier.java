package derpatiel.progressivediff;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class DifficultyModifier {

    public abstract int getMaxInstances();
    public abstract void makeChange(int numInstances, EntityLiving entity, boolean isUpkeep);
    public abstract int costPerChange();
    public abstract double getWeight();
    public abstract String getIdentifier();
    public boolean validForEntity(EntityLiving entity){
        return true;
    }
}
