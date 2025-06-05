package net.francisco.teleportfx;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Formatting;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import java.util.Collection;

public class TeleportCommands {

    private static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION =
            new SimpleCommandExceptionType(Text.literal("Player not found or offline.").formatted(Formatting.RED));

    private static final SimpleCommandExceptionType SAME_PLAYER_EXCEPTION =
            new SimpleCommandExceptionType(Text.literal("You can't teleport to yourself!").formatted(Formatting.RED));

    public static final String SYMBOL_SPARKLES = "✨";
    public static final String SYMBOL_LIGHTNING = "⚡";
    public static final String SYMBOL_TARGET = "🎯";
    public static final String SYMBOL_STAR = "🌟";
    public static final String SYMBOL_GLOBE = "🌐";
    public static final String SYMBOL_PEOPLE = "👥";

    private static final SuggestionProvider<ServerCommandSource> PLAYER_SUGGESTIONS =
            (context, builder) -> {
                ServerCommandSource source = context.getSource();
                String input = builder.getRemaining().toLowerCase();
                source.getServer().getPlayerManager().getPlayerList().forEach(player -> {
                    String playerName = player.getName().getString();
                    if (playerName.toLowerCase().startsWith(input)) {
                        builder.suggest(playerName);
                    }
                });
                return builder.buildFuture();
            };

    private static final SuggestionProvider<ServerCommandSource> COORDINATE_SUGGESTIONS =
            (context, builder) -> {
                try {
                    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                    Vec3d pos = player.getPos();
                    builder.suggest(String.format("%.0f %.0f %.0f", pos.x, pos.y, pos.z),
                            Text.literal("Sua posição atual"));
                    builder.suggest(String.format("%.0f %.0f %.0f",
                                    Math.round(pos.x / 10.0) * 10,
                                    Math.round(pos.y / 10.0) * 10,
                                    Math.round(pos.z / 10.0) * 10),
                            Text.literal("Posição arredondada"));
                    builder.suggest(String.format("0 %.0f 0", pos.y),
                            Text.literal("Spawn horizontal"));
                } catch (Exception e) {
                    builder.suggest("0 64 0", Text.literal("Spawn world"));
                    builder.suggest("~ ~ ~", Text.literal("Posição relativa"));
                }
                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {

        dispatcher.register(CommandManager.literal("tpr")
                .requires(source -> source.hasPermissionLevel(ConfigManager.CONFIG.permissions.tprPermissionLevel))
                .then(CommandManager.argument("targetPlayer", StringArgumentType.word())
                        .suggests(PLAYER_SUGGESTIONS)
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity commandExecutor = source.getPlayerOrThrow();
                            String targetPlayerName = StringArgumentType.getString(context, "targetPlayer");

                            if (commandExecutor.getName().getString().equalsIgnoreCase(targetPlayerName)) {
                                throw SAME_PLAYER_EXCEPTION.create();
                            }
                            ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(targetPlayerName);
                            if (targetPlayer == null) {
                                throw PLAYER_NOT_FOUND_EXCEPTION.create();
                            }

                            Vec3d originPos = commandExecutor.getPos();
                            Vec3d targetPos = targetPlayer.getPos();
                            ServerWorld world = commandExecutor.getServerWorld();

                            if (ConfigManager.CONFIG.general.enableAllEffects) {
                                TeleportEffects.playTeleportEffects(world, originPos, targetPos, commandExecutor);
                            }

                            commandExecutor.teleport(targetPlayer.getServerWorld(),
                                    targetPos.x, targetPos.y, targetPos.z,
                                    commandExecutor.getYaw(), commandExecutor.getPitch());

                            if (ConfigManager.CONFIG.messages.sendExecutorFeedback) {
                                source.sendFeedback(() -> Text.literal(SYMBOL_SPARKLES + " Teleported to ")
                                        .formatted(Formatting.GREEN)
                                        .append(Text.literal(targetPlayerName).formatted(Formatting.YELLOW))
                                        .append(Text.literal("!").formatted(Formatting.LIGHT_PURPLE)), false);
                            }
                            if (ConfigManager.CONFIG.messages.notifyTargetPlayerOnTpr) {
                                targetPlayer.sendMessage(Text.literal(SYMBOL_LIGHTNING + " ")
                                        .formatted(Formatting.AQUA)
                                        .append(Text.literal(commandExecutor.getName().getString()).formatted(Formatting.YELLOW))
                                        .append(Text.literal(" se teleportou para você!").formatted(Formatting.GRAY)));
                            }
                            return 1;
                        })
                )
        );

        dispatcher.register(CommandManager.literal("tpcoord")
                .requires(source -> source.hasPermissionLevel(ConfigManager.CONFIG.permissions.tpcoordPermissionLevel))
                .then(CommandManager.argument("coordinates", Vec3ArgumentType.vec3())
                        .suggests(COORDINATE_SUGGESTIONS)
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity player = source.getPlayerOrThrow();
                            Vec3d coords = Vec3ArgumentType.getVec3(context, "coordinates");
                            return executeTeleportToCoordinates(source, player, coords);
                        })
                )
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("coordinates", Vec3ArgumentType.vec3())
                                .suggests(COORDINATE_SUGGESTIONS)
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
                                    ServerPlayerEntity targetPlayer = targets.iterator().next();
                                    Vec3d coords = Vec3ArgumentType.getVec3(context, "coordinates");
                                    return executeTeleportToCoordinates(source, targetPlayer, coords);
                                })
                        )
                )
        );

        dispatcher.register(CommandManager.literal("tphere")
                .requires(source -> source.hasPermissionLevel(ConfigManager.CONFIG.permissions.tpherePermissionLevel))
                .then(CommandManager.argument("targetPlayer", StringArgumentType.word())
                        .suggests(PLAYER_SUGGESTIONS)
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ServerPlayerEntity commandExecutor = source.getPlayerOrThrow();
                            String targetPlayerName = StringArgumentType.getString(context, "targetPlayer");

                            if (commandExecutor.getName().getString().equalsIgnoreCase(targetPlayerName)) {
                                throw SAME_PLAYER_EXCEPTION.create();
                            }
                            ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(targetPlayerName);
                            if (targetPlayer == null) {
                                throw PLAYER_NOT_FOUND_EXCEPTION.create();
                            }

                            Vec3d targetOriginPos = targetPlayer.getPos();
                            Vec3d executorPos = commandExecutor.getPos();
                            ServerWorld targetWorld = targetPlayer.getServerWorld(); // Mundo do alvo

                            if (ConfigManager.CONFIG.general.enableAllEffects) {
                                TeleportEffects.playTeleportEffects(targetWorld, targetOriginPos, executorPos, targetPlayer);
                            }

                            targetPlayer.teleport(commandExecutor.getServerWorld(),
                                    executorPos.x, executorPos.y, executorPos.z,
                                    targetPlayer.getYaw(), targetPlayer.getPitch());

                            if (ConfigManager.CONFIG.messages.sendExecutorFeedback) {
                                source.sendFeedback(() -> Text.literal(SYMBOL_STAR + " Invoked ")
                                        .formatted(Formatting.GREEN)
                                        .append(Text.literal(targetPlayerName).formatted(Formatting.YELLOW))
                                        .append(Text.literal("!").formatted(Formatting.LIGHT_PURPLE)), false);
                            }
                            if (ConfigManager.CONFIG.messages.notifyTargetPlayerOnTphere) {
                                targetPlayer.sendMessage(Text.literal(SYMBOL_SPARKLES + " You were magically summoned by ")
                                        .formatted(Formatting.AQUA)
                                        .append(Text.literal(commandExecutor.getName().getString()).formatted(Formatting.YELLOW))
                                        .append(Text.literal("!").formatted(Formatting.AQUA)));
                            }
                            return 1;
                        })
                )
        );

        dispatcher.register(CommandManager.literal("tplist")
                .requires(source -> source.hasPermissionLevel(ConfigManager.CONFIG.permissions.tplistPermissionLevel))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    var playerManager = source.getServer().getPlayerManager();
                    var players = playerManager.getPlayerList();

                    if (players.isEmpty()) {
                        source.sendFeedback(() -> Text.literal(SYMBOL_PEOPLE + " No Players Online.").formatted(Formatting.YELLOW), false);
                        return 0;
                    }
                    source.sendFeedback(() -> Text.literal(SYMBOL_GLOBE + " Players Online (" + players.size() + "):").formatted(Formatting.AQUA), false);

                    for (ServerPlayerEntity player : players) {
                        Vec3d pos = player.getPos();
                        source.sendFeedback(() -> Text.literal(SYMBOL_LIGHTNING + " " + player.getName().getString())
                                .formatted(Formatting.WHITE)
                                .append(Text.literal(String.format(" (%.0f, %.0f, %.0f)", pos.x, pos.y, pos.z))
                                        .formatted(Formatting.GRAY)), false);
                    }
                    return players.size();
                })
        );

        dispatcher.register(CommandManager.literal("tpmodconfig")
                .requires(source -> source.hasPermissionLevel(ConfigManager.CONFIG.permissions.configReloadPermissionLevel))
                .then(CommandManager.literal("reload")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            ConfigManager.loadConfig();
                            if (ConfigManager.CONFIG != null) {
                                source.sendFeedback(() -> Text.literal("TeleportFX config reloaded successfully!").formatted(Formatting.GREEN), true);
                                TeleportFX.LOGGER.info("TeleportFX configuration reloaded by " + source.getName());
                                return 1;
                            } else {
                                source.sendError(Text.literal("Failed to reload TeleportFX config. Check server logs. Defaults may have been loaded.").formatted(Formatting.RED));
                                TeleportFX.LOGGER.error("Failed to reload TeleportFX config when requested by " + source.getName() + ". ConfigManager.CONFIG might be null.");
                                return 0;
                            }
                        })
                )
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Usage: /tpmodconfig reload").formatted(Formatting.YELLOW), false);
                    return 0;
                })
        );

        TeleportFX.LOGGER.info("Comandos de teleporte com efeitos visuais (configuráveis) registrados!");
        TeleportFX.LOGGER.info("Use /tpmodconfig reload para recarregar a configuração.");
    }

    private static int executeTeleportToCoordinates(ServerCommandSource source, ServerPlayerEntity player, Vec3d coords) {
        double x = coords.x;
        double y = coords.y;
        double z = coords.z;

        if (y < ConfigManager.CONFIG.coordinateValidation.minY || y > ConfigManager.CONFIG.coordinateValidation.maxY) {
            source.sendError(Text.literal(String.format("Coordenada Y deve estar entre %d e %d!",
                            ConfigManager.CONFIG.coordinateValidation.minY, ConfigManager.CONFIG.coordinateValidation.maxY))
                    .formatted(Formatting.RED));
            return 0;
        }

        Vec3d originPos = player.getPos();
        Vec3d destinationPos = new Vec3d(x, y, z);
        ServerWorld world = player.getServerWorld();

        if (ConfigManager.CONFIG.general.enableAllEffects && ConfigManager.CONFIG.lightBeam.enableSimpleEffectsForTpcoord) {
            TeleportEffects.playCoordinateTeleportEffects(world, originPos, destinationPos, player);
        }

        player.teleport(player.getServerWorld(), x, y, z, player.getYaw(), player.getPitch());

        if (ConfigManager.CONFIG.messages.sendExecutorFeedback) {
            source.sendFeedback(() -> Text.literal(SYMBOL_TARGET + " Teleported to ")
                    .formatted(Formatting.GREEN)
                    .append(Text.literal(String.format("%.1f %.1f %.1f", x, y, z)).formatted(Formatting.YELLOW))
                    .append(Text.literal("!").formatted(Formatting.AQUA)), false);
        }
        return 1;
    }
}