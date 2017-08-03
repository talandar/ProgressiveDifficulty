package derpatiel.progressivediff;

import derpatiel.progressivediff.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
        modid = ProgressiveDifficulty.MODID,
        version = ProgressiveDifficulty.VERSION,
        acceptedMinecraftVersions = "[1.12,1.13)",
        dependencies = "required-after:forge@[14.21.1.2394,);"

)
public class ProgressiveDifficulty
{
    public static final String MODID = "progressivedifficulty";
    public static final String VERSION = "1.12_2.0";

    public static boolean oldConfigExists = false;

    @Mod.Instance(MODID)
    public static ProgressiveDifficulty instance;

    public static CommonProxy proxy = new CommonProxy();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new ServerCommand());
    }
}
