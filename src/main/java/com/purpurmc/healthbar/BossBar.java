package com.purpurmc.healthbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
    public void giveBossBar(Player p, Entity entity) {
        Float progress = (float)(((Damageable) entity).getHealth() / ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        net.kyori.adventure.bossbar.BossBar bar = getorCreateBossBar(entity).progress(progress);
        p.showBossBar(bar);
        p.setMetadata("healthbarcooldown" + entity.getUniqueId(), new FixedMetadataValue(Healthbar.instance, System.currentTimeMillis()));
        Bukkit.getScheduler().runTaskLater(Healthbar.instance, () -> {
            Long current = System.currentTimeMillis();
            Long cd = p.getMetadata("healthbarcooldown" + entity.getUniqueId()).get(0).asLong();
            if ((current - cd) > 4900) {
                p.hideBossBar(bar);
            }
        }, 100L);

    }

    public net.kyori.adventure.bossbar.BossBar getorCreateBossBar(Entity entity) {

        if (!entity.hasMetadata("healthbar")) {
            //ComponentLike componentLike = (ComponentLike) Component.translatable(entity.getType().translationKey());
            ComponentLike componentLike = (ComponentLike) Component.empty();
            Float progress = 1f;
            net.kyori.adventure.bossbar.BossBar.Color color = Healthbar.instance.getBarcolor();
            net.kyori.adventure.bossbar.BossBar.Overlay overlay = Healthbar.instance.getBaroverlay();

            net.kyori.adventure.bossbar.BossBar bossbar = net.kyori.adventure.bossbar.BossBar.bossBar(
                    componentLike,
                    progress,
                    color,
                    overlay);
            entity.setMetadata("healthbar", new FixedMetadataValue(Healthbar.instance, bossbar));
            return bossbar;
        }
        else {
            return (net.kyori.adventure.bossbar.BossBar) entity.getMetadata("healthbar").get(0).value();
        }
    }

    public void updateHealthBar(Entity entity, double damage) {
        net.kyori.adventure.bossbar.BossBar bossbar = (net.kyori.adventure.bossbar.BossBar) entity.getMetadata("healthbar").get(0).value();
        Component entityname;
        Double health = ((Damageable) entity).getHealth() - damage;
        if (health < 0) {
            entityname = (Component.text("☠").append(Component.translatable(entity.getType().translationKey())).append(Component.text("☠"))).color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC);
            health = 0d;
        }
        else {
            entityname = Component.translatable(entity.getType().translationKey());
        }
        Component space = Component.space();
        Component damagecomponent = Component.text("-" + df.format(damage) + "♥", NamedTextColor.RED);
        Component allofem = entityname.append(space.append(damagecomponent));
        bossbar = bossbar.name(allofem);
        Float progress = (float)(health / ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        bossbar = bossbar.progress(progress);
    }
}
