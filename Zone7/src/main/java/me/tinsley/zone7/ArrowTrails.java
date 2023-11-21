package me.tinsley.zone7;

import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowTrails implements Listener {
    private JavaPlugin plugin;
    private Map<UUID, BukkitRunnable> arrowTasks = new HashMap<>();

    public ArrowTrails(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) event.getEntity();
            Player player = (Player) arrow.getShooter();
            ItemStack bow = player.getInventory().getItemInMainHand();

            if (isNamedBow(bow, "Cupid")) {
                createArrowTrail(arrow, Particle.HEART);
            } else if (isNamedBow(bow, "Bubbles")) {
                createArrowTrail(arrow, Particle.BUBBLE_POP, Particle.BUBBLE_COLUMN_UP);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            BukkitRunnable task = arrowTasks.get(arrow.getUniqueId());
            if (task != null) {
                task.cancel();
                arrowTasks.remove(arrow.getUniqueId());
            }
        }
    }

    private void createArrowTrail(Arrow arrow, Particle... particles) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isDead() && !arrow.isOnGround()) {
                    for (Particle particle : particles) {
                        arrow.getWorld().spawnParticle(particle, arrow.getLocation(), 1);
                    }
                } else {
                    this.cancel();
                    arrowTasks.remove(arrow.getUniqueId());
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
        arrowTasks.put(arrow.getUniqueId(), task);
    }

    private boolean isNamedBow(ItemStack item, String name) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase(name);
        }
        return false;
    }
}
