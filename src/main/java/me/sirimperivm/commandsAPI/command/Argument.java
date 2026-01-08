package me.sirimperivm.commandsAPI.command;

import lombok.Getter;
import lombok.Setter;
import me.sirimperivm.commandsAPI.command.model.ArgType;

@Getter
public class Argument {

    private final String name;
    private final ArgType type;
    private final long min, max;
    private final int minLen, maxLen;
    private final boolean greedy, optional;

    @Setter private Object value;

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

    @SuppressWarnings("unchecked")
    public <T> T getArg() {
        return (T) value;
    }

    public String getAsString() {
        return value != null ? value.toString() : null;
    }

    public Integer getAsInt() {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }

    public Long getAsLong() {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    public Double getAsDouble() {
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    public Boolean getAsBoolean() {
        return value instanceof Boolean ? (Boolean) value : null;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private final String name;
        private ArgType type = ArgType.STRING;
        private long min = Long.MIN_VALUE;
        private long max = Long.MAX_VALUE;
        private int minLen = 0;
        private int maxLen = Integer.MAX_VALUE;
        private boolean greedy = false;
        private boolean optional = false;

        public Builder(String name) {
            this.name = name;
        }

        public Builder type(ArgType type) {
            this.type = type;
            return this;
        }

        public Builder min(long min) {
            this.min = min;
            return this;
        }

        public Builder max(long max) {
            this.max = max;
            return this;
        }

        public Builder minLen(int minLen) {
            this.minLen = minLen;
            return this;
        }

        public Builder maxLen(int maxLen) {
            this.maxLen = maxLen;
            return this;
        }

        public Builder greedy(boolean greedy) {
            this.greedy = greedy;
            return this;
        }

        public Builder optional(boolean optional) {
            this.optional = optional;
            return this;
        }

        public Argument build() {
            return new Argument(this);
        }
    }
}
