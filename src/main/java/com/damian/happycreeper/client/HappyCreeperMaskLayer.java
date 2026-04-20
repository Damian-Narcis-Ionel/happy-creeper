package com.damian.happycreeper.client;

import com.damian.happycreeper.HappyCreeper;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SkullBlock;

public final class HappyCreeperMaskLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier MASK_TEXTURE = Identifier.fromNamespaceAndPath("happycreeper", "textures/item/happycreeper.png");
    private static final int FULL_COLOR = 0xFFFFFFFF;
    private final SkullModelBase creeperHeadModel;

    public HappyCreeperMaskLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.creeperHeadModel = SkullBlockRenderer.createModel(modelSet, SkullBlock.Types.CREEPER);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState renderState, float yRot, float xRot) {
        ItemStack headStack = renderState.getRenderData(HappyCreeperRenderStateKeys.PLAYER_HEAD_ITEM);
        if (renderState.isInvisible || this.creeperHeadModel == null || headStack == null || !headStack.is(HappyCreeper.FAKE_HAPPY_CREEPER_HEAD.get())) {
            return;
        }

        poseStack.pushPose();
        getParentModel().root().translateAndRotate(poseStack);
        getParentModel().head.translateAndRotate(poseStack);
        poseStack.scale(1.1875F, -1.1875F, -1.1875F);
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        RenderType renderType = SkullBlockRenderer.getSkullRenderType(SkullBlock.Types.CREEPER, MASK_TEXTURE);
        SkullBlockRenderer.submitSkull(
                null,
                180.0F,
                0.0F,
                poseStack,
                submitNodeCollector,
                packedLight,
                this.creeperHeadModel,
                renderType,
                FULL_COLOR,
                null);
        poseStack.popPose();
    }
}
