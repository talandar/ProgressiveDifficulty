package derpatiel.progressivediff;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import derpatiel.progressivediff.util.LOG;
import derpatiel.progressivediff.util.MobNBTHandler;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            "\"progdiff region\" print the region the player is currently in",
            "\"progdiff simulate [mobId]\" simulates the spawn of the given",
            "    mob at the player's location and returns the difficulty",
            "\"progdiff simulateDetailed [mobId]\" as with simulate, but",
            "    prints much more detailed results"
    };

    @Override
    public String getName() {
        return "progdiff";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "\"progdiff [sync|killmodified|killmobs|advancements|region|simulate|simulateDetailed]\"";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || args.length > 2) {
            sendChat(sender, usage);
        } else if (args[0].equalsIgnoreCase("sync")) {
            DifficultyManager.syncConfig();
            sendChat(sender, new String[]{"Synced config."});
        }else if(args[0].equalsIgnoreCase("simulate") || args[0].equalsIgnoreCase("simulateDetailed")){
            boolean detailed = args[0].equalsIgnoreCase("simulateDetailed");
            if(args.length<2){
                sendChat(sender, new String[]{"No mob provided.  Call this command with a mob to simulate. (try tab completion!)"});
                return;
            }
            if(args.length>2){
                sendChat(sender, new String[]{"Too many arguments to simulate/simulateDetailed."});
                return;
            }
            String mobType = args[1];
            Region senderRegion = DifficultyManager.getRegionForPosition(sender.getEntityWorld().provider.getDimension(),sender.getPosition());
            List<EntityEntry> matchingMob = ForgeRegistries.ENTITIES.getValues().stream()
                    .filter(entry->entry.getName().equalsIgnoreCase(mobType))
                    .collect(Collectors.toList());
            if(matchingMob.size()!=1){
                sendChat(sender, new String[]{"No matching mob found for "+mobType+".  Try using tab completion to find a mob to simulate."});
                return;
            }
            Entity fakeSpawned = matchingMob.get(0).newInstance(sender.getEntityWorld());
            fakeSpawned.setPosition(sender.getPosition().getX(),sender.getPosition().getY(),sender.getPosition().getZ());
            SpawnEventDetails fakeDetails = new SpawnEventDetails();
            fakeDetails.fromSpawner=false;
            fakeDetails.spawnEvent=null;
            fakeDetails.entity = (EntityLiving) fakeSpawned;
            Map<String, Integer> difficultyDetails = Maps.newHashMap();
            try {
                difficultyDetails.putAll(senderRegion.determineDifficultyForFakedSpawnEvent(fakeDetails));
            }catch(Exception e){
                sendChat(sender, new String[]{
                        "There was a problem simulating difficulty.",
                        "Please check the server log for details.",
                        });
                LOG.error(e.getMessage());
                LOG.error(ExceptionUtils.getStackTrace(e));
                return;
            }
            int totalDifficulty = 0;
            for(String key : difficultyDetails.keySet()){
                int thisControlDif = difficultyDetails.get(key);
                totalDifficulty+=thisControlDif;
                if(detailed){
                    sendChat(sender, new String[]{key+": "+thisControlDif+" points."});
                }
            }
            sendChat(sender, new String[]{totalDifficulty+" difficulty points"});
        }else if(args[0].equalsIgnoreCase("killmodified")){
            sendChat(sender, new String[]{"Killing all modified mobs in this dimension."});
            MobNBTHandler.getModifiedEntities(sender.getEntityWorld()).stream().forEach(mob->mob.setDead());
        }else if(args[0].equalsIgnoreCase("killmobs")) {
            sendChat(sender, new String[]{"Killing all mobs in this dimension."});
            sender.getEntityWorld().getEntities(EntityLiving.class, (entity) -> !entity.isDead).stream().forEach(mob -> mob.setDead());
        }else if(args[0].equalsIgnoreCase("region")) {
            Region currentRegion = DifficultyManager.getRegionForPosition(sender.getEntityWorld().provider.getDimension(),sender.getPosition());
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
                    "region",
                    "simulate",
                    "simulateDetailed"
            };
            return CommandBase.getListOfStringsMatchingLastWord(args, validCompletions);
        }
        if(args.length == 2 && args[0].startsWith("simulate")){
            String[] validCompletions = getMobList();
            return CommandBase.getListOfStringsMatchingLastWord(args, validCompletions);
        }
        return CommandBase.getListOfStringsMatchingLastWord(args, new String[0]);
    }

    private String[] getMobList(){
        List<String> lines = Lists.newArrayList();
        for(EntityEntry entry : ForgeRegistries.ENTITIES.getValues()){
            if(EntityLiving.class.isAssignableFrom(entry.getEntityClass())) {
                lines.add(entry.getName());
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
}
