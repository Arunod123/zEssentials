package com.github.zEssentialsXConverter.converter;

import com.github.zEssentialsXConverter.ZEssentialsXConverter;
import com.github.zEssentialsXConverter.data.ConfigStorage;
import com.github.zEssentialsXConverter.data.Home;
import com.github.zEssentialsXConverter.data.Warp;
import com.github.zEssentialsXConverter.data.ZUser;
import com.github.zEssentialsXConverter.database.DatabaseConnection;
import com.google.gson.Gson;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ZEssentialsConverter {

    private final ZEssentialsXConverter plugin;
    private DatabaseConnection databaseConnection;

    public ZEssentialsConverter(ZEssentialsXConverter plugin) {
        this.plugin = plugin;
    }

    public void convert(CommandSender sender) {
        File zEssentialsConfigFile = new File(plugin.getDataFolder().getParentFile(), "zEssentials/config.yml");
        if (!zEssentialsConfigFile.exists()) {
            sender.sendMessage("zEssentials config.yml not found!");
            return;
        }

        FileConfiguration zEssentialsConfig = YamlConfiguration.loadConfiguration(zEssentialsConfigFile);
        String host = zEssentialsConfig.getString("database-configuration.host");
        int port = zEssentialsConfig.getInt("database-configuration.port");
        String database = zEssentialsConfig.getString("database-configuration.database");
        String username = zEssentialsConfig.getString("database-configuration.user");
        String password = zEssentialsConfig.getString("database-configuration.password");

        this.databaseConnection = new DatabaseConnection(host, port, database, username, password);

        try (Connection connection = this.databaseConnection.getConnection()) {
            sender.sendMessage("Successfully connected to the zEssentials database.");

            File essentialsUserFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials/userdata");
            if (!essentialsUserFolder.exists()) {
                essentialsUserFolder.mkdirs();
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM zessentials_users");
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String name = resultSet.getString("name");
                    double balance = resultSet.getDouble("balance");

                    File essentialsUserFile = new File(essentialsUserFolder, uuid + ".yml");
                    YamlConfiguration essentialsConfig = new YamlConfiguration();
                    essentialsConfig.set("last-account-name", name);
                    essentialsConfig.set("money", balance);

                    try (PreparedStatement homesStatement = connection.prepareStatement("SELECT * FROM zessentials_homes WHERE user_uuid = ?");
                    ) {
                        homesStatement.setString(1, uuid.toString());
                        try (ResultSet homesResultSet = homesStatement.executeQuery()) {
                            if (homesResultSet.isBeforeFirst()) {
                                org.bukkit.configuration.ConfigurationSection homesSection = essentialsConfig.createSection("homes");
                                while (homesResultSet.next()) {
                                    String homeName = homesResultSet.getString("name");
                                    String world = homesResultSet.getString("world");
                                    double x = homesResultSet.getDouble("x");
                                    double y = homesResultSet.getDouble("y");
                                    double z = homesResultSet.getDouble("z");
                                    float yaw = homesResultSet.getFloat("yaw");
                                    float pitch = homesResultSet.getFloat("pitch");

                                    org.bukkit.configuration.ConfigurationSection homeSection = homesSection.createSection(homeName);
                                    homeSection.set("world", world);
                                    homeSection.set("x", x);
                                    homeSection.set("y", y);
                                    homeSection.set("z", z);
                                    homeSection.set("yaw", yaw);
                                    homeSection.set("pitch", pitch);
                                }
                            }
                        }
                    }

                    essentialsConfig.save(essentialsUserFile);
                }
            }
            sender.sendMessage("User data conversion complete.");

            File essentialsWarpFolder = new File(plugin.getDataFolder().getParentFile(), "Essentials/warps");
            if (!essentialsWarpFolder.exists()) {
                essentialsWarpFolder.mkdirs();
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM zessentials_warps");
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String world = resultSet.getString("world");
                    double x = resultSet.getDouble("x");
                    double y = resultSet.getDouble("y");
                    double z = resultSet.getDouble("z");
                    float yaw = resultSet.getFloat("yaw");
                    float pitch = resultSet.getFloat("pitch");

                    File essentialsWarpFile = new File(essentialsWarpFolder, name + ".yml");
                    YamlConfiguration essentialsConfig = new YamlConfiguration();
                    essentialsConfig.set("world", world);
                    essentialsConfig.set("x", x);
                    essentialsConfig.set("y", y);
                    essentialsConfig.set("z", z);
                    essentialsConfig.set("yaw", yaw);
                    essentialsConfig.set("pitch", pitch);
                    essentialsConfig.save(essentialsWarpFile);
                }
            }
            sender.sendMessage("Warp data conversion complete.");

        } catch (SQLException | IOException e) {
            sender.sendMessage("An error occurred during data conversion: " + e.getMessage());
        }
    }
}
