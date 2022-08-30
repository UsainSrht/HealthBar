package com.purpurmc.healthbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;

class HealthBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args[0] == "toggle") {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                NamespacedKey visible = new NamespacedKey(Healthbar.instance, "visible");
                if (p.getPersistentDataContainer().has(visible, PersistentDataType.LONG)) {
                    p.getPersistentDataContainer().remove(visible);
                }
                else {
                    p.getPersistentDataContainer().set(visible, PersistentDataType.LONG, 1L);
                }
            }
        }
        else if (args[0] == "reload") {
            if (sender.hasPermission("healthbar.reload")) {
                try {
                    Healthbar.instance.initializeYAML();
                    sender.sendMessage(Component.text("plugin reloaded successfully", NamedTextColor.GREEN));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
