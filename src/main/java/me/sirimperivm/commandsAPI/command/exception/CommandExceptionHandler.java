package me.sirimperivm.commandsAPI.command.exception;

import org.bukkit.command.CommandSender;

/**
 * Represents a handler for managing exceptions that occur during the execution of commands.
 * This functional interface allows custom handling of {@link CommandException} instances,
 * enabling actions such as error logging, user feedback, or recovery mechanisms.
 */
@FunctionalInterface
public interface CommandExceptionHandler {

    /**
     * Handles exceptions that occur during the execution of a command. This method
     * is typically used to manage errors, log details, or provide user feedback when
     * a {@link CommandException} is thrown.
     *
     * @param sender the entity that sent the command, typically representing the
     *               player or console initiating the command
     * @param exception the exception that was thrown during command execution,
     *                  containing information about the error and optional context
     */
    void handle(CommandSender sender, CommandException exception);
}
