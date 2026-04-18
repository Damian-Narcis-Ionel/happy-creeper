package com.damian.happycreeper.client;

import com.damian.happycreeper.HappyCreeper;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SkullBlock;

public final class HappyCreeperMaskLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
    private static final ResourceLocation MASK_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/happycreeper.png");
    private final SkullModelBase creeperHeadModel;

    public HappyCreeperMaskLayer(RenderLayerParent<PlayerRenderState, PlayerModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.creeperHeadModel = SkullBlockRenderer.createModel(modelSet, SkullBlock.Types.CREEPER);
    }

    @Override
    public void render(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            PlayerRenderState renderState,
            float yRot,
            float xRot) {
        if (renderState.isInvisible || this.creeperHeadModel == null || !renderState.headEquipment.is(HappyCreeper.FAKE_HAPPY_CREEPER_HEAD.get())) {
            return;
        }

        poseStack.pushPose();
        getParentModel().root().translateAndRotate(poseStack);
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.scale(1.22F, -1.22F, -1.22F);
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        SkullBlockRenderer.renderSkull(
                null,
                180.0F,
                0.0F,
                poseStack,
                bufferSource,
                packedLight,
                this.creeperHeadModel,
                SkullBlockRenderer.getRenderType(SkullBlock.Types.CREEPER, null, MASK_TEXTURE));
        poseStack.popPose();
    }
}
