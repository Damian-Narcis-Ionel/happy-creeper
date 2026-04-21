package com.damian.happycreeper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class HappyCreeperNetwork {
    private static final String NETWORK_VERSION = "1";

    private HappyCreeperNetwork() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(CreeperAppearancePayload.TYPE, CreeperAppearancePayload.STREAM_CODEC, CreeperAppearancePayload::handle);
    }

    public static void syncAppearance(Creeper creeper, int variant) {
        PacketDistributor.sendToPlayersTrackingEntity(creeper, new CreeperAppearancePayload(creeper.getId(), variant));
    }

    public static void syncAppearanceToPlayer(ServerPlayer player, Creeper creeper, int variant) {
        PacketDistributor.sendToPlayer(player, new CreeperAppearancePayload(creeper.getId(), variant));
    }
}
