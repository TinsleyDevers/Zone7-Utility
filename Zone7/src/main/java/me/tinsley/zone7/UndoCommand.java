package me.tinsley.zone7;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UndoCommand implements CommandExecutor {
    private Set<UUID> undoEnabled = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        if (!player.hasPermission("zone7.undotoggle")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (undoEnabled.contains(playerUUID)) {
            undoEnabled.remove(playerUUID);
            player.sendMessage(ChatColor.RED + "Undo Disabled!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
        } else {
            undoEnabled.add(playerUUID);
            player.sendMessage(ChatColor.GREEN + "Undo Enabled!");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        return true;
    }

    public boolean isUndoEnabled(UUID playerUUID) {
        return undoEnabled.contains(playerUUID);
    }
}
