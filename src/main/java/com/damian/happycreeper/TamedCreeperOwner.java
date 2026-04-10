package com.damian.happycreeper;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

public final class TamedCreeperOwner {
    private static final String OWNER_UUID_TAG = "HappyCreeperOwner";

    private TamedCreeperOwner() {
    }

    public static void setOwner(Creeper creeper, UUID ownerUuid) {
        creeper.getPersistentData().putUUID(OWNER_UUID_TAG, ownerUuid);
    }

    public static Optional<UUID> getOwnerUuid(Creeper creeper) {
        CompoundTag data = creeper.getPersistentData();
        if (!data.hasUUID(OWNER_UUID_TAG)) {
            return Optional.empty();
        }

        return Optional.of(data.getUUID(OWNER_UUID_TAG));
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
        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        return getOwnerUuid(creeper)
                .map(serverLevel::getPlayerByUUID)
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast);
    }
}
