package com.purpurmc.healthbar;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {

    private final BossBar bossbar = new BossBar();
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            NamespacedKey visible = new NamespacedKey(Healthbar.instance, "visible");
            if (!event.getDamager().getPersistentDataContainer().has(visible)) {
                if (!Healthbar.instance.getBlacklistEntities().contains(event.getEntityType())) {
                    bossbar.giveBossBar((Player) event.getDamager(), event.getEntity());
                }
            }

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        Bukkit.getScheduler().runTaskLater(Healthbar.instance, () -> {
            if (entity.hasMetadata("healthbar")) {
                bossbar.updateHealthBar(entity, event.getFinalDamage());
            }
        }, 1L);
    }
}
