package me.sirimperivm.commandsAPI.registry;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.sirimperivm.commandsAPI.command.Argument;
import me.sirimperivm.commandsAPI.command.CommandEntity;
import me.sirimperivm.commandsAPI.command.SubCommand;
import me.sirimperivm.commandsAPI.command.exception.CommandException;
import me.sirimperivm.commandsAPI.command.model.ExecutorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class CommandRegistry {

    private final Plugin plugin;
    private final List<CommandEntity> pendingCommands = new ArrayList<>();

    public CommandRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(CommandEntity command) {
        pendingCommands.add(command);
    }

    public void registerAll() {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands commands = event.registrar();
            for (CommandEntity cmd : pendingCommands) {
                LiteralArgumentBuilder<CommandSourceStack> builder = buildCommand(cmd);
                commands.register(builder.build(), cmd.getDescription(), cmd.getAliases());
            }
        });
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildCommand(CommandEntity command) {
        LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal(command.getName());

        for (SubCommand subCommand : command.getSubcommands().values()) {
            builder.then(buildSubCommand(subCommand, command));
        }

        if (!command.getArguments().isEmpty()) {
            builder.then(buildArgumentChain(command.getArguments().values().iterator(), command, null));
        }

        builder.executes(ctx -> executeCommand(ctx, command, null, Collections.emptyMap()));

        return builder;
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildSubCommand(SubCommand subCommand, CommandEntity rootCommand) {
        LiteralArgumentBuilder<CommandSourceStack> subBuilder = LiteralArgumentBuilder.literal(subCommand.getName());

        for (SubCommand nested : subCommand.getSubcommands().values()) {
            subBuilder.then(buildSubCommand(nested, rootCommand));
        }

        if (!subCommand.getArguments().isEmpty()) {
            subBuilder.then(buildArgumentChain(subCommand.getArguments().values().iterator(), rootCommand, subCommand));
        }

        subBuilder.executes(ctx -> executeCommand(ctx, rootCommand, subCommand, Collections.emptyMap()));

        return subBuilder;
    }

    private CommandNode<CommandSourceStack> buildArgumentChain(
            Iterator<Argument> iterator,
            CommandEntity rootCommand,
            SubCommand subCommand
    ) {
        if (!iterator.hasNext()) {
            return null;
        }

        Argument arg = iterator.next();
        RequiredArgumentBuilder<CommandSourceStack, ?> argBuilder = createArgumentBuilder(arg);

        if (iterator.hasNext()) {
            CommandNode<CommandSourceStack> next = buildArgumentChain(iterator, rootCommand, subCommand);
            if (next != null) {
                argBuilder.then(next);
            }
        }

        argBuilder.executes(ctx -> {
            Map<String, Object> parsedArgs = extractArguments(ctx, subCommand != null ? subCommand : rootCommand);
            return executeCommand(ctx, rootCommand, subCommand, parsedArgs);
        });

        return argBuilder.build();
    }

    private RequiredArgumentBuilder<CommandSourceStack, ?> createArgumentBuilder(Argument arg) {
        return switch (arg.getType()) {
            case INTEGER -> RequiredArgumentBuilder.argument(arg.getName(),
                    IntegerArgumentType.integer((int) arg.getMin(), (int) arg.getMax()));
            case LONG -> RequiredArgumentBuilder.argument(arg.getName(),
                    LongArgumentType.longArg(arg.getMin(), arg.getMax()));
            case DOUBLE -> RequiredArgumentBuilder.argument(arg.getName(),
                    DoubleArgumentType.doubleArg(arg.getMin(), arg.getMax()));
            case BOOLEAN -> RequiredArgumentBuilder.argument(arg.getName(),
                    BoolArgumentType.bool());
            default -> {
                if (arg.isGreedy()) {
                    yield RequiredArgumentBuilder.argument(arg.getName(), StringArgumentType.greedyString());
                } else {
                    yield RequiredArgumentBuilder.argument(arg.getName(), StringArgumentType.word());
                }
            }
        };
    }

    private Map<String, Object> extractArguments(CommandContext<CommandSourceStack> ctx, Object commandOrSub) {
        Map<String, Argument> args = commandOrSub instanceof CommandEntity ce
                ? ce.getArguments()
                : ((SubCommand) commandOrSub).getArguments();

        Map<String, Object> result = new HashMap<>();
        for (Argument arg : args.values()) {
            try {
                Object value = switch (arg.getType()) {
                    case INTEGER -> IntegerArgumentType.getInteger(ctx, arg.getName());
                    case LONG -> LongArgumentType.getLong(ctx, arg.getName());
                    case DOUBLE -> DoubleArgumentType.getDouble(ctx, arg.getName());
                    case BOOLEAN -> BoolArgumentType.getBool(ctx, arg.getName());
                    default -> StringArgumentType.getString(ctx, arg.getName());
                };
                result.put(arg.getName().toLowerCase(), value);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return result;
    }

    private int executeCommand(
            CommandContext<CommandSourceStack> ctx,
            CommandEntity command,
            SubCommand subCommand,
            Map<String, Object> parsedArgs
    ) throws CommandException{
        CommandSender sender = ctx.getSource().getSender();
        ExecutorType executorType = subCommand != null ? subCommand.getExecutorType() : command.getExecutorType();

        String permission = subCommand != null && subCommand.hasPermission()
                ? subCommand.getPermission()
                : command.getPermission();

        if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
            throw new CommandException("sender.no-permission");
        }

        checkExecutorType(sender, executorType);

        Map<String, Argument> targetArgs = subCommand != null ? subCommand.getArguments() : command.getArguments();
        for (Map.Entry<String, Object> entry : parsedArgs.entrySet()) {
            Argument arg = targetArgs.get(entry.getKey());
            if (arg != null) {
                arg.setValue(entry.getValue());
            }
        }

        try {
            if (subCommand != null) {
                subCommand.setExecutionContext(sender, new String[0]);
                subCommand.run();
            } else {
                command.setExecutionContext(sender, new String[0]);
                command.run();
            }
            return 1;
        } catch (Exception e) {
            plugin.getLogger().severe("Command execution error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    private void checkExecutorType(CommandSender sender, ExecutorType type) throws CommandException {
        switch (type) {
            case PLAYER -> {
                if (sender instanceof Player) return;
                throw new CommandException("executor-type.player");
            }
            case CONSOLE -> {
                if (sender instanceof ConsoleCommandSender) return;
                throw new CommandException("executor-type.console");
            }
        }
    }
}
