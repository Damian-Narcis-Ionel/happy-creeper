package com.damian.happycreeper;

import com.mojang.serialization.Codec;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = HappyCreeper.MODID)
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

    private TamedCreeperAppearance() {
    }

    public static void init() {
        // Forces class loading during mod initialization so the attachment registers in time.
    }

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> COLOR_VARIANT = HappyCreeper.ATTACHMENTS.register(
            "color_variant",
            () -> AttachmentType.builder(() -> NONE_VARIANT)
                    .serialize(Codec.INT.fieldOf("value"))
                    .build());

    public static int getVariant(Creeper creeper) {
        return creeper.getData(COLOR_VARIANT.get());
    }

    public static void setVariant(Creeper creeper, int variant) {
        creeper.setData(COLOR_VARIANT.get(), variant);
        if (!creeper.level().isClientSide()) {
            HappyCreeperNetwork.syncAppearance(creeper, variant);
        }
    }

    public static void ensureTamedAppearance(Creeper creeper) {
        if (CreeperState.get(creeper) == CreeperState.TAMED && getVariant(creeper) == NONE_VARIANT) {
            setVariant(creeper, HAPPY_VARIANT);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof Creeper creeper && !creeper.level().isClientSide()) {
            ensureTamedAppearance(creeper);
        }
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(event.getTarget() instanceof Creeper creeper)) {
            return;
        }

        HappyCreeperNetwork.syncAppearanceToPlayer(player, creeper, getVariant(creeper));
    }
}
