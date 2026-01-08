package me.sirimperivm.commandsAPI.command.model;

/**
 * Represents the type of argument. This enum defines the possible data types
 * that an argument can be, helping to define and enforce type restrictions in
 * commands or operations that require specific input formats.
 *
 * The types included are:
 * - STRING: Represents a textual value.
 * - INTEGER: Represents a 32-bit signed integer.
 * - LONG: Represents a 64-bit signed integer.
 * - DOUBLE: Represents a floating-point value.
 * - BOOLEAN: Represents a true or false value.
 */
public enum ArgType {

    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    BOOLEAN,
}
