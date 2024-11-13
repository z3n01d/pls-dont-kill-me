package com.zen.pdkm.persistent_states;

import com.zen.pdkm.PlsDontKillMe;
import com.zen.pdkm.data.PlayerData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public static String PLAYERS_NBT_KEY = "plsdontkillme$players";
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));

        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        StateSaverAndLoader state = persistentStateManager.getOrCreate(
                StateSaverAndLoader::createFromNbt,
                StateSaverAndLoader::new,
                PlsDontKillMe.MOD_ID
        );

        state.markDirty();

        return state;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {

        StateSaverAndLoader state = new StateSaverAndLoader();

        NbtCompound playersNbt = tag.getCompound(PLAYERS_NBT_KEY);

        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();

            playerData.pvp = playersNbt.getCompound(key).getBoolean("pvp");
            playerData.pvpTicks = playersNbt.getCompound(key).getLong("pvpTicks");

            UUID uuid = UUID.fromString(key);

            state.players.put(uuid,playerData);
        });

        return state;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound playersNbt = new NbtCompound();

        this.players.forEach((uuid,playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            playerNbt.putBoolean("pvp",playerData.pvp);
            playerNbt.putLong("pvpTicks",playerData.pvpTicks);

            playersNbt.put(uuid.toString(),playerNbt);
        });

        nbt.put(PLAYERS_NBT_KEY,playersNbt);

        return nbt;
    }
}
