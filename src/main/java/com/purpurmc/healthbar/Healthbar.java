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
    public String livingtitle;
    public String deadtitle;
    public BossBar.Overlay baroverlay;
    public BossBar.Color barcolor;
    public Collection<EntityType> blacklist;

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
        return this.blacklist;
    }

    public BossBar.Overlay getBaroverlay() {
        return this.baroverlay;
    }

    public BossBar.Color getBarcolor() {
        return this.barcolor;
    }

    public String getLivingtitle() {return this.livingtitle;}
    public String getDeadtitle() {return this.deadtitle;}

    public void initializeYAML() throws IOException {
        saveDefaultConfig();
        getLogger().info("Loading config.yml");
        FileConfiguration configyml = getConfig();

        this.livingtitle = configyml.getString("bar.title.living");

        this.deadtitle = configyml.getString("bar.title.dead");

        String overlay = configyml.getString("bar.overlay");
        this.baroverlay = BossBar.Overlay.valueOf(overlay);

        String color = configyml.getString("bar.color");
        this.barcolor = BossBar.Color.valueOf(color);

        List<String> blacklist = configyml.getStringList("blacklist");
        Collection<EntityType> blacklistentitytpye = new ArrayList<>();
        blacklist.forEach(str -> blacklistentitytpye.add(EntityType.valueOf(str))
        );

        this.blacklist = blacklistentitytpye;
    }
}
