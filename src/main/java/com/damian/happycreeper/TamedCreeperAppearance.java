package com.damian.happycreeper;

import com.damian.happycreeper.network.SyncColorVariantPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperAppearance {
    public static final int NONE_VARIANT = 0;
    public static final int HAPPY_VARIANT = 1;
    public static final int BLUE_VARIANT = 2;
    public static final int CYAN_VARIANT = 3;
    public static final int GRAY_VARIANT = 4;
    public static final int YELLOW_VARIANT = 5;
    public static final int PINK_VARIANT = 6;
    public static final int PURPLE_VARIANT = 7;
    public static final int RED_VARIANT = 8;
    public static final int BLACK_VARIANT = 9;
    public static final int RAINBOW_VARIANT = 10;

    public static final String COLOR_VARIANT_TAG = "HappyCreeperColorVariant";

    private TamedCreeperAppearance() {}

    public static int getVariant(Creeper creeper) {
        return IPersistentDataProvider.of(creeper).getInt(COLOR_VARIANT_TAG);
    }

    public static void setVariant(Creeper creeper, int variant) {
        IPersistentDataProvider.of(creeper).putInt(COLOR_VARIANT_TAG, variant);
        if (!creeper.level().isClientSide()) {
            SyncColorVariantPacket packet = new SyncColorVariantPacket(creeper.getId(), variant);
            PlayerLookup.tracking(creeper).forEach(player -> ServerPlayNetworking.send(player, packet));
        }
    }

    public static void syncToPlayer(Creeper creeper, ServerPlayer player) {
        int variant = getVariant(creeper);
        if (variant != NONE_VARIANT) {
            ServerPlayNetworking.send(player, new SyncColorVariantPacket(creeper.getId(), variant));
        }
    }

    public static void ensureTamedAppearance(Creeper creeper) {
        if (CreeperState.get(creeper) == CreeperState.TAMED && getVariant(creeper) == NONE_VARIANT) {
            setVariant(creeper, HAPPY_VARIANT);
        }
    }
}
