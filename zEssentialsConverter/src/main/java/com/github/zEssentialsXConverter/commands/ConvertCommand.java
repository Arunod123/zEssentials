package com.github.zEssentialsXConverter.commands;

import com.github.zEssentialsXConverter.ZEssentialsXConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.github.zEssentialsXConverter.converter.ZEssentialsConverter;

public class ConvertCommand implements CommandExecutor {

    private final ZEssentialsXConverter plugin;

    public ConvertCommand(ZEssentialsXConverter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Starting zEssentials to EssentialsX conversion...");
        ZEssentialsConverter converter = new ZEssentialsConverter(plugin);
        converter.convert(sender);
        return true;
    }
}
