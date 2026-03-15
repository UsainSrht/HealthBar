package com.purpurmc.healthbar;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Damageable)) return;
        Entity attacker = event.getDamager();
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                attacker = (Entity) projectile.getShooter();
            }
        }
        if (attacker instanceof Player) {
            if (!Healthbar.instance.getBlacklistEntities().contains(event.getEntityType())) {
                BossBarUtils.giveBossBar((Player) attacker, event.getEntity());
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        double finalDamage = event.getFinalDamage();
        Healthbar.getInstance().getScheduling().entitySpecificScheduler(entity).runDelayed(() -> {
            if (entity.hasMetadata("healthbar")) {
                BossBarUtils.updateHealthBar(entity, finalDamage);
            }
        }, () -> {
            // Entity was removed before the delayed update ran.
        }, 1L);
    }
}
