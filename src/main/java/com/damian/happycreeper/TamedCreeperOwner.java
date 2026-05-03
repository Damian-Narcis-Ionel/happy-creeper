package com.damian.happycreeper;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

public final class TamedCreeperOwner {
    private static final String OWNER_UUID_TAG = "HappyCreeperOwner";
    private static final String OWNER_NAME_TAG = "HappyCreeperOwnerName";

    private TamedCreeperOwner() {
    }

    public static void setOwner(Creeper creeper, Player player) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        data.putUUID(OWNER_UUID_TAG, player.getUUID());
        data.putString(OWNER_NAME_TAG, player.getGameProfile().getName());
    }

    public static void setOwner(Creeper creeper, UUID ownerUuid) {
        IPersistentDataProvider.of(creeper).putUUID(OWNER_UUID_TAG, ownerUuid);
    }

    public static Optional<UUID> getOwnerUuid(Creeper creeper) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        if (!data.hasUUID(OWNER_UUID_TAG)) {
            return Optional.empty();
        }

        return Optional.of(data.getUUID(OWNER_UUID_TAG));
    }

    public static Optional<String> getOwnerName(Creeper creeper) {
        String ownerName = IPersistentDataProvider.of(creeper).getString(OWNER_NAME_TAG);
        return ownerName.isBlank() ? Optional.empty() : Optional.of(ownerName);
    }

    public static boolean hasOwner(Creeper creeper) {
        return getOwnerUuid(creeper).isPresent() || getOwnerName(creeper).isPresent();
    }

    public static boolean isOwner(Creeper creeper, Player player) {
        if (getOwnerUuid(creeper).map(ownerUuid -> ownerUuid.equals(player.getUUID())).orElse(false)) {
            return true;
        }

        if (getOwnerName(creeper)
                .filter(ownerName -> ownerName.equals(player.getGameProfile().getName()))
                .isPresent()) {
            setOwner(creeper, player);
            return true;
        }

        MinecraftServer server = creeper.getServer();
        if (server != null && server.isSingleplayer() && server.isSingleplayerOwner(player.getGameProfile())) {
            setOwner(creeper, player);
            return true;
        }

        return false;
    }

    public static Optional<ServerPlayer> getOwner(Creeper creeper) {
        MinecraftServer server = creeper.getServer();
        if (server == null) {
            return Optional.empty();
        }

        Optional<ServerPlayer> ownerByUuid = getOwnerUuid(creeper)
                .map(server.getPlayerList()::getPlayer)
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast);
        if (ownerByUuid.isPresent()) {
            return ownerByUuid;
        }

        Optional<ServerPlayer> ownerByName = getOwnerName(creeper)
                .flatMap(ownerName -> server.getPlayerList().getPlayers().stream()
                        .filter(player -> ownerName.equals(player.getGameProfile().getName()))
                        .findFirst());
        if (ownerByName.isPresent()) {
            setOwner(creeper, ownerByName.get());
            return ownerByName;
        }

        if (!server.isSingleplayer()) {
            return Optional.empty();
        }

        return server.getPlayerList().getPlayers().stream()
                .filter(player -> server.isSingleplayerOwner(player.getGameProfile()))
                .findFirst()
                .map(player -> {
                    setOwner(creeper, player);
                    return player;
                });
    }
}
