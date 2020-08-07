package hu.Pdani.TSWorld;

import hu.Pdani.TSWorld.utils.TSWorldException;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static hu.Pdani.TSWorld.TSWorldPlugin.c;
import static hu.Pdani.TSWorld.TSWorldPlugin.replaceLast;

public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("tsworld")) return true;
        if(args.length == 0){
            sendInfo(sender);
            if (sender.hasPermission("tsworld.use")) sendHelp(sender,alias);
        }
        if(args.length > 0) {
            if (!sender.hasPermission("tsworld.use")){
                sendInfo(sender);
                return true;
            }
            StringBuilder name = new StringBuilder();
            String exec = args[0];
            switch (exec.toLowerCase()){
                case "c":
                case "create":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world> [options]"));
                        break;
                    }
                    try {
                        if(args.length == 2) {
                            sender.sendMessage(c("&eCreating world '" + args[1] + "'..."));
                            WorldManager.createWorld(args[1]);
                        }
                        if(args.length > 2){
                            boolean closed = true;
                            int start = 1;
                            if(args[1].startsWith("\"")){
                                if(!args[1].endsWith("\"")) {
                                    name.append(args[1].replaceFirst("\"",""));
                                    closed = false;
                                    for (int i = 2; i < args.length; i++) {
                                        name.append("_");
                                        start++;
                                        if (args[i].endsWith("\"")) {
                                            closed = true;
                                            name.append(replaceLast(args[i], "\"", ""));
                                            break;
                                        } else {
                                            name.append(args[i]);
                                        }
                                    }
                                } else {
                                    name.append(replaceLast(args[1].replaceFirst("\"",""),"\"",""));
                                }
                            }
                            if(!closed){
                                sender.sendMessage(c("&cError: Invalid world name."));
                                break;
                            }
                            sender.sendMessage(c("&eCreating world '"+name.toString()+"'..."));
                            WorldManager.createWorld(name.toString(),getOptions(args,start));
                        }
                    } catch (TSWorldException e) {
                        sender.sendMessage(c("&cError: "+e.getMessage()));
                        break;
                    }
                    sender.sendMessage(c("&aWorld created."));
                    break;
                case "d":
                case "delete":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world>"));
                        break;
                    }
                    try {
                        boolean closed = true;
                        if(args[1].startsWith("\"")){
                            if(!args[1].endsWith("\"")) {
                                name.append(args[1].replaceFirst("\"", ""));
                                closed = false;
                                for (int i = 2; i < args.length; i++) {
                                    name.append("_");
                                    if (args[i].endsWith("\"")) {
                                        closed = true;
                                        name.append(replaceLast(args[i], "\"", ""));
                                        break;
                                    } else {
                                        name.append(args[i]);
                                    }
                                }
                            } else {
                                name.append(replaceLast(args[1].replaceFirst("\"",""),"\"",""));
                            }
                        } else {
                            name.append(args[1]);
                        }
                        if(!closed){
                            sender.sendMessage(c("&cError: Invalid world name."));
                            break;
                        }
                        WorldManager.deleteWorld(name.toString());
                    } catch (TSWorldException e) {
                        sender.sendMessage(c("&cError: "+e.getMessage()));
                        break;
                    }
                    sender.sendMessage(c("&aWorld deleted."));
                    break;
                case "l":
                case "load":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world>"));
                        break;
                    }
                    try {
                        if(args.length == 2) {
                            sender.sendMessage(c("&eLoading world '" + args[1] + "'..."));
                            WorldManager.loadWorld(args[1]);
                        }
                        if(args.length > 2){
                            boolean closed = true;
                            int start = 1;
                            if(args[1].startsWith("\"")){
                                if(!args[1].endsWith("\"")) {
                                    name.append(args[1].replaceFirst("\"", ""));
                                    closed = false;
                                    for (int i = 2; i < args.length; i++) {
                                        name.append("_");
                                        start++;
                                        if (args[i].endsWith("\"")) {
                                            closed = true;
                                            name.append(replaceLast(args[i], "\"", ""));
                                            break;
                                        } else {
                                            name.append(args[i]);
                                        }
                                    }
                                }
                            } else {
                                name.append(replaceLast(args[1].replaceFirst("\"",""),"\"",""));
                            }
                            if(!closed){
                                sender.sendMessage(c("&cError: Invalid world name."));
                                break;
                            }
                            sender.sendMessage(c("&eLoading world '"+name.toString()+"'..."));
                            WorldManager.loadWorld(name.toString(),getOptions(args,start));
                        }
                    } catch (TSWorldException e) {
                        sender.sendMessage(c("&cError: "+e.getMessage()));
                        break;
                    }
                    sender.sendMessage(c("&aWorld loaded."));
                    break;
                case "u":
                case "unload":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world>"));
                        break;
                    }
                    try {
                        boolean closed = true;
                        if(args[1].startsWith("\"")){
                            if(!args[1].endsWith("\"")) {
                                name.append(args[1].replaceFirst("\"", ""));
                                closed = false;
                                for (int i = 2; i < args.length; i++) {
                                    name.append("_");
                                    if (args[i].endsWith("\"")) {
                                        closed = true;
                                        name.append(replaceLast(args[i], "\"", ""));
                                        break;
                                    } else {
                                        name.append(args[i]);
                                    }
                                }
                            } else {
                                name.append(replaceLast(args[1].replaceFirst("\"",""),"\"",""));
                            }
                        } else {
                            name.append(args[1]);
                        }
                        if(!closed){
                            sender.sendMessage(c("&cError: Invalid world name."));
                            break;
                        }
                        WorldManager.unloadWorld(name.toString());
                    } catch (TSWorldException e) {
                        sender.sendMessage(c("&cError: "+e.getMessage()));
                        break;
                    }
                    sender.sendMessage(c("&aWorld unloaded."));
                    break;
                case "tp":
                    if(!(sender instanceof Player)) {
                        sender.sendMessage("In-game only.");
                        break;
                    }
                    Player player = (Player) sender;
                    if(!player.hasPermission("tsworld.tp"))
                        break;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" tp <world>"));
                        break;
                    }
                    World world = TSWorldPlugin.getTSWPlugin().getServer().getWorld(args[1]);
                    if(world == null){
                        sender.sendMessage(c("&cError: The given world is not loaded."));
                        break;
                    }
                    sender.sendMessage(c("&aTeleporting..."));
                    player.teleport(world.getSpawnLocation());
                    break;
                case "list":
                    if(!sender.hasPermission("tsworld.list"))
                        break;
                    List<World> worldList = TSWorldPlugin.getTSWPlugin().getServer().getWorlds();
                    sender.sendMessage(c("&fLoaded worlds ("+worldList.size()+"): "));
                    for(World w : worldList){
                        sender.sendMessage("- '"+w.getName()+"' ("+w.getEnvironment().name()+")");
                    }
                    break;
                case "ss":
                case "setspawn":
                    if(!(sender instanceof Player)) {
                        sender.sendMessage("In-game only.");
                        break;
                    }
                    player = (Player) sender;
                    if(!player.hasPermission("tsworld.admin"))
                        break;
                    world = player.getWorld();
                    world.setSpawnLocation(player.getLocation());
                    sender.sendMessage(c("&aSpawn location changed."));
                    break;
                default:
                    sendInfo(sender);
                    sendHelp(sender, alias);
                    break;
            }
        }
        return true;
    }
    private void sendInfo(CommandSender sender){
        PluginDescriptionFile pdf = TSWorldPlugin.getTSWPlugin().getDescription();
        String version = pdf.getVersion();
        List<String> authors = pdf.getAuthors();
        String longauthors = pdf.getAuthors().stream().collect(Collectors.joining(", ", "", ""));
        String description = pdf.getDescription();
        sender.sendMessage(c("&eTSWorld plugin v"+version+" created by "+authors.get(0)));
    }
    private void sendHelp(CommandSender sender, String alias){
        if(!sender.hasPermission("tsworld.use"))
            return;
        if(sender.hasPermission("tsworld.admin")) {
            sender.sendMessage(c("&7- &9/" + alias + " &bc&9reate <world> [options] &7- &6Creates a new world with the given options"));
            sender.sendMessage(c("&7- &9/" + alias + " &bd&9elete <world> &7- &6Deletes the given world (&c&lIRREVERSIBLE!!! Copy the world file before running this!&6)"));
            sender.sendMessage(c("&7- &9/" + alias + " &bl&9oad <world> [options] &7- &6Loads the given world from the disk, with the given options"));
            sender.sendMessage(c("&7- &9/" + alias + " &bu&9nload <world> &7- &6Unloads the given world"));
            sender.sendMessage(c("&7- &9/" + alias + " &bs&9et&bs&9pawn &7- &6Set the world spawn location to your position"));
        }
        if(sender.hasPermission("tsworld.tp"))
            sender.sendMessage(c("&7- &9/"+alias+" tp <world> &7- &6Teleport to the given worlds spawn location"));
        if(sender.hasPermission("tsworld.list"))
            sender.sendMessage(c("&7- &9/"+alias+" list &7- &6List all the loaded worlds"));
    }

    private HashMap<String,String> getOptions(String[] args, int start){
        HashMap<String,String> options = new HashMap<>();
        String key = null;
        StringBuilder value = new StringBuilder();
        boolean next = false;
        for(int i = 1+start;i < args.length;i++){
            if(args[i].startsWith("-")){
                key = args[i].replaceFirst("-","");
                continue;
            }
            if(key == null)
                continue;
            if(!next)
                value = new StringBuilder();
            if(args[i].startsWith("\"")){
                if(args[i].endsWith("\"")){
                    value.append(replaceLast(args[i].replaceFirst("\"","").replace("\\\"","\""),"\"",""));
                    options.put(key,value.toString());
                    key = null;
                } else {
                    next = true;
                    value.append(args[i].replaceFirst("\"", "").replace("\\\"","\""));
                }
                continue;
            }
            if(next){
                value.append(" ");
                if(!args[i].endsWith("\\\"")){
                    if(args[i].endsWith("\"")) {
                        next = false;
                        value.append(replaceLast(args[i].replace("\\\"","\""),"\"",""));
                        options.put(key,value.toString());
                        key = null;
                    } else {
                        value.append(args[i].replace("\\\"","\""));
                    }
                } else {
                    value.append(args[i].replace("\\\"","\""));
                }
            } else {
                value.append(args[i].replace("\\\"","\""));
                options.put(key,value.toString());
                key = null;
            }
        }
        return options;
    }
}
