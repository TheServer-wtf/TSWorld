package hu.Pdani.TSWorld;

import hu.Pdani.TSWorld.utils.TabCompleter;
import hu.Pdani.TSWorld.utils.Updater;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class TSWorldPlugin extends JavaPlugin {
    private static TSWorldPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Updater updater = new Updater("TheServer-wtf/TSWorld");
        if(updater.check()){
            getLogger().warning("There is a new version ("+updater.getLatest()+") available! Download it at https://github.com/"+updater.getRepo());
        } else {
            getLogger().info("You are running the latest version.");
        }
        PluginCommand command = getCommand("tsworld");
        if(command != null) {
            command.setExecutor(new CommandManager());
            command.setTabCompleter(new TabCompleter());
        }
        getLogger().info("The plugin is now enabled.");
    }
    @Override
    public void onDisable() {
        getLogger().info("The plugins is now disabled.");
    }

    public TSWorldPlugin getPlugin() {
        return getTSWPlugin();
    }
    public static TSWorldPlugin getTSWPlugin() {
        return plugin;
    }

    public static String c(String m){
        return ChatColor.translateAlternateColorCodes('&',m);
    }
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
