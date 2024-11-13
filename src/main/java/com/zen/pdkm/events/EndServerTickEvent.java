package com.zen.pdkm.events;

import com.zen.pdkm.data.PlayerData;
import com.zen.pdkm.persistent_states.StateSaverAndLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class EndServerTickEvent implements ServerLifecycleEvents.ServerStarted,ServerTickEvents.EndTick {

    public static final long PVP_TICKS_COUNTDOWN = 10;

    private long tickCountdown = PVP_TICKS_COUNTDOWN;

    @Override
    public void onServerStarted(MinecraftServer minecraftServer) {
        this.tickCountdown = PVP_TICKS_COUNTDOWN;
    }

    @Override
    public void onEndTick(MinecraftServer server) {
        if (--this.tickCountdown <= 0L) {
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            // We don't use an enhanced for loop because this is faster + Player list isn't ordered
            for (int i = 0, size = players.size(); i < size; i++) {
                ServerPlayerEntity player = players.get(i);

                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

                playerData.pvpTicks -= PVP_TICKS_COUNTDOWN;
                playerData.pvpTicks = Math.max(0,playerData.pvpTicks);
            }
            this.tickCountdown = PVP_TICKS_COUNTDOWN;
        }
    }
}
