package derpatiel.progressivediff;

import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    //sequence of events:
    //onEntitySpawn
    //OnSpecialSpawn(if relevant)
    //OnJoinWorld

    public static final EventHandler eventHandler = new EventHandler();

    //used for every spawn - does this include spawners?
    @SubscribeEvent
    public void onEntitySpawn(LivingSpawnEvent.CheckSpawn checkSpawnEvent){
        LOG.info("onEntitySpawn:" + checkSpawnEvent.getEntityLiving().getName() + ", at " + checkSpawnEvent.getX() + ", " + checkSpawnEvent.getY() + ", " + checkSpawnEvent.getZ());
    }

    //used by spawners only
    @SubscribeEvent
    public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn specialSpawnEvent){
        LOG.info("onSpecialSpawn:" + specialSpawnEvent.getEntityLiving().getName() + ", at " + specialSpawnEvent.getX() + ", " + specialSpawnEvent.getY() + ", " + specialSpawnEvent.getZ());
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent joinWorldEvent){
        LOG.info("onJoinWorld:" + joinWorldEvent.getEntity().getName() + ", at " + joinWorldEvent.getEntity().getPosition().getX() + ", " + joinWorldEvent.getEntity().getPosition().getY() + ", " + joinWorldEvent.getEntity().getPosition().getZ());
    }
}
