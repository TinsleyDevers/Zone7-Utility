package me.tinsley.zone7;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FlowerDropListener implements Listener {
    private Random random = new Random();
    private static final Set<Material> FLOWERS;

    static {
        FLOWERS = new HashSet<>();
        FLOWERS.add(Material.DANDELION);
        FLOWERS.add(Material.POPPY);
        FLOWERS.add(Material.BLUE_ORCHID);
        FLOWERS.add(Material.ALLIUM);
        FLOWERS.add(Material.AZURE_BLUET);
        FLOWERS.add(Material.RED_TULIP);
        FLOWERS.add(Material.ORANGE_TULIP);
        FLOWERS.add(Material.WHITE_TULIP);
        FLOWERS.add(Material.PINK_TULIP);
        FLOWERS.add(Material.OXEYE_DAISY);
        FLOWERS.add(Material.CORNFLOWER);
        FLOWERS.add(Material.LILY_OF_THE_VALLEY);
        FLOWERS.add(Material.WITHER_ROSE);
        FLOWERS.add(Material.SUNFLOWER);
        FLOWERS.add(Material.LILAC);
        FLOWERS.add(Material.ROSE_BUSH);
        FLOWERS.add(Material.PEONY);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();

        // Check if the item is a flower and has been renamed
        if (isRenamedFlower(item)) {
            Location loc = event.getLocation();
            // Spawn 1 to 5 heart and spark particles around the dropped item
            for (int i = 0; i < random.nextInt(5) + 1; i++) {
                loc.getWorld().spawnParticle(Particle.HEART, loc.clone().add(randomOffset()), 1);
                loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.clone().add(randomOffset()), 1);
            }
        }
    }

    private boolean isRenamedFlower(ItemStack item) {
        return FLOWERS.contains(item.getType()) && item.hasItemMeta() && item.getItemMeta().hasDisplayName();
    }

    private Vector randomOffset() {
        return new Vector((random.nextDouble() - 0.5) * 2.0, 0.5, (random.nextDouble() - 0.5) * 2.0);
    }
}
