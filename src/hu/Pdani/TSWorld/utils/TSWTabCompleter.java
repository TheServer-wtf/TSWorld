package hu.Pdani.TSWorld.utils;

import hu.Pdani.TSWorld.TSWorldPlugin;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static hu.Pdani.TSWorld.TSWorldPlugin.c;
import static hu.Pdani.TSWorld.TSWorldPlugin.replaceLast;

public class TSWTabCompleter implements TabCompleter {
    private HashMap<CommandSender,Long> seeds = new HashMap<>();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("tsworld")) return null;
        List<String> tabs = new ArrayList<>();
        if(args.length <= 1){
            if(sender.hasPermission("tsworld.use")){
                if(sender.hasPermission("tsworld.admin")){
                    tabs.add("create");
                    tabs.add("delete");
                    tabs.add("confirm");
                    tabs.add("load");
                    tabs.add("unload");
                }
                if(sender.hasPermission("tsworld.tp")) tabs.add("tp");
                if(sender.hasPermission("tsworld.list")) tabs.add("list");
            }
        } else {
            String last = args[args.length-1];
            switch (args[0].toLowerCase()){
                case "d":
                case "delete":
                case "tp":
                case "u":
                case "unload":
                    for(World world : TSWorldPlugin.getTSWPlugin().getServer().getWorlds()){
                        if(world.getName().toLowerCase().startsWith(last.toLowerCase()))
                            tabs.add(world.getName());
                    }
                    break;
                case "c":
                case "create":
                case "l":
                case "load":
                    if(args.length >= 3){
                        String prev = args[args.length-2];
                        if(args.length-2 == 1)
                            prev = "";
                        int start = 1;
                        boolean closed = true;
                        if(args[1].startsWith("\"")){
                            if(!args[1].endsWith("\"")) {
                                closed = false;
                                for (int i = 2; i < args.length; i++) {
                                    start++;
                                    if (args[i].endsWith("\"")) {
                                        closed = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(!closed){
                            break;
                        }
                        boolean isQuote = false;
                        for(int i = 1+start;i < args.length;i++){
                            if(args[i].startsWith("\"")){
                                if(!args[i].endsWith("\""))
                                    isQuote = true;
                                continue;
                            }
                            if(isQuote){
                                if(!args[i].endsWith("\\\"")){
                                    if(args[i].endsWith("\"")){
                                        isQuote = false;
                                    }
                                }
                            }
                        }
                        if((last.equals("") || last.startsWith("-")) && !prev.startsWith("-") && !isQuote){
                            tabs.add("-type");
                            tabs.add("-structures");
                            tabs.add("-seed");
                            tabs.add("-environment");
                            tabs.add("-generator");
                            tabs.add("-gensettings");
                            tabs.add("-hardcore");
                            seeds.remove(sender);
                        }
                        if(prev.startsWith("-")){
                            switch (prev.replaceFirst("-","")){
                                case "structures":
                                case "structure":
                                case "hardcore":
                                case "hc":
                                    tabs.add("true");
                                    tabs.add("false");
                                    break;
                                case "environment":
                                case "env":
                                    for(World.Environment e : World.Environment.values()){
                                        tabs.add(e.toString());
                                    }
                                    break;
                                case "type":
                                case "t":
                                    for(WorldType w : WorldType.values()){
                                        tabs.add(w.toString());
                                    }
                                    break;
                                case "seed":
                                    if(!seeds.containsKey(sender)) {
                                        long leftLimit = 1L;
                                        long rightLimit = 999999999999999999L;
                                        long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
                                        seeds.put(sender, generatedLong);
                                    }
                                    tabs.add(String.valueOf(seeds.get(sender)));
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return tabs;
    }
}
