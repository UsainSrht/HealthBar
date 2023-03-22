package com.purpurmc.healthbar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.purpurmc.healthbar.Healthbar.visible;

public class HealthBarCommand extends Command {

    protected HealthBarCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String command, String[] args) {
        if (args.length == 0) return true;
        if (args[0].equals("toggle")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.getPersistentDataContainer().has(visible, PersistentDataType.LONG)) {
                    p.getPersistentDataContainer().remove(visible);
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(Healthbar.getInstance().getConfig().getString("messages.toggle.true")));
                } else {
                    p.getPersistentDataContainer().set(visible, PersistentDataType.LONG, 1L);
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(Healthbar.getInstance().getConfig().getString("messages.toggle.false")));
                }
            }
            else {
                sender.sendMessage(Component.text("/healthbar toggle command only executable as player!", NamedTextColor.RED));
            }
        } else if (args[0].equals("reload")) {
            if (sender.hasPermission("healthbar.reload")) {
                try {
                    Healthbar.getInstance().initializeYAML();
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(Healthbar.getInstance().getConfig().getString("messages.reload")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return true;
    }
}
