package com.github.zEssentialsXConverter;

import com.github.zEssentialsXConverter.commands.ConvertCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ZEssentialsXConverter extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("zEssentialsXConverter has been enabled!");
        getCommand("convertztoex").setExecutor(new ConvertCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("zEssentialsXConverter has been disabled!");
    }
}
