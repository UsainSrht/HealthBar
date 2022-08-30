package com.purpurmc.healthbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Damageable;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;

public class BossBar {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final Healthbar healthbar = Healthbar.instance;
    public void giveBossBar(Player p, Entity entity) {
        float progress = (float)(((Damageable) entity).getHealth() / ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        net.kyori.adventure.bossbar.BossBar bar = getorCreateBossBar(entity).progress(progress);
        p.showBossBar(bar);
        p.setMetadata("healthbarcooldown" + entity.getUniqueId(), new FixedMetadataValue(healthbar, System.currentTimeMillis()));
        Bukkit.getScheduler().runTaskLater(healthbar, () -> {
            Long current = System.currentTimeMillis();
            Long cd = p.getMetadata("healthbarcooldown" + entity.getUniqueId()).get(0).asLong();
            if ((current - cd) > 4900) {
                p.hideBossBar(bar);
            }
        }, 100L);

    }

    public net.kyori.adventure.bossbar.BossBar getorCreateBossBar(Entity entity) {

        if (!entity.hasMetadata("healthbar")) {
            ComponentLike componentLike = (ComponentLike) Component.empty();
            float progress = 1f;
            net.kyori.adventure.bossbar.BossBar.Color color = healthbar.getBarcolor();
            net.kyori.adventure.bossbar.BossBar.Overlay overlay = healthbar.getBaroverlay();

            net.kyori.adventure.bossbar.BossBar bossbar = net.kyori.adventure.bossbar.BossBar.bossBar(
                    componentLike,
                    progress,
                    color,
                    overlay);
            entity.setMetadata("healthbar", new FixedMetadataValue(healthbar, bossbar));
            return bossbar;
        }
        else {
            return (net.kyori.adventure.bossbar.BossBar) entity.getMetadata("healthbar").get(0).value();
        }
    }

    public void updateHealthBar(Entity entity, double damage) {
        net.kyori.adventure.bossbar.BossBar bossbar = (net.kyori.adventure.bossbar.BossBar) entity.getMetadata("healthbar").get(0).value();
        Component parsed;
        Component translate = Component.translatable(entity.getType().translationKey());
        double max = ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = ((Damageable) entity).getHealth();
        String title;
        if (health <= 0) {
            health = 0d;
            title = healthbar.getDeadtitle();
            parsed = MiniMessage.miniMessage().deserialize(title,
                    Placeholder.component("entity", translate),
                    Placeholder.component("name", entity.customName() == null ? translate : entity.customName()),
                    Placeholder.component("max", Component.text(df.format(max))),
                    Placeholder.component("health", Component.text(df.format(health))),
                    Placeholder.component("damage", Component.text(df.format(damage)))
            );
        }
        else {
            title = healthbar.getLivingtitle();
            parsed = MiniMessage.miniMessage().deserialize(title,
                    Placeholder.component("entity", translate),
                    Placeholder.component("name", entity.customName() == null ? translate : entity.customName()),
                    Placeholder.component("max", Component.text(df.format(max))),
                    Placeholder.component("health", Component.text(df.format(health))),
                    Placeholder.component("damage", Component.text(df.format(damage)))
            );
        }
        bossbar = bossbar.name(parsed);
        Float progress = (float)(health / max);
        bossbar = bossbar.progress(progress);
    }
}
