package com.damian.happycreeper;

import com.mojang.serialization.Codec;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class TamedCreeperAppearance {
    private TamedCreeperAppearance() {
    }

    public static void init() {
        // Forces class loading during mod initialization so the attachment registers in time.
    }

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BLUE_RECOLOR = HappyCreeper.ATTACHMENTS.register(
            "blue_recolor",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL)
                    .sync(ByteBufCodecs.BOOL)
                    .build());

    public static boolean isBlue(Creeper creeper) {
        return creeper.getData(BLUE_RECOLOR.get());
    }

    public static void setBlue(Creeper creeper, boolean blue) {
        creeper.setData(BLUE_RECOLOR.get(), blue);
        creeper.syncData(BLUE_RECOLOR.get());
    }
}
