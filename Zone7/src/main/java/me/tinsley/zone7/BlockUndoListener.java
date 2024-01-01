package me.tinsley.zone7;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.block.Action;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockUndoListener implements Listener {
    private final JavaPlugin plugin;
    private final UndoCommand undoCommand;
    private final Map<UUID, Long> lastPlacedTimes = new HashMap<>();
    private final Map<Location, ItemStack> lastPlacedBlocks = new HashMap<>();
    private final Map<UUID, Long> lastMessageTimes = new HashMap<>();
    private static final long MESSAGE_COOLDOWN = 45000; // 45 seconds

    public BlockUndoListener(JavaPlugin plugin, UndoCommand undoCommand) {
        this.plugin = plugin;
        this.undoCommand = undoCommand;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (undoCommand.isUndoEnabled(playerId)) {
            ItemStack itemInHand = event.getItemInHand().clone();
            itemInHand.setAmount(1);
            lastPlacedTimes.put(playerId, System.currentTimeMillis());
            lastPlacedBlocks.put(event.getBlockPlaced().getLocation(), itemInHand);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && undoCommand.isUndoEnabled(playerId)) {
            Block block = event.getClickedBlock();
            Location blockLocation = block.getLocation();

            if (lastPlacedBlocks.containsKey(blockLocation)) {
                long timeSincePlaced = System.currentTimeMillis() - lastPlacedTimes.getOrDefault(playerId, 0L);
                long undoWindow = plugin.getConfig().getLong("UndoCommand.UndoTimer", 1500); // 1.5 seconds (default time)
                long minPlacementTime = plugin.getConfig().getLong("UndoCommand.MinPlacementTime", 500); // 500ms (default time)

                if (timeSincePlaced > minPlacementTime && timeSincePlaced <= minPlacementTime + undoWindow) {
                    simulateInstantBreak(player, block, lastPlacedBlocks.get(blockLocation));
                    lastPlacedBlocks.remove(blockLocation);
                    updateMessageCooldown(playerId);
                }
            }
        }
        cleanUpOldData();
    }

    private void simulateInstantBreak(Player player, Block block, ItemStack originalBlockItem) {
        block.setType(Material.AIR);
        originalBlockItem.setAmount(1);
        block.getWorld().dropItemNaturally(block.getLocation(), originalBlockItem);
        player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 1, 1);
        if (shouldSendMessage(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "Block undo successful.");
            lastMessageTimes.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    private boolean shouldSendMessage(UUID playerId) {
        long lastMessageTime = lastMessageTimes.getOrDefault(playerId, 0L);
        return System.currentTimeMillis() - lastMessageTime >= MESSAGE_COOLDOWN;
    }

    private void updateMessageCooldown(UUID playerId) {
        if (!lastMessageTimes.containsKey(playerId)) {
            lastMessageTimes.put(playerId, System.currentTimeMillis() - MESSAGE_COOLDOWN);
        }
    }

    private void cleanUpOldData() {
        long currentTime = System.currentTimeMillis();
        lastPlacedTimes.entrySet().removeIf(entry -> currentTime - entry.getValue() > 60000); // 60 seconds
    }
}
