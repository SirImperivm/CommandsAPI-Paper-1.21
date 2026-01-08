package me.sirimperivm.commandsAPI.command;

import lombok.Getter;
import me.sirimperivm.commandsAPI.command.model.ExecutorType;
import org.bukkit.command.CommandSender;

import java.util.*;

@Getter
public abstract class SubCommand {

    private final String name, permission, description;
    private final List<String> aliases;
    private final ExecutorType executorType;
    protected final Map<String, SubCommand> subcommands = new LinkedHashMap<>();
    protected final Map<String, Argument> arguments = new LinkedHashMap<>();

    protected CommandSender sender;
    protected String[] args;

    protected SubCommand(String name) {
        this(name, null, null, Collections.emptyList(), ExecutorType.BOTH);
    }

    protected SubCommand(String name, String permission) {
        this(name, permission, null, Collections.emptyList(), ExecutorType.BOTH);
    }

    protected SubCommand(String name, String permission, String description) {
        this(name, permission, description, Collections.emptyList(), ExecutorType.BOTH);
    }

    protected SubCommand(String name, String permission, String description, List<String> aliases) {
        this(name, permission, description, aliases, ExecutorType.BOTH);
    }

    protected SubCommand(String name, String permission, String description, List<String> aliases, ExecutorType executorType) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.aliases = aliases != null ? new ArrayList<>(aliases) : new ArrayList<>();
        this.executorType = executorType != null ? executorType : ExecutorType.BOTH;
    }

    public abstract void run();

    public void setExecutionContext(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subcommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    public void registerArgument(Argument argument) {
        arguments.put(argument.getName().toLowerCase(), argument);
    }

    public SubCommand getSubcommand(String name) {
        return subcommands.get(name.toLowerCase());
    }

    public Argument getArgument(String name) {
        return arguments.get(name.toLowerCase());
    }

    public boolean hasPermission() {
        return permission != null && !permission.isEmpty();
    }

    public boolean matchesName(String input) {
        if (name.equalsIgnoreCase(input)) return true;
        return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(input));
    }
}
