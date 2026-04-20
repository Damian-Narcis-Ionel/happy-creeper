package com.damian.happycreeper;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

public final class TamedCreeperOwner {
    private static final String OWNER_UUID_TAG = "HappyCreeperOwner";

    private TamedCreeperOwner() {
    }

    public static void setOwner(Creeper creeper, UUID ownerUuid) {
        creeper.getPersistentData().store(OWNER_UUID_TAG, UUIDUtil.CODEC, ownerUuid);
    }

    public static Optional<UUID> getOwnerUuid(Creeper creeper) {
        CompoundTag data = creeper.getPersistentData();
        return data.read(OWNER_UUID_TAG, UUIDUtil.CODEC);
    }

    public static boolean hasOwner(Creeper creeper) {
        return getOwnerUuid(creeper).isPresent();
    }

    public static boolean isOwner(Creeper creeper, Player player) {
        return getOwnerUuid(creeper)
                .map(ownerUuid -> ownerUuid.equals(player.getUUID()))
                .orElse(false);
    }

    public static Optional<ServerPlayer> getOwner(Creeper creeper) {
        MinecraftServer server = creeper.level().getServer();
        if (server == null) {
            return Optional.empty();
        }

        return getOwnerUuid(creeper)
                .map(server.getPlayerList()::getPlayer)
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast);
    }
}
