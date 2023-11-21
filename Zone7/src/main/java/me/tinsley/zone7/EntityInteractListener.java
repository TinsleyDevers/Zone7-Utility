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
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class EntityInteractListener implements Listener {
    private Map<UUID, Long> lastPetTimes = new HashMap<>();
    private Map<UUID, UUID> lastPetEntity = new HashMap<>();
    private Random random = new Random();
    private JavaPlugin plugin;

    public EntityInteractListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // player is sneaking and has an empty hand
        if (player.isSneaking() && event.getHand() == EquipmentSlot.HAND) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType().isAir()) {
                int heartsToSpawn = random.nextInt(3) + 1;
                for (int i = 0; i < heartsToSpawn; i++) {
                    Vector offset = new Vector((random.nextDouble() - 0.5) * 2.0, 1.0, (random.nextDouble() - 0.5) * 2.0);
                    entity.getWorld().spawnParticle(Particle.HEART, entity.getLocation().add(offset), 1);
                }

                long lastPetTime = lastPetTimes.getOrDefault(player.getUniqueId(), 0L);
                UUID lastEntityId = lastPetEntity.getOrDefault(player.getUniqueId(), new UUID(0, 0));
                if (System.currentTimeMillis() - lastPetTime >= 7000 || !lastEntityId.equals(entity.getUniqueId())) {
                    lastPetTimes.put(player.getUniqueId(), System.currentTimeMillis());
                    lastPetEntity.put(player.getUniqueId(), entity.getUniqueId());

                    List<String> pettingMessages = plugin.getConfig().getStringList("PetMessages");
                    String randomMessage = pettingMessages.get(random.nextInt(pettingMessages.size()));

                    String entityName = entity.getCustomName();
                    if (entityName == null || entityName.isEmpty()) {
                        entityName = entity.getType().name();
                    }

                    String message = ChatColor.GRAY + player.getName() + " " + randomMessage + " " + entityName + ".";
                    Bukkit.getOnlinePlayers().stream()
                            .filter(p -> p.getLocation().distance(entity.getLocation()) <= 20)
                            .forEach(p -> p.sendMessage(message));
                }
            }
        }
    }
}
