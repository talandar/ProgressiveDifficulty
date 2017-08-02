package derpatiel.progressivediff.util;

import com.google.common.collect.Maps;
import derpatiel.progressivediff.ProgressiveDifficulty;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class PlayerNBTHandler {

    public static int getDifficultyForPlayerForController(EntityPlayer player,String identifier){
        NBTTagCompound tag = player.getEntityData().getCompoundTag(ProgressiveDifficulty.MODID);
        int difficulty = tag.getInteger(identifier);
        return difficulty;
    }

    public static void incrementDifficultyForPlayerForController(EntityPlayer player, String identifier, int increment){
        NBTTagCompound tag = player.getEntityData().getCompoundTag(ProgressiveDifficulty.MODID);
        int curVal = tag.getInteger(identifier);
        int newVal = curVal + increment;
        tag.setInteger(identifier,newVal);
        player.getEntityData().setTag(ProgressiveDifficulty.MODID,tag);
    }
}
