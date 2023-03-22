package com.purpurmc.healthbar;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Healthbar extends JavaPlugin {

    public static Healthbar instance;
    public static NamespacedKey visible;
    public static String livingtitle;
    public static String deadtitle;
    public static BossBar.Overlay baroverlay;
    public static BossBar.Color barcolor;
    public static int bartime;
    public static Collection<EntityType> blacklist;
    @Override
    public void onEnable() {
        instance = this;

        visible = new NamespacedKey(instance, "visible");

        getServer().getPluginManager().registerEvents(new DamageEvent(), this);

        CommandHandler.register(new HealthBarCommand("healthbar",
                "command to toggle/reload healthbar",
                "/healthbar (toggle|reload)",
                new ArrayList<>()));
        try {
            initializeYAML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onDisable() {

    }
    public static Healthbar getInstance() {
        return instance;
    }
    public Collection<EntityType> getBlacklistEntities() {
        return blacklist;
    }
    public BossBar.Overlay getBaroverlay() {
        return baroverlay;
    }
    public BossBar.Color getBarcolor() {
        return barcolor;
    }
    public String getLivingtitle() {
        return livingtitle;
    }
    public String getDeadtitle() {
        return deadtitle;
    }
    public void initializeYAML() throws IOException {
        saveDefaultConfig();
        getLogger().info("Loading config.yml");
        FileConfiguration configyml = getConfig();

        livingtitle = configyml.getString("bar.title.living");

        deadtitle = configyml.getString("bar.title.dead");

        String overlay = configyml.getString("bar.overlay");
        baroverlay = BossBar.Overlay.valueOf(overlay);

        String color = configyml.getString("bar.color");
        barcolor = BossBar.Color.valueOf(color);

        List<String> Lblacklist = configyml.getStringList("blacklist");
        Collection<EntityType> blacklistentitytpye = new ArrayList<>();
        Lblacklist.forEach(str -> blacklistentitytpye.add(EntityType.valueOf(str))
        );

        blacklist = blacklistentitytpye;

        bartime = configyml.getInt("bar.time");
    }
}
