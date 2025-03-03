package arathain.amogus.command;

import arathain.amogus.EliminatePlayers;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;

public class EliminatePlayersCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("eliminateplayers")
                        .requires(source -> source.hasPermissionLevel(2)) // Requires OP level 2
                        // Toggle functionality
                        .then(literal("toggle")
                                .then(argument("enabled", bool())
                                        .executes(context -> toggleFunctionality(context, getBool(context, "enabled")))
                                )
                        )
                        // Check status
                        .then(literal("status")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.literal("EliminatePlayers functionality is " + (EliminatePlayers.enabled ? "enabled" : "disabled")), true);
                                    return 1;
                                })
                        )
                        // Add players to banned list
                        .then(literal("add")
                                .then(argument("players", EntityArgumentType.players())
                                        .executes(context -> addBannedPlayers(context, EntityArgumentType.getPlayers(context, "players")))
                                )
                        )
                        // Remove players from banned list
                        .then(literal("remove")
                                .then(argument("players", EntityArgumentType.players())
                                        .executes(context -> removeBannedPlayers(context, EntityArgumentType.getPlayers(context, "players")))
                                )
                        )
                        // List banned UUIDs
                        .then(literal("list")
                                .executes(EliminatePlayersCommand::listBannedUuids)
                        )
        );
    }

    // Toggle functionality
    private static int toggleFunctionality(CommandContext<ServerCommandSource> context, boolean enabled) {
        EliminatePlayers.enabled = enabled;
        context.getSource().sendFeedback(Text.literal("EliminatePlayers functionality is now " + (enabled ? "enabled" : "disabled")), true);
        return 1;
    }

    // Add players to banned list
    private static int addBannedPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            UUID uuid = player.getUuid();
            if (EliminatePlayers.bannedUuids.contains(uuid)) {
                context.getSource().sendFeedback(Text.literal("Player " + player.getName().getString() + " is already banned."), true);
            } else {
                EliminatePlayers.bannedUuids.add(uuid);
                context.getSource().sendFeedback(Text.literal("Added player " + player.getName().getString() + " to the banned list."), true);
            }
        }
        return 1;
    }

    // Remove players from banned list
    private static int removeBannedPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            UUID uuid = player.getUuid();
            if (EliminatePlayers.bannedUuids.remove(uuid)) {
                context.getSource().sendFeedback(Text.literal("Removed player " + player.getName().getString() + " from the banned list."), true);
            } else {
                context.getSource().sendFeedback(Text.literal("Player " + player.getName().getString() + " is not in the banned list."), true);
            }
        }
        return 1;
    }

    // List banned UUIDs
    private static int listBannedUuids(CommandContext<ServerCommandSource> context) {
        if (EliminatePlayers.bannedUuids.isEmpty()) {
            context.getSource().sendFeedback(Text.literal("No players are currently banned."), true);
        } else {
            context.getSource().sendFeedback(Text.literal("Banned players:"), true);
            for (UUID uuid : EliminatePlayers.bannedUuids) {
                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(uuid);
                if (player != null) {
                    context.getSource().sendFeedback(Text.literal("- " + player.getName().getString()), true);
                } else {
                    context.getSource().sendFeedback(Text.literal("- " + uuid.toString()), true);
                }
            }
        }
        return 1;
    }
}