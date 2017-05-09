package derpatiel.progressivediff;

import derpatiel.progressivediff.util.LOG;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jim on 5/7/2017.
 */
public class ServerCommand extends CommandBase {

    private String[] usage = new String[]{
            "progdiff (Progressive Difficulty) help:",
            "\"progdiff sync\" sync the config for the server",
            "\tUseful for testing difficulty configs.",
    };

    @Override
    public String getName() {
        return "progdiff";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "\"progdiff sync\"";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length==0 || args.length>1){
            sendChat(sender,usage);
        }
        if(args[0].equalsIgnoreCase("sync")){
            DifficultyConfiguration.syncConfig();
            sendChat(sender, new String[]{"Synced config."});
        /*}else if(args[0].equalsIgnoreCase("entities")){
            for(EntityEntry entry : ForgeRegistries.ENTITIES.getValues()){
                if(EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                    LOG.info(entry.getName());
                }
            }
            sendChat(sender, new String[]{"See Log for entity list."});
            */
        }else{
            sendChat(sender,usage);
        }
    }

    private void sendChat(ICommandSender sender, String[] msg){
        EntityPlayer player = (EntityPlayer)sender.getCommandSenderEntity();
        for (String str : msg)
        {
            TextComponentString line = new TextComponentString(str);
            player.sendMessage(line);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            String[] validCompletions = new String[]{
                    "sync",
                    "entities"
            };
            return CommandBase.getListOfStringsMatchingLastWord(args, validCompletions);
        }
        return CommandBase.getListOfStringsMatchingLastWord(args, new String[0]);
    }
}
