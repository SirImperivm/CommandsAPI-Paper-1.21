package me.sirimperivm.commandsAPI;

import me.sirimperivm.commandsAPI.registry.CommandRegistry;
import org.bukkit.plugin.Plugin;

public final class CommandsAPI {

    public static CommandRegistry createRegistry(Plugin plugin) {
        return new CommandRegistry(plugin);
    }
}
