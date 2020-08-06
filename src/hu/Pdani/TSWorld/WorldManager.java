package hu.Pdani.TSWorld;

import hu.Pdani.TSWorld.utils.FileManager;
import hu.Pdani.TSWorld.utils.WorldException;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static hu.Pdani.TSWorld.TSWorldPlugin.replaceLast;

public class WorldManager {
    private static HashMap<String,String> custom = new HashMap<>();
    public static void startup(){
        List<String> files = FileManager.getFiles();
        if(!files.isEmpty()){
            for(String f : files){
                String name = replaceLast(f,".yml","");
                FileConfiguration config = FileManager.getConfig(name);
                if(!config.isSet("type")
                        || !config.isSet("generatorSettings")
                        || !config.isSet("environment")
                        || !config.isSet("structures")
                        || !config.isSet("seed"))
                    continue;
                String s_type = config.getString("type","NORMAL");
                String s_gen = config.getString("generator", null);
                String s_genset = config.getString("generatorSettings");
                String s_env = config.getString("environment","NORMAL");
                boolean struct = config.getBoolean("structures",true);
                long seed = config.getLong("seed");
                WorldCreator wc = new WorldCreator(name);
                WorldType type = WorldType.getByName(s_type);
                if(type == null) type = WorldType.NORMAL;
                wc.type(type);
                if(s_gen != null){
                    custom.put(name,s_gen);
                    wc.generator(s_gen);
                }
                wc.generatorSettings(s_genset);
                Environment environment = Environment.NORMAL;
                try {
                    environment = Environment.valueOf(s_env);
                }catch (IllegalArgumentException | NullPointerException ignore){}
                wc.generateStructures(struct);
                wc.seed(seed);
                wc.createWorld();
            }
        }
    }

    public static World createWorld(String name) throws WorldException {
        return createWorld(name,null);
    }
    public static World createWorld(String name, HashMap<String,String> args) throws WorldException {
        World check = TSWorldPlugin.getTSWPlugin().getServer().getWorld(name);
        if(check != null)
            throw new WorldException("This world already exists, and is loaded.");
        File wfile = new File(TSWorldPlugin.getTSWPlugin().getServer().getWorldContainer(),name);
        if(wfile.exists() && wfile.isDirectory())
            throw new WorldException("This world already exists, but isn't loaded.");
        WorldCreator wc = new WorldCreator(name);
        if(args != null && args.size() > 0){
            wc = setSettings(wc,args);
        }
        saveWorld(name,wc);
        return wc.createWorld();
    }

    public static World loadWorld(String name) throws WorldException {
        return loadWorld(name,null);
    }
    public static World loadWorld(String name, HashMap<String,String> args) throws WorldException {
        World check = TSWorldPlugin.getTSWPlugin().getServer().getWorld(name);
        if(check != null)
            throw new WorldException("This world already exists, and is loaded.");
        File wfile = new File(TSWorldPlugin.getTSWPlugin().getServer().getWorldContainer(),name);
        if(!wfile.exists())
            throw new WorldException("This world doesn't exists.");
        WorldCreator wc = new WorldCreator(name);
        if(args != null && args.size() > 0){
            wc = setSettings(wc,args);
        }
        saveWorld(name,wc);
        return TSWorldPlugin.getTSWPlugin().getServer().createWorld(wc);
    }

    public static boolean unloadWorld(String name) throws WorldException {
        World check = TSWorldPlugin.getTSWPlugin().getServer().getWorld(name);
        boolean unload = true;
        if(check != null) {
            if(TSWorldPlugin.getTSWPlugin().getServer().getWorlds().get(0).getName().equalsIgnoreCase(name))
                throw new WorldException("You can't unload the default world!");
            Location def = TSWorldPlugin.getTSWPlugin().getServer().getWorlds().get(0).getSpawnLocation();
            for(Player p : TSWorldPlugin.getTSWPlugin().getServer().getOnlinePlayers()){
                if(p.getWorld().getName().equalsIgnoreCase(name)){
                    p.teleport(def);
                }
            }
            unload = TSWorldPlugin.getTSWPlugin().getServer().unloadWorld(check, true);
            FileManager.delConfig(name);
        } else {
            throw new WorldException("The given world isn't loaded!");
        }
        return unload;
    }

    public static void deleteWorld(String name) throws WorldException {
        World check = TSWorldPlugin.getTSWPlugin().getServer().getWorld(name);
        boolean unload = true;
        if(check != null) {
            if(TSWorldPlugin.getTSWPlugin().getServer().getWorlds().get(0).getName().equalsIgnoreCase(name))
                throw new WorldException("You can't delete the default world!");
            Location def = TSWorldPlugin.getTSWPlugin().getServer().getWorlds().get(0).getSpawnLocation();
            for(Player p : TSWorldPlugin.getTSWPlugin().getServer().getOnlinePlayers()){
                if(p.getWorld().getName().equalsIgnoreCase(name)){
                    p.teleport(def);
                }
            }
            unload = TSWorldPlugin.getTSWPlugin().getServer().unloadWorld(check, false);
            FileManager.delConfig(name);
        }
        if(!unload)
            throw new WorldException("Failed to unload world!");
        File wfile = new File(TSWorldPlugin.getTSWPlugin().getServer().getWorldContainer(),name);
        if(!wfile.exists())
            throw new WorldException("The given world is already deleted!");
        boolean success = delete(wfile);
        if(!success)
            throw new WorldException("Unable to delete world directory, please delete it manually!");
    }

    private static WorldCreator setSettings(WorldCreator wc, HashMap<String,String> args){
        ArrayList<String> duplicate = new ArrayList<>();
        for(String key : args.keySet()){
            String value = args.get(key);
            switch (key.toLowerCase()){
                case "type":
                case "t":
                    if(duplicate.contains("type")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("type");
                    WorldType worldType = WorldType.getByName(value.toUpperCase());
                    if(worldType == null) worldType = WorldType.NORMAL;
                    wc = wc.type(worldType);
                    break;
                case "structures":
                case "structure":
                    if(duplicate.contains("struct")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("struct");
                    wc = wc.generateStructures(Boolean.parseBoolean(value));
                    break;
                case "seed":
                    if(duplicate.contains("seed")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("seed");
                    try {
                        wc = wc.seed(Long.parseLong(value));
                    } catch (NumberFormatException e){
                        duplicate.remove("seed");
                    }
                    break;
                case "environment":
                case "env":
                    if(duplicate.contains("env")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("env");
                    Environment environment = Environment.NORMAL;
                    try {
                        environment = Environment.valueOf(((String) value).toUpperCase());
                    } catch (IllegalArgumentException ignored){}
                    wc = wc.environment(environment);
                    break;
                case "generator":
                case "g":
                    if(duplicate.contains("gen")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("gen");
                    custom.put(wc.name(),value);
                    wc = wc.generator(value);
                    break;
                case "gensettings":
                case "gs":
                    if(duplicate.contains("genset")){
                        TSWorldPlugin.getTSWPlugin().getLogger().warning("Duplicate world option '"+key+"', with value: '"+value+"'");
                        break;
                    }
                    duplicate.add("genset");
                    TSWorldPlugin.getTSWPlugin().getLogger().info(value);
                    wc = wc.generatorSettings(value);
                    break;
                default:
                    TSWorldPlugin.getTSWPlugin().getLogger().warning("Invalid world option '"+key+"', with value: '"+value+"'");
                    break;
            }
        }
        return wc;
    }

    private static void saveWorld(String name, WorldCreator wc){
        FileConfiguration config = FileManager.getConfig(name);
        config.set("type",wc.type().getName());
        if(custom.containsKey(name))
            config.set("generator",custom.get(name));
        config.set("generatorSettings",wc.generatorSettings());
        config.set("environment",wc.environment().name());
        config.set("structures",wc.generateStructures());
        config.set("seed",wc.seed());
        try {
            config.save(FileManager.getFile(name));
        } catch (IOException e) {
            TSWorldPlugin.getTSWPlugin().getLogger().severe("Unable to save world config for world '"+name+"' !!!");
        }
    }

    private static boolean delete(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    delete(file);
                } else {
                    file.delete();
                }
            }
        }
        return(path.delete());
    }
}
