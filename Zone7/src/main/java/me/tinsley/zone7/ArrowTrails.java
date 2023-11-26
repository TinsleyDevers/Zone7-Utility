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

import java.util.*;

public class ArrowTrails implements Listener {
    private final JavaPlugin plugin;
    private final Map<UUID, BukkitRunnable> arrowTasks = new HashMap<>();
    private final Map<String, List<Particle>> customTrails = new HashMap<>();

    public ArrowTrails(JavaPlugin plugin) {
        this.plugin = plugin;
        loadCustomTrails();
    }

    private void loadCustomTrails() {
        customTrails.clear();
        if (plugin.getConfig().isConfigurationSection("ArrowTrails.Trails")) {
            for (String key : plugin.getConfig().getConfigurationSection("ArrowTrails.Trails").getKeys(false)) {
                List<String> particleNames = plugin.getConfig().getStringList("ArrowTrails.Trails." + key + ".Particles");
                List<Particle> particles = new ArrayList<>();
                for (String name : particleNames) {
                    try {
                        Particle particle = Particle.valueOf(name.toUpperCase());
                        particles.add(particle);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid particle type: " + name + " in trail " + key);
                    }
                }
                customTrails.put(key, particles);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
            Arrow arrow = (Arrow) event.getEntity();
            Player player = (Player) arrow.getShooter();
            ItemStack bow = player.getInventory().getItemInMainHand();

            for (Map.Entry<String, List<Particle>> entry : customTrails.entrySet()) {
                if (isNamedBow(bow, entry.getKey())) {
                    createArrowTrail(arrow, entry.getValue());
                    break;
                }
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

    private void createArrowTrail(Arrow arrow, List<Particle> particles) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isDead() && !arrow.isOnGround()) {
                    particles.forEach(particle -> arrow.getWorld().spawnParticle(particle, arrow.getLocation(), 1));
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