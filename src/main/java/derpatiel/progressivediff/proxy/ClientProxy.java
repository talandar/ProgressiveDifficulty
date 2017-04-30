package derpatiel.progressivediff.proxy;

import derpatiel.progressivediff.util.LOG;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event){
        super.preInit(event);
        LOG.info("preInit CLIENT");
    }

    @Override
    public void init(FMLInitializationEvent event){
        super.init(event);
        LOG.info("init CLIENT");
    }

    @Override
    public void postInit(FMLPostInitializationEvent event){
        super.postInit(event);
        LOG.info("postInit CLIENT");
    }
}

