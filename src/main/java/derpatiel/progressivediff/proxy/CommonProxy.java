package derpatiel.progressivediff.proxy;


import derpatiel.progressivediff.DifficultyConfiguration;
import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.EventHandler;
import derpatiel.progressivediff.ProgressiveDifficulty;
import derpatiel.progressivediff.network.PacketHandler;
import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("preInit COMMON");
        DifficultyConfiguration.config = new Configuration(event.getSuggestedConfigurationFile());
        DifficultyConfiguration.syncConfig();
    }

    public void init(FMLInitializationEvent event) {
        LOG.info("init COMMON");
        PacketHandler.init();
        if(DifficultyConfiguration.controlEnabled) {//don't register event handler if we're not enabled.
            MinecraftForge.EVENT_BUS.register(EventHandler.eventHandler);
        }
    }

    public void postInit(FMLPostInitializationEvent event) {
        LOG.info("postInit COMMON");
        DifficultyManager.generateWeightMap();
    }

}

