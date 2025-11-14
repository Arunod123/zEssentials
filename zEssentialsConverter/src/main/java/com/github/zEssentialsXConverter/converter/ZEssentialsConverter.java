package com.github.zEssentialsXConverter.converter;

import com.github.zEssentialsXConverter.ZEssentialsXConverter;
import com.github.zEssentialsXConverter.data.ConfigStorage;
import com.github.zEssentialsXConverter.data.Home;
import com.github.zEssentialsXConverter.data.Warp;
import com.github.zEssentialsXConverter.data.ZUser;
import com.google.gson.Gson;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

public class ZEssentialsConverter {

    private final ZEssentialsXConverter plugin;

    public ZEssentialsConverter(ZEssentialsXConverter plugin) {
        this.plugin = plugin;
    }

    public void convert(CommandSender sender) {
        File zEssentialsUserFolder = new File(plugin.getDataFolder().getParentFile(), "zEssentials/users");
        if (!zEssentialsUserFolder.exists()) {
            sender.sendMessage("zEssentials user data folder not found!");
            return;
        }

        File essentialsUserFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials/userdata");
        if (!essentialsUserFolder.exists()) {
            essentialsUserFolder.mkdirs();
        }

        File[] userFiles = zEssentialsUserFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (userFiles == null || userFiles.length == 0) {
            sender.sendMessage("No zEssentials user data found to convert.");
            return;
        }

        sender.sendMessage("Found " + userFiles.length + " user files to convert.");

        for (File userFile : userFiles) {
            try {
                convertUser(userFile, essentialsUserFolder);
            } catch (Exception e) {
                sender.sendMessage("Error converting user data from file " + userFile.getName() + ": " + e.getMessage());
            }
        }

        convertWarps(sender);

        sender.sendMessage("Conversion process finished.");
    }

    private void convertWarps(CommandSender sender) {
        File zEssentialsConfigFile = new File(plugin.getDataFolder().getParentFile(), "zEssentials/configstorage.json");
        if (!zEssentialsConfigFile.exists()) {
            sender.sendMessage("zEssentials configstorage.json not found!");
            return;
        }

        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(zEssentialsConfigFile);
            ConfigStorage configStorage = gson.fromJson(reader, ConfigStorage.class);
            reader.close();

            File essentialsWarpFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials/warps");
            if (!essentialsWarpFolder.exists()) {
                essentialsWarpFolder.mkdirs();
            }

            if (configStorage.getWarps() != null && !configStorage.getWarps().isEmpty()) {
                for (Warp warp : configStorage.getWarps()) {
                    File essentialsWarpFile = new File(essentialsWarpFolder, warp.getName() + ".yml");
                    YamlConfiguration essentialsConfig = new YamlConfiguration();
                    essentialsConfig.set("world", warp.getLocation().getLocation().getWorld().getName());
                    essentialsConfig.set("x", warp.getLocation().getLocation().getX());
                    essentialsConfig.set("y", warp.getLocation().getLocation().getY());
                    essentialsConfig.set("z", warp.getLocation().getLocation().getZ());
                    essentialsConfig.set("yaw", warp.getLocation().getLocation().getYaw());
                    essentialsConfig.set("pitch", warp.getLocation().getLocation().getPitch());
                    essentialsConfig.save(essentialsWarpFile);
                }
                sender.sendMessage("Converted " + configStorage.getWarps().size() + " warps.");
            } else {
                sender.sendMessage("No warps found to convert.");
            }

        } catch (IOException e) {
            sender.sendMessage("Error converting warps: " + e.getMessage());
        }
    }

    private void convertUser(File userFile, File essentialsUserFolder) throws IOException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(userFile);
        ZUser zUser = gson.fromJson(reader, ZUser.class);
        reader.close();

        File essentialsUserFile = new File(essentialsUserFolder, zUser.getUniqueId() + ".yml");
        YamlConfiguration essentialsConfig = new YamlConfiguration();

        essentialsConfig.set("last-account-name", zUser.getName());
        essentialsConfig.set("money", zUser.getBalances().getOrDefault("money", BigDecimal.ZERO).toString());

        if (zUser.getHomes() != null && !zUser.getHomes().isEmpty()) {
            ConfigurationSection homesSection = essentialsConfig.createSection("homes");
            for (Home home : zUser.getHomes()) {
                ConfigurationSection homeSection = homesSection.createSection(home.getName());
                homeSection.set("world-name", home.getLocation().getWorld().getName());
                homeSection.set("x", home.getLocation().getX());
                homeSection.set("y", home.getLocation().getY());
                homeSection.set("z", home.getLocation().getZ());
                homeSection.set("yaw", home.getLocation().getYaw());
                homeSection.set("pitch", home.getLocation().getPitch());
            }
        }

        essentialsConfig.save(essentialsUserFile);
    }
}
