package com.purpurmc.healthbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Damageable;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.DecimalFormat;

public class BossBarUtils {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    public static void giveBossBar(Player p, Entity entity) {
        Damageable damageable = (Damageable) entity;
        Attributable attributable = (Attributable) entity;
        float progress = (float) (damageable.getHealth() / attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        BossBar bar = getorCreateBossBar(entity).progress(progress);
        p.showBossBar(bar);
        p.setMetadata("healthbarcooldown" + entity.getUniqueId()
                , new FixedMetadataValue(Healthbar.getInstance(), Bukkit.getCurrentTick()));
        Bukkit.getScheduler().runTaskLater(Healthbar.getInstance(), () -> {
            int current = Bukkit.getCurrentTick();
            int cd = p.getMetadata("healthbarcooldown" + entity.getUniqueId()).get(0).asInt();
            if ((current - cd) >= Healthbar.bartime) {
                p.hideBossBar(bar);
            }
        }, Healthbar.bartime);

    }

    public static BossBar getorCreateBossBar(Entity entity) {

        if (!entity.hasMetadata("healthbar")) {
            ComponentLike componentLike = Component.empty();
            float progress = 1f;
            BossBar.Color color = Healthbar.getInstance().getBarcolor();
            BossBar.Overlay overlay = Healthbar.getInstance().getBaroverlay();

            BossBar bossbar = BossBar.bossBar(
                    componentLike,
                    progress,
                    color,
                    overlay);
            entity.setMetadata("healthbar", new FixedMetadataValue(Healthbar.getInstance(), bossbar));
            return bossbar;
        }
        else {
            return (BossBar) entity.getMetadata("healthbar").get(0).value();
        }
    }

    public static void updateHealthBar(Entity entity, double damage) {
        BossBar bossbar = (BossBar) entity.getMetadata("healthbar").get(0).value();
        Component parsed;
        Component translate = Component.translatable(entity.getType().translationKey());
        Damageable damageable = (Damageable) entity;
        Attributable attributable = (Attributable) entity;
        double max = attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double health = damageable.getHealth();
        String title;
        if (health <= 0) {
            health = 0d;
            title = Healthbar.getInstance().getDeadtitle();
        }
        else {
            title = Healthbar.getInstance().getLivingtitle();
        }
        Component customName = entity.customName();
        parsed = MiniMessage.miniMessage().deserialize(title,
                Placeholder.component("entity", translate),
                Placeholder.component("name", customName == null ? translate : customName),
                Placeholder.component("max", Component.text(df.format(max))),
                Placeholder.component("health", Component.text(df.format(health))),
                Placeholder.component("damage", Component.text(df.format(damage)))
        );
        bossbar.name(parsed);
        float progress = (float) (health / max);
        bossbar.progress(progress);
    }
}
