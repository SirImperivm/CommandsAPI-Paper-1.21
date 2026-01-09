package me.sirimperivm.commandsAPI.command;

import lombok.Getter;
import lombok.Setter;
import me.sirimperivm.commandsAPI.command.model.ArgType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents a command-line or data-processing argument with metadata such as type,
 * constraints, and optional/greedy behavior.
 *
 * The {@code Argument} class allows defining input parameters for commands or operations.
 * It supports constraints on value ranges, length, and type enforcement.
 * Instances of this class are immutable, except for the {@code value} property, which can be modified.
 *
 * Key features:
 * - Type enforcement using {@code ArgType}.
 * - Support for minimum and maximum value constraints.
 * - Support for minimum and maximum length constraints.
 * - Flags for optional and greedy behavior.
 * - Ability to retrieve the stored value in different formats (e.g., String, Integer, Long).
 */
@Getter
public class Argument {

    private final String name;
    private final ArgType type;
    private final long min, max;
    private final int minLen, maxLen;
    private final boolean greedy, optional;

    @Setter private Object value;

    /**
     * Constructs a new Argument instance using the provided Builder instance.
     *
     * @param builder the Builder object containing the configuration for the Argument.
     *                It provides values for the following fields:
     *                - name: the name of the argument
     *                - type: the type of the argument (e.g., STRING, INTEGER)
     *                - min: the minimum allowed value for the argument (if applicable)
     *                - max: the maximum allowed value for the argument (if applicable)
     *                - minLen: the minimum allowed length for the argument (if applicable)
     *                - maxLen: the maximum allowed length for the argument (if applicable)
     *                - greedy: whether the argument allows taking multiple values
     *                - optional: whether the argument is optional
     */
    private Argument(Builder builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.min = builder.min;
        this.max = builder.max;
        this.minLen = builder.minLen;
        this.maxLen = builder.maxLen;
        this.greedy = builder.greedy;
        this.optional = builder.optional;
    }

    /**
     * Retrieves the value of the argument with its type determined at runtime.
     *
     * @param <T> the type of the returned argument value
     * @return the value of the argument cast to the expected type
     * @throws ClassCastException if the value cannot be cast to the specified type
     */
    @SuppressWarnings("unchecked")
    public <T> T getArg() {
        return (T) value;
    }

    /**
     * Converts the stored value to its string representation.
     * If the value is null, this method returns null.
     *
     * @return the string representation of the value, or null if the value is not set
     */
    public String getAsString() {
        return value != null ? value.toString() : null;
    }

    /**
     * Converts the stored value to an Integer if it is a Number.
     * If the value is not an instance of Number, this method returns null.
     *
     * @return the integer representation of the value, or null if the value is not a Number
     */
    public Integer getAsInt() {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    /**
     * Converts the stored value to a Long if it is an instance of Number.
     * If the value is not an instance of Number, this method returns null.
     *
     * @return the long representation of the value, or null if the value is not a Number
     */
    public Long getAsLong() {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    /**
     * Converts the stored value to a Double if it is an instance of Number.
     * If the value is not an instance of Number, this method returns null.
     *
     * @return the double representation of the value, or null if the value is not a Number
     */
    public Double getAsDouble() {
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    /**
     * Retrieves the stored value as a Boolean if it is an instance of Boolean.
     * If the value is not an instance of Boolean, this method returns null.
     *
     * @return the Boolean representation of the value, or null if the value is not a Boolean
     */
    public Boolean getAsBoolean() {
        return value instanceof Boolean ? (Boolean) value : null;
    }

    /**
     * Converts the stored value to a Player object using Bukkit's {@code getPlayerExact} method.
     * If the value is null, this method returns null.
     *
     * @return the Player object corresponding to the stored value, or null if the value is null
     */
    public Player getAsPlayer() {
        if (value == null) return null;
        return Bukkit.getPlayerExact(value.toString());
    }

    /**
     * Creates a new Builder instance for constructing an {@code Argument}.
     *
     * @param name the name of the argument to be configured; cannot be null
     * @return a new instance of {@code Builder} initialized with the provided name
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * A builder class for constructing an instance of the {@code Argument} class.
     * This class provides a fluent API to configure various properties of an {@code Argument},
     * such as its name, type, constraints on values or length, and whether it is optional or greedy.
     */
    public static class Builder {
        private final String name;
        private ArgType type = ArgType.STRING;
        private long min = Long.MIN_VALUE;
        private long max = Long.MAX_VALUE;
        private int minLen = 0;
        private int maxLen = Integer.MAX_VALUE;
        private boolean greedy = false;
        private boolean optional = false;

        /**
         * Constructs a new {@code Builder} instance with the specified name.
         *
         * @param name the name of the argument to be constructed
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Sets the type of the argument being constructed.
         *
         * @param type the type of the argument, represented by the {@code ArgType} enum
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder type(ArgType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the minimum allowed value for the argument being constructed.
         *
         * @param min the minimum value constraint to be applied to the argument
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder min(long min) {
            this.min = min;
            return this;
        }

        /**
         * Sets the maximum allowed value for the argument being constructed.
         *
         * @param max the maximum value constraint to be applied to the argument
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder max(long max) {
            this.max = max;
            return this;
        }

        /**
         * Sets the minimum allowed length for the argument being constructed.
         *
         * @param minLen the minimum length constraint to be applied to the argument
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder minLen(int minLen) {
            this.minLen = minLen;
            return this;
        }

        /**
         * Sets the maximum allowed length for the argument being constructed.
         *
         * @param maxLen the maximum length constraint to be applied to the argument
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder maxLen(int maxLen) {
            this.maxLen = maxLen;
            return this;
        }

        /**
         * Sets whether the constructed argument should operate in "greedy" mode.
         *
         * @param greedy a boolean value indicating if the argument should consume as much input as possible
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder greedy(boolean greedy) {
            this.greedy = greedy;
            return this;
        }

        /**
         * Sets whether the constructed argument is optional.
         *
         * @param optional a boolean value indicating if the argument is optional
         * @return the current {@code Builder} instance for method chaining
         */
        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        /**
         * Builds and returns a new {@code Argument} instance based on the parameters provided
         * to this {@code Builder}.
         *
         * @return a new {@code Argument} instance with the configured properties
         */
        public Argument build() {
            return new Argument(this);
        }
    }
}
