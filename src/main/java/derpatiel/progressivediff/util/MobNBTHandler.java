package derpatiel.progressivediff.util;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class MobNBTHandler {

    private static final String ROOT_NBT_KEY = "progdiff";
    private static final String REGION_KEY = "region";

    public static boolean isModifiedMob(EntityLiving entity){
        return entity.getEntityData().hasKey(ROOT_NBT_KEY);
    }

    public static Map<String,Integer> getChangeMap(EntityLiving entity) {
        Map<String,Integer> changeMap = Maps.newHashMap();
        NBTTagCompound compound = entity.getEntityData().getCompoundTag(ROOT_NBT_KEY);
        for(String key : compound.getKeySet()){
            changeMap.put(key,compound.getInteger(key));
        }
        return changeMap;
    }

    public static int getModifierLevel(EntityLiving entity, String modifierId){
        Map<String,Integer> changeMap = Maps.newHashMap();
        NBTTagCompound compound = entity.getEntityData().getCompoundTag(ROOT_NBT_KEY);
        if(compound.hasKey(modifierId)){
            return compound.getInteger(modifierId);
        }else{
            return 0;
        }
    }

    public static void setChangeMap(EntityLiving entity, String region, Map<String,Integer> changeMap){
        NBTTagCompound compound = new NBTTagCompound();
        for(String id : changeMap.keySet()){
            int num = changeMap.get(id);
            compound.setInteger(id,num);
        }
        compound.setString(REGION_KEY,region);
        entity.getEntityData().setTag(ROOT_NBT_KEY,compound);
    }

    public static List<EntityLiving> getModifiedEntities(World world){
        return world.getEntities(EntityLiving.class,(entity)-> MobNBTHandler.isModifiedMob(entity) && !entity.isDead);
    }

    public static String getEntityRegion(EntityLiving entity) {
        NBTTagCompound compound = entity.getEntityData().getCompoundTag(ROOT_NBT_KEY);
        String region = compound.getString(REGION_KEY);
        if(region==null){
            region="default";
        }
        return region;
    }
}
