package derpatiel.progressivediff.network;

import derpatiel.progressivediff.ProgressiveDifficulty;
import derpatiel.progressivediff.util.ChatUtil;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ProgressiveDifficulty.MODID);

    public static void init() {
        int ID = 0;
        INSTANCE.registerMessage(ChatUtil.PacketNoSpamChat.Handler.class, ChatUtil.PacketNoSpamChat.class, ID++, Side.CLIENT);
        //INSTANCE.registerMessage(PacketFluidClick.Handler.class, PacketFluidClick.class, ID++, Side.SERVER);
        //INSTANCE.registerMessage(FluidChangedPacket.FluidChangedPacketMessageHandler.class, FluidChangedPacket.class, ID++, Side.CLIENT);
    }
}
