package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import derpatiel.progressivediff.util.LOG;
import jline.internal.Log;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.swing.text.html.parser.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityFilter {

    private static String[] defaultBlacklist = new String[]{
            "Bat",
            "Squid"
    };

    private static String[] defaultMobSpawnCosts;

    private static boolean blacklistMode;
    private static final Set<String> mobList = Sets.newHashSet();
    private static final Map<String,Integer> mobSpawnCosts = Maps.newHashMap();

    public static void loadConfig(Configuration config){
        Property blacklistProp = config.get(Configuration.CATEGORY_GENERAL,"BlacklistMode",true,"All mobs are modified, except those that are in the blacklist.  If set to false, only those in the mob list are modified.  Boss-type mobs are never modified.");
        blacklistMode = blacklistProp.getBoolean();

        Property mobListProp = config.get(Configuration.CATEGORY_GENERAL,"MobList",defaultBlacklist,"List of mobs, either blacklist or whitelisted for modification by this mod.  See BlacklistMode.");
        mobList.clear();
        mobList.addAll(Sets.newHashSet(mobListProp.getStringList()));

        Property mobSpawnMapProp = config.get(Configuration.CATEGORY_GENERAL,"MobBaseDifficulty",generateDefaultSpawnCosts(),"A set of mob costs, of the format \"<mobRegistryName>:<cost>\".  " +
                "Provides bonus difficulty points to the mob before spawning if the number is positive.  If the number is negative, subtract that much difficulty from the mod before applying modifiers.  If the result after all controls is still negative, the value is used as the chance out of 100 that the mob spawn is cancelled entirely.");
        mobSpawnCosts.clear();
        Arrays.stream(mobSpawnMapProp.getStringList()).forEach(entry->{
            int index = entry.lastIndexOf(":");
            if(index<0) {
                LOG.error("Problem reading line for mob spawn cost.  Needed \"<mobRegistryName>:<cost>\", but string was " + entry);
            }else{
                String name = entry.substring(0,index);
                String valStr = entry.substring(index+1);
                try {
                    int value = Integer.parseInt(valStr);
                    mobSpawnCosts.put(name,value);
                }catch(Exception e){
                    LOG.error("Problem reading line for mob spawn cost.  Second parameter should have been integer, but was "+valStr);
                }
            }
                mobSpawnCosts.put(entry,0);
        });
        LOG.info(mobSpawnCosts.size()+" spawn costs found.");
    }

    private static String[] generateDefaultSpawnCosts(){
        List<String> lines = Lists.newArrayList();
        for(EntityEntry entry : ForgeRegistries.ENTITIES.getValues()){
            if(EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                lines.add(entry.getName()+":0");
            }
        }
        return lines.toArray(new String[lines.size()]);
    }

    public static boolean shouldModifyEntity(EntityLivingBase entity){
        if(entity==null || !entity.isNonBoss() || entity instanceof EntityPlayer)
            return false;
        if(mobList.contains(EntityList.getEntityString(entity))){
            return !blacklistMode;
        }
        return blacklistMode;
    }

    public static int getMobSpawnCost(EntityLivingBase entity){
        return mobSpawnCosts.getOrDefault(EntityList.getEntityString(entity),0);
    }
}
