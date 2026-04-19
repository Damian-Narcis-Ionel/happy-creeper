package com.damian.happycreeper.client;

import com.damian.happycreeper.CreeperState;
import com.damian.happycreeper.HappyCreeper;
import com.damian.happycreeper.TamedCreeperAppearance;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID, value = Dist.CLIENT)
public final class TamedCreeperRenderHandler {
    private TamedCreeperRenderHandler() {
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED
                || TamedCreeperAppearance.getVariant(creeper) != TamedCreeperAppearance.BLUE_VARIANT) {
            return;
        }

        RenderSystem.setShaderColor(0.45F, 0.65F, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof Creeper) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
