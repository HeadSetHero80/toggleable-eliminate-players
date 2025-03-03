package arathain.amogus;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;

public class EliminatePlayersCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("eliminateplayers")
                .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                .then(literal("toggle")
                        .then(argument("enabled", bool())
                                .executes(context -> toggleFunctionality(context, getBool(context, "enabled")))
                        )
                )
                .then(literal("status")
                        .executes(context -> {
                            context.getSource().sendFeedback(Text.literal("EliminatePlayers functionality is " + (EliminatePlayers.enabled ? "enabled" : "disabled")), true);
                            return 1;
                        })
                )
        );
    }

    private static int toggleFunctionality(CommandContext<ServerCommandSource> context, boolean enabled) {
        EliminatePlayers.enabled = enabled;
        context.getSource().sendFeedback(Text.literal("EliminatePlayers functionality is now " + (enabled ? "enabled" : "disabled")), true);
        return 1;
    }
}