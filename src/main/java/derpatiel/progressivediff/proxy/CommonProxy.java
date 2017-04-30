package derpatiel.progressivediff.proxy;


import derpatiel.progressivediff.EventHandler;
import derpatiel.progressivediff.network.PacketHandler;
import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("preInit COMMON");
    }

    public void init(FMLInitializationEvent event) {
        LOG.info("init COMMON");
        PacketHandler.init();
        MinecraftForge.EVENT_BUS.register(EventHandler.eventHandler);
    }

    public void postInit(FMLPostInitializationEvent event) {
        LOG.info("postInit COMMON");
    }

}

