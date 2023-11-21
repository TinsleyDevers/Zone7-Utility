package me.tinsley.zone7;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

public class PokeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        if (args.length > 0) {
            String targetName = args[0];
            Player target = Bukkit.getServer().getPlayer(targetName);

            if (target != null && target.isOnline()) {
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                target.sendMessage(player.getName() + " has poked you!");
                player.sendMessage("You poked " + target.getName() + ".");
            } else {
                player.sendMessage("Player not found or not online.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /poke <player>");
        }
        return true;
    }
}
