package derpatiel.progressivediff;

import derpatiel.progressivediff.api.DifficultyModifier;
import derpatiel.progressivediff.modifiers.PotionCloudModifier;
import derpatiel.progressivediff.util.ChatUtil;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Map;

public class EventHandler {

    //sequence of events:
    //onEntitySpawn
    //OnSpecialSpawn(if relevant - usually)
    //OnJoinWorld

    public static final EventHandler eventHandler = new EventHandler();

    //occurs for every spawn.  Including ones that are later canceled for light or similar.
    @SubscribeEvent
    public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn checkSpawnEvent){
        if(checkSpawnEvent.getEntityLiving() instanceof  EntityLiving) {
            DifficultyManager.onCheckSpawnEvent(checkSpawnEvent);
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent joinWorldEvent){
        //only catch if its EntityLiving - not a player but is a living entity
        //this lets us skip things like fallingsand entities, arrows, fx, etc
        if(joinWorldEvent.getEntity() instanceof EntityLiving) {
            DifficultyManager.onJoinWorldEvent(joinWorldEvent);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(ProgressiveDifficulty.oldConfigExists){
            TextComponentString linkComponent = new TextComponentString("[Progressive Difficulty Wiki]");
            ITextComponent[] chats = new ITextComponent[]{
                    new TextComponentString("[ProgressiveDifficulty] It looks like you have a version 1.0 " +
                            "config file. Please check out the Progressive Difficulty Wiki for instructions on how" +
                            " to migrate to a version 2.0 config file."),
                    linkComponent
            };
            ClickEvent goLinkEvent = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/talandar/ProgressiveDifficulty/wiki/2.0-Transition");
            linkComponent.getStyle().setClickEvent(goLinkEvent);
            linkComponent.getStyle().setColor(TextFormatting.BLUE);
            linkComponent.getStyle().setUnderlined(true);
            ChatUtil.sendChat(event.player,chats);
        }
    }

    @SubscribeEvent
    public void onEndermanTeleport(EnderTeleportEvent event){
        if(event.getEntity() instanceof EntityEnderman){
            event.getAttackDamage();
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event){
        Entity causeMob = event.getSource().getTrueSource();
        if(causeMob instanceof EntityLiving
                && event.getEntity() instanceof EntityPlayer
                && MobNBTHandler.isModifiedMob((EntityLiving)causeMob)){
            Map<String,Integer> changes = MobNBTHandler.getChangeMap((EntityLiving)causeMob);
            String regionName = MobNBTHandler.getEntityRegion((EntityLiving)causeMob);
            Region mobRegion = DifficultyManager.getRegionByName(regionName);
            String mobId = EntityList.getEntityString(causeMob);
            for(String change : changes.keySet()){
                try {
                    DifficultyModifier modifier = mobRegion.getModifierForMobById(mobId,change);
                    if (modifier != null) {
                        modifier.handleDamageEvent(event);
                    }
                }catch(Exception e){
                    LOG.warn("Error applying modifier at onLivingAttack.  Mob was "+causeMob.getName()+", Modifier was "+change+".  Please report to Progressive Difficulty Developer!");
                    LOG.warn("\tCaught Exception had message "+e.getMessage());
                }
            }
        }else if(event.getSource().getImmediateSource() instanceof PotionCloudModifier.PlayerEffectingOnlyEntityAreaEffectCloud
                && !(event.getEntity() instanceof EntityPlayer)){
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        DifficultyManager.onWorldTick(event.world.provider.getDimension());
        MobUpkeepController.tick(event.world);
    }
}
