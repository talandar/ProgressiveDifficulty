package derpatiel.progressivediff.modifiers;

import derpatiel.progressivediff.DifficultyModifer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Created by Jim on 4/30/2017.
 */
public class AddHealthModifier extends DifficultyModifer {

    private int minAddedHearts;
    private int maxAddedHearts;
    private int diffCostPerHeart;

    public AddHealthModifier(int minAddedHearts, int maxAddedHearts, int diffCostPerHeart){
        this.minAddedHearts = minAddedHearts;
        this.maxAddedHearts = maxAddedHearts;
        this.diffCostPerHeart = diffCostPerHeart;
    }


    @Override
    public int getMinChange() {
        return 0;
    }

    @Override
    public int getMaxChange() {
        return 1000;
    }

    @Override
    public void makeChange(int changeValue, EntityLivingBase entity) {
        entity.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2000, 3, false, true));
    }
}
