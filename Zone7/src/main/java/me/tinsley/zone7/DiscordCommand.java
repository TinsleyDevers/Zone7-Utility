package me.tinsley.zone7;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DiscordCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public DiscordCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("zone7.discord")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use the Discord command.");
            return true;
        }

        sendGradientMessage(player, "DiscordCommand.TextMessage", "DiscordCommand.TextColor");
        sendClickableLink(player, "DiscordCommand.Link", "DiscordCommand.LinkColor");

        return true;
    }

    private void sendGradientMessage(Player player, String messagePath, String gradientPath) {
        String message = plugin.getConfig().getString(messagePath, "Message not set");
        List<String> gradientColors = plugin.getConfig().getStringList(gradientPath);
        player.sendMessage(applyGradient(message, gradientColors));
    }

    private void sendClickableLink(Player player, String linkPath, String linkColorPath) {
        String link = plugin.getConfig().getString(linkPath, "Link not set");
        ChatColor linkColor = ChatColor.of(plugin.getConfig().getString(linkColorPath, "#FFFF55")); // Default light yellow
        TextComponent linkComponent = new TextComponent(link);
        linkComponent.setColor(linkColor);
        linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://" + link));
        player.spigot().sendMessage(linkComponent);
    }

    private String applyGradient(String message, List<String> gradientColors) {
        ChatColor[] gradient = new ChatColor[gradientColors.size()];
        for (int i = 0; i < gradientColors.size(); i++) {
            gradient[i] = ChatColor.of(gradientColors.get(i));
        }

        StringBuilder coloredMessage = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            coloredMessage.append(gradient[i % gradient.length]).append(message.charAt(i));
        }
        return coloredMessage.toString();
    }
}
