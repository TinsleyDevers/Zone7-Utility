package me.tinsley.zone7;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Arrays;
import java.util.List;


public class MainPluginClass extends JavaPlugin {
    private TipsManager tipsManager;
    private UndoCommand undoCommand;
    private BlockUndoListener blockUndoListener;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.addDefaultPetMessages();
        this.registerCommands();
        this.registerListeners();

        tipsManager = new TipsManager(this);
        if (getConfig().getBoolean("TipsCommand.Enabled", true)) {
            tipsManager.startBroadcasting();
        }

        undoCommand = new UndoCommand();
        this.getCommand("undotoggle").setExecutor(undoCommand);

        blockUndoListener = new BlockUndoListener(this, undoCommand);
        getServer().getPluginManager().registerEvents(blockUndoListener, this);
    }

    private void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }

    private void registerFeatures() {
        unregisterListeners();
        registerCommands();
        registerListeners();
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
        if (getConfig().getBoolean("DiscordCommand.Enabled", true)) {
            this.getCommand("discord").setExecutor(new DiscordCommand(this));
        }
        if (getConfig().getBoolean("PokeCommand.Enabled", true)) {
            this.getCommand("poke").setExecutor(new PokeCommand());
        }
        this.getCommand("reload").setExecutor(this::reloadCommand);
        if (getConfig().getBoolean("TipsCommand.Enabled", true)) {
            this.getCommand("tips").setExecutor((sender, command, label, args) -> {
                if (sender instanceof Player) {
                    tipsManager.toggleTipsForPlayer((Player) sender);
                }
                return true;
            });
        }
    }

    private void registerListeners() {
        if (getConfig().getBoolean("FlowerDropFeature.Enabled", true)) {
            getServer().getPluginManager().registerEvents(new FlowerDropListener(), this);
        }
        if (getConfig().getBoolean("ArrowTrails.Enabled", true)) {
            getServer().getPluginManager().registerEvents(new ArrowTrails(this), this);
        }
        if (getConfig().getBoolean("PettingFeature.Enabled", true)) {
            getServer().getPluginManager().registerEvents(new EntityInteractListener(this), this);
        }
    }

    private boolean reloadCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("zone7.reload")) {
            if (tipsManager != null) {
                tipsManager.savePlayerPreferences();
            }

            reloadConfig();
            registerFeatures();

            if (tipsManager != null) {
                tipsManager.restorePlayerPreferences();
                tipsManager.restartBroadcasting();
            }

            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
        }
        return true;
    }
}
