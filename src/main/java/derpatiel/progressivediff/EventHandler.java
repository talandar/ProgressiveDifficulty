package derpatiel.progressivediff;

import derpatiel.progressivediff.util.LOG;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    //sequence of events:
    //onEntitySpawn
    //OnSpecialSpawn(if relevant)
    //OnJoinWorld

    public static final EventHandler eventHandler = new EventHandler();

    //used for every spawn.  Including ones that are later canceled for light or similar.
    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn checkSpawnEvent){
        //LOG.info("onEntitySpawn:" + checkSpawnEvent.getEntityLiving().getName() + ", at " + checkSpawnEvent.getX() + ", " + checkSpawnEvent.getY() + ", " + checkSpawnEvent.getZ());
        //checkSpawnEvent.setResult(Event.Result.DENY);
        //if(checkSpawnEvent.isCancelable()) {
        //    checkSpawnEvent.setCanceled(true);
        //}
        //how to allow spawner ones but not default ones...
        int difficulty = DifficultyManager.determineDifficultyForSpawnEvent(checkSpawnEvent);
        DifficultyManager.makeDifficultyChanges(checkSpawnEvent,difficulty);
    }

    //used by spawners only, after onEntitySpawn
    @SubscribeEvent
    public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn specialSpawnEvent){
        //LOG.info("onSpecialSpawn:" + specialSpawnEvent.getEntityLiving().getName() + ", at " + specialSpawnEvent.getX() + ", " + specialSpawnEvent.getY() + ", " + specialSpawnEvent.getZ());
        //specialSpawnEvent.setResult(Event.Result.ALLOW);
        //if(specialSpawnEvent.isCanceled()){
        //    specialSpawnEvent.setCanceled(false);
        //}
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent joinWorldEvent){
        //only catch if its EntityLiving - not a player but is a living entity
        //this lets us skip things like fallingsand entities, arrows, fx, etc
        if(joinWorldEvent.getEntity() instanceof EntityLiving) {
            //LOG.info("onJoinWorld:" + joinWorldEvent.getEntity().getName() + ", at " + joinWorldEvent.getEntity().getPosition().getX() + ", " + joinWorldEvent.getEntity().getPosition().getY() + ", " + joinWorldEvent.getEntity().getPosition().getZ());
        }
    }
}
