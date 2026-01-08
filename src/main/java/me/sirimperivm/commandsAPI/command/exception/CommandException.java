package me.sirimperivm.commandsAPI.command.exception;

import lombok.Getter;

@Getter
public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }
}
