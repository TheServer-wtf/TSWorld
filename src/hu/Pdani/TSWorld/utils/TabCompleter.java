package hu.Pdani.TSWorld.utils;

import hu.Pdani.TSWorld.TSWorldPlugin;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("tsworld")) return null;
        List<String> tabs = new ArrayList<>();
        if(args.length <= 1){
            if(sender.hasPermission("tsworld.use")){
                if(sender.hasPermission("tsworld.admin")){
                    tabs.add("create");
                    tabs.add("delete");
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
                        if(last.equals("") || last.startsWith("-")){
                            tabs.add("-type");
                            tabs.add("-structures");
                            tabs.add("-seed");
                            tabs.add("-environment");
                            tabs.add("-generator");
                            tabs.add("-gensettings");
                        }
                    }
                default:
                    break;
            }
        }
        return tabs;
    }
}
