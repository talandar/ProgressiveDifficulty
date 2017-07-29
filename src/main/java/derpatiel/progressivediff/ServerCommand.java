package derpatiel.progressivediff;

import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by Jim on 5/7/2017.
 */
public class ServerCommand extends CommandBase {

    private String[] usage = new String[]{
            "progdiff (Progressive Difficulty) help:",
            "\"progdiff sync\" sync the config for the server",
            "    Useful for testing difficulty configs.",
            "\"progdiff killmodified\" kill modified mobs in the ",
            "    same dimension as the player",
            "\"progdiff killmobs\" kill all mobs in the ",
            "    same dimension as the player",
            "\"progdiff advancements\" print a list of all loaded",
            "    advancements to the configuration directory as",
            "    progdiff_advancements.txt",
            "    (filters out advancements under minecraft:recipes/*)",
            "\"progdiff region\" print the region the player is currently in"
    };

    @Override
    public String getName() {
        return "progdiff";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "\"progdiff [sync|killmodified|killmobs|advancements|region]\"";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length==0 || args.length>1){
            sendChat(sender,usage);
        }else if(args[0].equalsIgnoreCase("sync")){
            DifficultyManager.syncConfig();
            sendChat(sender, new String[]{"Synced config."});
        }else if(args[0].equalsIgnoreCase("killmodified")){
            sendChat(sender, new String[]{"Killing all modified mobs in this dimension."});
            MobNBTHandler.getModifiedEntities(sender.getEntityWorld()).stream().forEach(mob->mob.setDead());
        }else if(args[0].equalsIgnoreCase("killmobs")) {
            sendChat(sender, new String[]{"Killing all mobs in this dimension."});
            sender.getEntityWorld().getEntities(EntityLiving.class, (entity) -> !entity.isDead).stream().forEach(mob -> mob.setDead());
        }else if(args[0].equalsIgnoreCase("region")) {
            Region currentRegion = DifficultyManager.getRegionForPosition(sender.getPosition());
            sendChat(sender, new String[]{"Currently in region "+currentRegion.getName()});
        }else if(args[0].equalsIgnoreCase("advancements")){
            File configDir = DifficultyManager.getConfigDir();
            File advancementsFile = new File(configDir,"progdiff_advancements.txt");
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(advancementsFile))){
                for(Advancement advancement : sender.getServer().getAdvancementManager().getAdvancements()){
                    String id = advancement.getId().toString();
                    if(!id.startsWith("minecraft:recipes")) {
                        writer.write(advancement.getId().toString());
                        writer.newLine();
                    }
                }
            }catch(Exception e){
                sendChat(sender, new String[]{
                        "There was a problem writing the advancements file.",
                });
                if(advancementsFile.exists()){
                    try {
                        advancementsFile.delete();
                    }catch(Exception e2){
                        sendChat(sender, new String[]{
                                "There was a problem deleting the broken file.",
                                "Please manually delete the \"progdiff_advancements.txt\" file before trying again."
                        });
                    }
                }
            }

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
                    "killmodified",
                    "advancements",
                    "killmobs",
                    "region"
            };
            return CommandBase.getListOfStringsMatchingLastWord(args, validCompletions);
        }
        return CommandBase.getListOfStringsMatchingLastWord(args, new String[0]);
    }
}
