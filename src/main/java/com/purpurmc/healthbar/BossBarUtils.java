package com.purpurmc.healthbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import space.arim.morepaperlib.scheduling.ScheduledTask;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarUtils {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final Map<String, ScheduledTask> HIDE_TASKS = new ConcurrentHashMap<>();

    public static void giveBossBar(Player p, Entity entity) {
        if (!(entity instanceof Damageable) || !(entity instanceof Attributable)) {
            return;
        }

        Attributable attributable = (Attributable) entity;
        AttributeInstance maxHealthAttribute = attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute == null) {
            return;
        }

        Damageable damageable = (Damageable) entity;
        float progress = (float) (damageable.getHealth() / maxHealthAttribute.getValue());
        BossBar bar = getorCreateBossBar(entity).progress(progress);
        UUID entityId = entity.getUniqueId();

        Healthbar.getInstance().getScheduling().entitySpecificScheduler(p).run(() -> {
            if (p.getPersistentDataContainer().has(Healthbar.visible, PersistentDataType.LONG)) {
                return;
            }
            showAndScheduleHide(p, bar, entityId);
        }, () -> {
            // Player was removed before boss bar could be shown.
        });
    }

    private static void showAndScheduleHide(Player player, BossBar bar, UUID entityId) {
        player.showBossBar(bar);

        String cooldownKey = "healthbarcooldown" + entityId;
        player.setMetadata(cooldownKey, new FixedMetadataValue(Healthbar.getInstance(), Bukkit.getCurrentTick()));

        String taskKey = getTaskKey(player, entityId);
        ScheduledTask previousTask = HIDE_TASKS.remove(taskKey);
        if (previousTask != null) {
            previousTask.cancel();
        }

        ScheduledTask hideTask = Healthbar.getInstance().getScheduling().entitySpecificScheduler(player).runDelayed(() -> {
            HIDE_TASKS.remove(taskKey);
            if (!player.hasMetadata(cooldownKey)) {
                return;
            }
            int current = Bukkit.getCurrentTick();
            int cd = player.getMetadata(cooldownKey).get(0).asInt();
            if ((current - cd) >= Healthbar.bartime) {
                player.hideBossBar(bar);
            }
        }, () -> HIDE_TASKS.remove(taskKey), Healthbar.bartime);

        if (hideTask != null) {
            HIDE_TASKS.put(taskKey, hideTask);
        }
    }

    public static void cancelAllHideTasks() {
        HIDE_TASKS.values().forEach(ScheduledTask::cancel);
        HIDE_TASKS.clear();
    }

    private static String getTaskKey(Player player, UUID entityId) {
        return player.getUniqueId() + ":" + entityId;
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
        if (bossbar == null) return;
        Component parsed;
        Component translate = Component.translatable(entity.getType().translationKey());
        Damageable damageable = (Damageable) entity;
        Attributable attributable = (Attributable) entity;
        AttributeInstance maxHealthAttribute = attributable.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute == null) {
            return;
        }
        double max = maxHealthAttribute.getValue();
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
