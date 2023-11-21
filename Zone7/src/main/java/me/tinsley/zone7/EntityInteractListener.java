package me.tinsley.zone7;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityInteractListener implements Listener {
    private Map<UUID, Long> lastPetTimes = new HashMap<>();
    private Map<UUID, UUID> lastPetEntity = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // player is sneaking and has an empty hand
        if (player.isSneaking() && event.getHand() == EquipmentSlot.HAND) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType().isAir()) {
                // heart particles
                entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().add(0, 1, 0), 5);

                // Cooldown
                long lastPetTime = lastPetTimes.getOrDefault(player.getUniqueId(), 0L);
                if (System.currentTimeMillis() - lastPetTime >= 7000) {
                    lastPetTimes.put(player.getUniqueId(), System.currentTimeMillis());


                    // name to display in message
                    String entityName = entity.getCustomName();
                    if (entityName == null || entityName.isEmpty()) {
                        entityName = entity.getType().name();
                    }

                    if (System.currentTimeMillis() - lastPetTime >= 7000 || !lastPetEntity.getOrDefault(player.getUniqueId(), UUID.randomUUID()).equals(entity.getUniqueId())) {
                        lastPetTimes.put(player.getUniqueId(), System.currentTimeMillis());
                        lastPetEntity.put(player.getUniqueId(), entity.getUniqueId());

                        String message = ChatColor.GRAY + player.getName() + " has pet " + entityName + ".";
                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> p.getLocation().distance(entity.getLocation()) <= 20)
                                .forEach(p -> p.sendMessage(message));
                    }
                }
            }
        }
    }
}
