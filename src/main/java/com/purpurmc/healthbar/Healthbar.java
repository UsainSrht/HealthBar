package com.purpurmc.healthbar;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Healthbar extends JavaPlugin {

    public static Healthbar instance;
    public String livingtitle;
    public String deadtitle;
    public BossBar.Overlay baroverlay;
    public BossBar.Color barcolor;
    public Collection<EntityType> blacklist;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new DamageEvent(), this);
        this.getCommand("healthbar").setExecutor(new HealthBarCommand());
        try {
            initializeYAML();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling HealthBar...");
        instance = null;
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
        File config = new File("plugins/HealthBar/config.yml");
        if (config.exists()) {
            getLogger().info("Loading congif.yml");
            YamlConfiguration configyml = YamlConfiguration.loadConfiguration(config);

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
        else {
            getLogger().info("Config.yml couldn't found. Creating one...");
            this.saveDefaultConfig();
            initializeYAML();
        }
    }
}
