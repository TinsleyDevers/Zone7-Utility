package me.tinsley.zone7;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class TipsManager {
    private final JavaPlugin plugin;
    private Set<UUID> tipsDisabled = new HashSet<>();
    private BukkitTask broadcastTask;

    public TipsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        restorePlayerPreferences();
    }

    public void startBroadcasting() {
        broadcastTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("TipsCommand.FeatureEnabled", true)) return;
                broadcastRandomTip();
            }
        }.runTaskTimer(plugin, 0, 20L * plugin.getConfig().getInt("TipsCommand.Interval", 3600));
    }

    public void restartBroadcasting() {
        if (broadcastTask != null) {
            broadcastTask.cancel();
        }
        startBroadcasting();
    }

    private void broadcastRandomTip() {
        List<String> messages = plugin.getConfig().getStringList("TipsCommand.Messages");
        String message = messages.get(new Random().nextInt(messages.size()));

        Bukkit.getServer().getOnlinePlayers().stream()
                .filter(player -> !tipsDisabled.contains(player.getUniqueId()))
                .forEach(player -> {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    player.sendMessage(ChatColor.GRAY + "Want to disable these? Do /tips to toggle on and off!");
                });
    }

    public void toggleTipsForPlayer(Player player) {
        if (tipsDisabled.contains(player.getUniqueId())) {
            tipsDisabled.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Tips Enabled!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        } else {
            tipsDisabled.add(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Tips Disabled!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        }
        savePlayerPreferences();
    }

    public void savePlayerPreferences() {
        List<String> disabledPlayers = tipsDisabled.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        plugin.getConfig().set("TipsCommand.DisabledPlayers", disabledPlayers);
        plugin.saveConfig();
    }

    public void restorePlayerPreferences() {
        tipsDisabled.clear();
        if (plugin.getConfig().isSet("TipsCommand.DisabledPlayers")) {
            List<String> disabledPlayers = plugin.getConfig().getStringList("TipsCommand.DisabledPlayers");
            for (String playerUUID : disabledPlayers) {
                tipsDisabled.add(UUID.fromString(playerUUID));
            }
        }
    }
}
