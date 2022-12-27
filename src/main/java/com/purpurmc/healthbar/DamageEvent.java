package com.purpurmc.healthbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.purpurmc.healthbar.Healthbar.visible;

public class DamageEvent implements Listener {

    private final BossBar bossbar = new BossBar();
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            attacker = (Entity) projectile.getShooter();
        }
        if (attacker instanceof Player) {
            if (!attacker.getPersistentDataContainer().has(visible)) {
                if (!Healthbar.instance.getBlacklistEntities().contains(event.getEntityType())) {
                    bossbar.giveBossBar((Player) attacker, event.getEntity());
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
