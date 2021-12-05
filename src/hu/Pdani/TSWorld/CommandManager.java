package hu.Pdani.TSWorld;

import hu.Pdani.TSWorld.utils.TSWorldException;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;

import static hu.Pdani.TSWorld.TSWorldPlugin.c;
import static hu.Pdani.TSWorld.TSWorldPlugin.replaceLast;

public class CommandManager implements CommandExecutor {
    private final HashMap<CommandSender,String> delConfirm = new HashMap<>();
    private final HashMap<CommandSender, BukkitTask> delTask = new HashMap<>();
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
                case "delete":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world>"));
                        break;
                    }
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
                    delConfirm.put(sender,name.toString());
                    if(delTask.containsKey(sender))
                        delTask.get(sender).cancel();
                    delTask.remove(sender);
                    int wait = 10;
                    sender.sendMessage(c("&eTo confirm the world deleting, please type in &b/"+alias+" confirm &e! &c(Resets after "+wait+" seconds)"));
                    BukkitTask task = TSWorldPlugin.getTSWPlugin().getServer().getScheduler().runTaskLaterAsynchronously(TSWorldPlugin.getTSWPlugin(),()->{
                        delConfirm.remove(sender);
                        delTask.remove(sender);
                        sender.sendMessage(c("&eWorld delete cancelled."));
                    },wait*20L);
                    delTask.put(sender,task);
                    break;
                case "confirm":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(!delConfirm.containsKey(sender)){
                        sender.sendMessage(c("&cThere is nothing to confirm."));
                        break;
                    }
                    if(delTask.containsKey(sender))
                        delTask.remove(sender).cancel();
                    try{
                        WorldManager.deleteWorld(delConfirm.remove(sender));
                    }catch (TSWorldException e){
                        sender.sendMessage(c("&cError: "+e.getMessage()));
                        break;
                    }
                    sender.sendMessage(c("&aWorld deleted."));
                    break;
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
                            closed = true;
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
                case "unload":
                    if (!sender.hasPermission("tsworld.admin")) return true;
                    if(args.length < 2){
                        sender.sendMessage(c("&7Usage: &9/"+alias+" "+exec+" <world>"));
                        break;
                    }
                    try {
                        closed = true;
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
                        if(args.length < 3) {
                            sender.sendMessage(c("&7Usage: &9/"+alias+" tp <world> [player]"));
                        } else {
                            Player target = TSWorldPlugin.getTSWPlugin().getServer().getPlayer(args[2]);
                            if(target == null) {
                                sender.sendMessage(c("&cError: Invalid player."));
                                break;
                            }
                            World world = TSWorldPlugin.getTSWPlugin().getServer().getWorld(args[1]);
                            if(world == null){
                                sender.sendMessage(c("&cError: The given world is not loaded."));
                                break;
                            }
                            sender.sendMessage(c("&aTeleporting..."));
                            target.teleport(world.getSpawnLocation());
                        }
                        break;
                    }
                    Player player = (Player) sender;
                    if(!player.hasPermission("tsworld.tp"))
                        break;
                    if(args.length < 2){
                        if(!player.hasPermission("tsworld.tp.others"))
                            sender.sendMessage(c("&7Usage: &9/"+alias+" tp <world>"));
                        else
                            sender.sendMessage(c("&7Usage: &9/"+alias+" tp <world> [player]"));
                        break;
                    }
                    String worldName = WorldManager.getWorldFromNick(args[1]);
                    if(worldName == null)
                        worldName = args[1];
                    World world = TSWorldPlugin.getTSWPlugin().getServer().getWorld(worldName);
                    if(world == null){
                        sender.sendMessage(c("&cError: The given world is not loaded."));
                        break;
                    }
                    if(args.length >= 3 && sender.hasPermission("tsworld.tp.others")){
                        Player target = TSWorldPlugin.getTSWPlugin().getServer().getPlayer(args[2]);
                        if(target == null) {
                            sender.sendMessage(c("&cError: Invalid player."));
                            break;
                        }
                        sender.sendMessage(c("&aTeleporting..."));
                        target.teleport(world.getSpawnLocation());
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
                        String nick = WorldManager.getNickname(w.getName());
                        if(nick != null)
                            sender.sendMessage("- '"+nick+"' == "+w.getName()+" ["+w.getEnvironment().name()+"]");
                        else
                            sender.sendMessage("- '"+w.getName()+"' ["+w.getEnvironment().name()+"]");
                    }
                    break;
                case "setspawn":
                    if(!(sender instanceof Player)) {
                        sender.sendMessage("In-game only.");
                        break;
                    }
                    player = (Player) sender;
                    if(!player.hasPermission("tsworld.admin"))
                        break;
                    world = player.getWorld();
                    world.setSpawnLocation(player.getLocation().add(0,1,0));
                    sender.sendMessage(c("&aSpawn location changed."));
                    break;
                case "nick":
                    if(sender.hasPermission("tsworld.admin")){
                        if(args.length < 2) {
                            sender.sendMessage(c("&7Usage: &9/"+alias+" nick <world> [nickname]"));
                        } else {
                            world = TSWorldPlugin.getTSWPlugin().getServer().getWorld(args[1]);
                            if(world == null){
                                sender.sendMessage(c("&cError: The given world is not loaded."));
                                break;
                            }
                            if(args.length == 3){
                                try {
                                    WorldManager.setNickname(world.getName(),args[2]);
                                    sender.sendMessage(c("&aNickname updated."));
                                } catch (TSWorldException e) {
                                    sender.sendMessage(c("&cError: "+e.getMessage()));
                                }
                            } else {
                                try {
                                    WorldManager.setNickname(world.getName(),null);
                                    sender.sendMessage(c("&aNickname removed."));
                                } catch (TSWorldException e) {
                                    sender.sendMessage(c("&cError: "+e.getMessage()));
                                }
                            }
                        }
                    }
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
        sender.sendMessage(c("&eTSWorld plugin v"+version+" created by "+authors.get(0)));
    }
    private void sendHelp(CommandSender sender, String alias){
        if(!sender.hasPermission("tsworld.use"))
            return;
        if(sender.hasPermission("tsworld.admin")) {
            sender.sendMessage(c("&7- &9/" + alias + " create <world> [options] &7- &6Creates a new world with the given options"));
            sender.sendMessage(c("&7- &9/" + alias + " delete <world> &7- &6Deletes the given world"));
            sender.sendMessage(c("&c&lWorld deletion is irreversible! Make sure to copy the world file before deleting it!"));
            sender.sendMessage(c("&7- &9/" + alias + " load <world> [options] &7- &6Loads the given world from the disk, with the given options"));
            sender.sendMessage(c("&7- &9/" + alias + " unload <world> &7- &6Unloads the given world"));
            sender.sendMessage(c("&7- &9/" + alias + " setspawn &7- &6Set the world spawn location to your position"));
            sender.sendMessage(c("&7- &9/" + alias + " nick <world> [nickname] &7- &6Set/Unset the world nickname used in the &9tp &6command"));
        }
        if(sender.hasPermission("tsworld.tp")) {
            if(!sender.hasPermission("tsworld.tp.others"))
                sender.sendMessage(c("&7- &9/" + alias + " tp <world> &7- &6Teleport to the given worlds spawn location"));
            else
                sender.sendMessage(c("&7- &9/" + alias + " tp <world> [player] &7- &6Teleport the specified player to the given world"));
        }
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

    public void stopTasks(){
        for(CommandSender sender : delTask.keySet()){
            delTask.get(sender).cancel();
            delTask.remove(sender);
            delConfirm.remove(sender);
            if(sender instanceof Player){
                Player player = (Player) sender;
                if(player.isOnline()){
                    player.sendMessage(c("&eWorld delete cancelled, due to plugin reload."));
                }
            }
        }
    }
}
