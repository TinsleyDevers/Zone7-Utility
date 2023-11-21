package me.tinsley.zone7;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.List;

public class MainPluginClass extends JavaPlugin {
    private TipsManager tipsManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.addDefaultPetMessages();
        this.registerCommands();
        this.registerListeners();


        tipsManager = new TipsManager(this);
        tipsManager.startBroadcasting();
        getServer().getPluginManager().registerEvents(new FlowerDropListener(), this);
        getServer().getPluginManager().registerEvents(new ArrowTrails(this), this);
    }

    private void addDefaultPetMessages() {
        List<String> defaultPetMessages = Arrays.asList(
                "gently pets", "lovingly strokes", "happily pats",
                "kindly rubs", "carefully caresses", "playfully tickles",
                "affectionately scratches", "softly touches", "fondly nudges",
                "tenderly pokes"
        );
        if (!this.getConfig().isSet("PetMessages")) {
            this.getConfig().set("PetMessages", defaultPetMessages);
            this.saveConfig();
        }
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
        getServer().getPluginManager().registerEvents(new EntityInteractListener(this), this);
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
