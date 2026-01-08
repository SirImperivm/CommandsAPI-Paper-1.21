package me.sirimperivm.commandsAPI.command.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an exception that occurs during command execution. This exception is used
 * to signal command-related errors and provides additional context about the error, such
 * as a unique error identifier and a map for storing detailed contextual information.
 */
@Getter
public class CommandException extends RuntimeException {

    private final String errorId;
    private final Map<String, Object> context;

    /**
     * Constructs a new {@code CommandException} with the specified error identifier.
     * This constructor initializes the {@code errorId} of the exception and prepares
     * an empty context for storing additional information related to the error.
     *
     * @param errorId a string representing the unique identifier of the error
     */
    public CommandException(String errorId) {
        super(errorId);
        this.errorId = errorId;
        this.context = new HashMap<>();
    }

    /**
     * Constructs a {@code CommandException} with the specified error identifier and an optional context.
     * This constructor initializes the {@code errorId} of the exception and assigns a copy of the provided
     * context map. If the context is {@code null}, an empty context is initialized.
     *
     * @param errorId a string representing the unique identifier of the error
     * @param context a map containing additional context data associated with the error; can be {@code null}
     */
    public CommandException(String errorId, Map<String, Object> context) {
        super(errorId);
        this.errorId = errorId;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    /**
     * Adds a key-value pair to the context map of the exception.
     * This method is used to associate additional information with
     * the exception, providing more details about the error that occurred.
     *
     * @param key the key representing the context information to add
     * @param value the value associated with the provided key
     * @return the same instance of {@code CommandException} for method chaining
     */
    public CommandException with(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    /**
     * Retrieves a value from the context map associated with the given key.
     * This method allows generic type casting for the returned value.
     *
     * @param <T> the type of the value to be retrieved
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or {@code null} if no mapping is found
     * @throws ClassCastException if the value associated with the key cannot be cast to the expected type
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }
}
