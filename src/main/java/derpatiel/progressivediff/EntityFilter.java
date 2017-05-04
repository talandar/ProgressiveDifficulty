package derpatiel.progressivediff;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.swing.text.html.parser.Entity;
import java.util.Set;

public class EntityFilter {

    private static String[] defaultBlacklist = new String[]{
            "Bat",
            "Squid"
    };

    private static boolean blacklistMode;
    private static Set<String> mobList;

    public static void loadConfig(Configuration config){
        Property blacklistProp = config.get(Configuration.CATEGORY_GENERAL,"BlacklistMode",true,"All mobs are modified, except those that are in the blacklist.  If set to false, only those in the mob list are modified.  Boss-type mobs are never modified.");
        blacklistMode = blacklistProp.getBoolean();

        Property mobListProp = config.get(Configuration.CATEGORY_GENERAL,"MobList",defaultBlacklist,"List of mobs, either blacklist or whitelisted for modification by this mod.  See BlacklistMode.");
        mobList = Sets.newHashSet(mobListProp.getStringList());
    }

    public static boolean shouldModifyEntity(EntityLivingBase entity){
        if(entity==null || !entity.isNonBoss() || entity instanceof EntityPlayer)
            return false;
        if(mobList.contains(EntityList.getEntityString(entity))){
            return !blacklistMode;
        }
        return blacklistMode;
    }
}
