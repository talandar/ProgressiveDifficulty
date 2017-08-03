package derpatiel.progressivediff.proxy;


import derpatiel.progressivediff.DifficultyManager;
import derpatiel.progressivediff.EventHandler;
import derpatiel.progressivediff.ProgressiveDifficulty;
import derpatiel.progressivediff.network.PacketHandler;
import derpatiel.progressivediff.util.LOG;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("preInit COMMON");
        DifficultyManager.setBaseConfigDir(event.getModConfigurationDirectory());
        ProgressiveDifficulty.oldConfigExists=event.getSuggestedConfigurationFile().exists();
    }

    public void init(FMLInitializationEvent event) {
        LOG.info("init COMMON");
        PacketHandler.init();
        DifficultyManager.registerBaseModControlsAndModifiers();
        MinecraftForge.EVENT_BUS.register(EventHandler.eventHandler);
    }

    public void postInit(FMLPostInitializationEvent event) {
        LOG.info("postInit COMMON");
        //normally would do this in pre-init, but we need to let other mods add mobs and stuff
        //so we can key off them.
        DifficultyManager.syncConfig();
    }

}

