package com.damian.happycreeper.client;

import com.damian.happycreeper.TamedCreeperAppearance;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;

public final class BlueCreeperTintLayer extends RenderLayer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation CREEPER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");
    private static final int BLUE_TINT = 0xFF73A7FF;

    public BlueCreeperTintLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> renderer) {
        super(renderer);
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Creeper creeper,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        if (!TamedCreeperAppearance.isBlue(creeper)) {
            return;
        }

        renderColoredCutoutModel(getParentModel(), CREEPER_TEXTURE, poseStack, bufferSource, packedLight, creeper, BLUE_TINT);
    }
}
