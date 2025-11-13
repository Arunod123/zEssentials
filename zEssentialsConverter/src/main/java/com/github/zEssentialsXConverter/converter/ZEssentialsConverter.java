package com.github.zEssentialsXConverter.converter;

import com.github.zEssentialsXConverter.ZEssentialsXConverter;
import com.github.zEssentialsXConverter.data.Home;
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
        File zEssentialsUserFolder = new File(plugin.getServer().getUpdateFolder() + "/../zEssentials/users");
        if (!zEssentialsUserFolder.exists()) {
            sender.sendMessage("zEssentials user data folder not found!");
            return;
        }

        File essentialsUserFolder = new File(plugin.getServer().getUpdateFolder() + "/../Essentials/userdata");
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

        sender.sendMessage("Conversion process finished.");
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
