package com.damian.happycreeper;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CreeperAppearancePayload(int entityId, int variant) implements CustomPacketPayload {
    public static final Type<CreeperAppearancePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_appearance"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CreeperAppearancePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CreeperAppearancePayload::entityId,
            ByteBufCodecs.VAR_INT,
            CreeperAppearancePayload::variant,
            CreeperAppearancePayload::new);

    @Override
    public Type<CreeperAppearancePayload> type() {
        return TYPE;
    }

    public static void handle(CreeperAppearancePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().level().getEntity(payload.entityId()) instanceof Creeper creeper) {
                creeper.setData(TamedCreeperAppearance.COLOR_VARIANT.get(), payload.variant());
            }
        });
    }
}
