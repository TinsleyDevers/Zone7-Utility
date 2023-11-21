package me.tinsley.zone7;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MainPluginClass extends JavaPlugin {
    private TipsManager tipsManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.registerCommands();
        this.registerListeners();

        tipsManager = new TipsManager(this);
        tipsManager.startBroadcasting();
    }

    private void registerCommands() {
        this.getCommand("discord").setExecutor(new DiscordCommand(this));
        this.getCommand("poke").setExecutor(new PokeCommand());
        this.getCommand("reload").setExecutor(this::reloadCommand);
        this.getCommand("tips").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                tipsManager.toggleTipsForPlayer((Player) sender);
            }
            return true;
        });
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EntityInteractListener(), this);
    }

    private boolean reloadCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("zone7.reload")) {
            reloadConfig();
            tipsManager.restartBroadcasting();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
        }
        return true;
    }
}
