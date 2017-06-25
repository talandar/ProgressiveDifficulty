package derpatiel.progressivediff;

import derpatiel.progressivediff.modifiers.PiercingModifier;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandler {

    //sequence of events:
    //onEntitySpawn
    //OnSpecialSpawn(if relevant)
    //OnJoinWorld

    public static final EventHandler eventHandler = new EventHandler();

    //used for every spawn.  Including ones that are later canceled for light or similar.
    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn checkSpawnEvent){
        if(checkSpawnEvent.getEntityLiving() instanceof  EntityLiving) {
            DifficultyManager.onCheckSpawnEvent(checkSpawnEvent);
        }
    }

    //used by spawners only, after onEntitySpawn
    @SubscribeEvent
    public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn specialSpawnEvent){
        if(specialSpawnEvent.getEntity() instanceof EntityLiving) {
            DifficultyManager.onSpecialSpawnEvent(specialSpawnEvent);
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
    public void onEntityHurt(LivingAttackEvent event){
        if(event.getSource().getTrueSource() instanceof EntityLiving && event.getEntity() instanceof EntityPlayer && MobNBTHandler.isModifiedMob((EntityLiving) event.getSource().getTrueSource())){
            EntityLiving causeMob = (EntityLiving) event.getSource().getTrueSource();
            if(MobNBTHandler.getModifierLevel(causeMob, PiercingModifier.IDENTIFIER)>0){
                event.getSource().setDamageBypassesArmor();
                event.getSource().setDamageIsAbsolute();
                event.getSource().setMagicDamage();
            }

        }

    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        DifficultyManager.onWorldTick(event.world.provider.getDimension());
        MobUpkeepController.tick(event.world);
    }
}
