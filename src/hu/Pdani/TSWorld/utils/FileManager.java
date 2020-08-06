package hu.Pdani.TSWorld.utils;

import hu.Pdani.TSWorld.TSWorldPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static hu.Pdani.TSWorld.TSWorldPlugin.replaceLast;

public class FileManager {
    private static HashMap<String, File> files = new HashMap<>();
    private static HashMap<String, FileConfiguration> configs = new HashMap<>();
    public static FileConfiguration getConfig(String name){
        if(!configs.containsKey(name)){
            loadConfig(name);
        }
        return configs.get(name);
    }
    public static void loadConfig(String name){
        if(!files.containsKey(name)){
            File file = new File(TSWorldPlugin.getTSWPlugin().getDataFolder(),name+".yml");
            files.put(name,file);
        }
        configs.put(name, YamlConfiguration.loadConfiguration(files.get(name)));
    }
    public static void saveConfig(String name){
        try {
            getConfig(name).save(files.get(name));
        } catch (IOException e) {
            TSWorldPlugin.getTSWPlugin().getLogger().severe("Unable to save world config for world '"+name+"' !!!");
        }
    }
    public static void delConfig(String name){
        files.get(name).delete();
        files.remove(name);
        configs.remove(name);
    }
    public static List<String> getFiles(){
        List<String> files = new ArrayList<>();
        File dataFolder = TSWorldPlugin.getTSWPlugin().getDataFolder();
        if(dataFolder.exists() && dataFolder.isDirectory()){
            for(File file : dataFolder.listFiles()){
                files.add(replaceLast(file.getName(),".yml",""));
            }
        }
        return files;
    }
    public static File getFile(String name){
        if(!files.containsKey(name)){
            File file = new File(TSWorldPlugin.getTSWPlugin().getDataFolder(),name+".yml");
            files.put(name,file);
        }
        return files.get(name);
    }
}
