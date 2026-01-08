package me.sirimperivm.commandsAPI.command.exception;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandExceptionHandler {

    void handle(CommandSender sender, CommandException exception);
}
