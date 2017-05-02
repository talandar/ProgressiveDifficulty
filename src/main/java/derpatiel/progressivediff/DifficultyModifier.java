package derpatiel.progressivediff;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class DifficultyModifier {

    public abstract int getMinChange();
    public abstract int getMaxChange();
    public abstract void makeChange(int changeValue, EntityLivingBase entity);
}
