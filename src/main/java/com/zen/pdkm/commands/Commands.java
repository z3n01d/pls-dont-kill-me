package com.zen.pdkm.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.zen.pdkm.data.PlayerData;
import com.zen.pdkm.persistent_states.StateSaverAndLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher,registryAccess,environment) -> {
            final LiteralCommandNode<ServerCommandSource> pdkmNode = dispatcher.register(literal("plsdontkillme")
                    .executes(context -> {

                        if (!context.getSource().isExecutedByPlayer()) return 0;

                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) return 0;
                        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

                        if (playerData.pvpTicks <= 0 || !playerData.pvp) {
                            if (!playerData.pvp) {
                                playerData.pvpTicks = 0;
                            }
                            playerData.pvp = !playerData.pvp;
                        } else {
                            double pvpSeconds = playerData.pvpTicks / 20.0;
                            int minutes = (int) ((pvpSeconds % 3600) / 60);
                            int seconds = (int) (pvpSeconds % 60);

                            context.getSource().sendError(Text.of("You're in combat.\nYou'll exit combat mode in %02d minutes and %02d seconds".formatted(minutes,seconds)));
                            return 0;
                        }

                        if (playerData.pvp) {
                            context.getSource().sendFeedback(() -> Text.of("You can attack/be attacked by players."),false);
                        } else {
                            context.getSource().sendFeedback(() -> Text.of("You are safe from players."),false);
                        }

                        return 1;
                    })
                    .then(literal("status").executes(context -> {
                        if (!context.getSource().isExecutedByPlayer()) return 0;

                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) return 0;
                        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

                        if (playerData.pvp) {
                            context.getSource().sendFeedback(() -> Text.of("You can attack/be attacked by players."),false);
                        } else {
                            context.getSource().sendFeedback(() -> Text.of("You are safe from players."),false);
                        }

                        return 1;
                    }))
            );

            dispatcher.register(literal("pdkm").redirect(pdkmNode));
        }
        );
    }
}
