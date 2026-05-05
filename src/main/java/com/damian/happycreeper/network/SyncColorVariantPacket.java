package com.damian.happycreeper.network;

import com.damian.happycreeper.HappyCreeper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncColorVariantPacket(int entityId, int variant) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncColorVariantPacket> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "sync_color_variant"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncColorVariantPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncColorVariantPacket::entityId,
            ByteBufCodecs.VAR_INT, SyncColorVariantPacket::variant,
            SyncColorVariantPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
