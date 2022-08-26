package com.purpurmc.healthbar;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {

    private BossBar bossbar = new BossBar();
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (!Healthbar.instance.getBlacklistEntities().contains(event.getEntityType())) {
                bossbar.giveBossBar((Player) event.getDamager(), event.getEntity());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.hasMetadata("healthbar")) {
            bossbar.updateHealthBar(entity, event.getFinalDamage());
        }
    }
}
