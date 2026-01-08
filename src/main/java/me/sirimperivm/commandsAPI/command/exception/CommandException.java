package me.sirimperivm.commandsAPI.command.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandException extends RuntimeException {

    private final String errorId;
    private final Map<String, Object> context;

    public CommandException(String errorId) {
        super(errorId);
        this.errorId = errorId;
        this.context = new HashMap<>();
    }

    public CommandException(String errorId, Map<String, Object> context) {
        super(errorId);
        this.errorId = errorId;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    public CommandException with(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }
}
