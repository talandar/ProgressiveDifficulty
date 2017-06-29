package derpatiel.progressivediff;

import derpatiel.progressivediff.modifiers.FieryModifier;
import derpatiel.progressivediff.modifiers.PiercingModifier;
import derpatiel.progressivediff.modifiers.VampiricModifier;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.entity.Entity;
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

import java.util.Map;

public class EventHandler {

    //sequence of events:
    //onEntitySpawn
    //OnSpecialSpawn(if relevant - usually)
    //OnJoinWorld

    public static final EventHandler eventHandler = new EventHandler();

    //occurs for every spawn.  Including ones that are later canceled for light or similar.
    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn checkSpawnEvent){
        if(checkSpawnEvent.getEntityLiving() instanceof  EntityLiving) {
            DifficultyManager.onCheckSpawnEvent(checkSpawnEvent);
        }
    }

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
    public void onLivingAttack(LivingAttackEvent event){
        Entity causeMob = event.getSource().getTrueSource();
        if(causeMob instanceof EntityLiving
                && event.getEntity() instanceof EntityPlayer
                && MobNBTHandler.isModifiedMob((EntityLiving)causeMob)){
            Map<String,Integer> changes = MobNBTHandler.getChangeMap((EntityLiving)causeMob);
            for(String change : changes.keySet()){
                DifficultyModifier modifier = DifficultyManager.getModifierById(change);
                if(modifier!=null) {
                    modifier.handleDamageEvent(event);
                }
            }
        }

    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        DifficultyManager.onWorldTick(event.world.provider.getDimension());
        MobUpkeepController.tick(event.world);
    }
}
