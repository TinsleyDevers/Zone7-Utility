package me.tinsley.zone7;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PokeCommand implements CommandExecutor {

    private Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 10 * 1000; // 10 seconds in milliseconds

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Check cooldown
        long timeLeft = System.currentTimeMillis() - cooldowns.getOrDefault(playerUUID, 0L);
        if (timeLeft < COOLDOWN_TIME) {
            // Cooldown active, send remaining time message
            long timeRemaining = (COOLDOWN_TIME - timeLeft) / 1000;
            player.sendMessage(ChatColor.RED + "You can poke again in " + timeRemaining + " seconds.");
            return true;
        }

        if (args.length > 0) {
            String targetName = args[0];
            Player target = Bukkit.getServer().getPlayer(targetName);

            if (target != null && target.isOnline()) {
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                target.sendMessage(player.getName() + " has poked you!");
                player.sendMessage("You poked " + target.getName() + ".");

                // Update last used time
                cooldowns.put(playerUUID, System.currentTimeMillis());
            } else {
                player.sendMessage(ChatColor.RED + "Player not found or not online.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /poke <player>");
        }
        return true;
    }
}
