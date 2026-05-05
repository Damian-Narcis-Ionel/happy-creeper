package com.damian.happycreeper.client;

import java.util.Map;

import com.damian.happycreeper.HappyCreeper;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SkullBlock;

public final class HappyCreeperMaskLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private static final Identifier MASK_TEXTURE = Identifier.fromNamespaceAndPath("happycreeper", "textures/item/happycreeper.png");
    private final SkullModelBase creeperHeadModel;

    public HappyCreeperMaskLayer(RenderLayerParent<AvatarRenderState, PlayerModel> renderer,
            net.minecraft.client.model.geom.EntityModelSet modelSet) {
        super(renderer);
        Map<SkullBlock.Type, SkullModelBase> skullModels = Map.of(
                SkullBlock.Types.CREEPER,
                SkullBlockRenderer.createModel(modelSet, SkullBlock.Types.CREEPER));
        this.creeperHeadModel = skullModels.get(SkullBlock.Types.CREEPER);
    }

    @Override
    public void submit(PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            AvatarRenderState renderState,
            float yRot,
            float xRot) {
        Player player = findPlayer(renderState);
        if (renderState.isInvisible || player == null || !player.getItemBySlot(EquipmentSlot.HEAD).is(HappyCreeper.FAKE_HAPPY_CREEPER_HEAD) || this.creeperHeadModel == null) {
            return;
        }

        poseStack.pushPose();
        getParentModel().getHead().translateAndRotate(poseStack);
        poseStack.scale(1.22F, -1.22F, -1.22F);
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        SkullBlockRenderer.submitSkull(null,
                180.0F,
                0.0F,
                poseStack,
                submitNodeCollector,
                packedLight,
                this.creeperHeadModel,
                SkullBlockRenderer.getSkullRenderType(SkullBlock.Types.CREEPER, MASK_TEXTURE),
                0,
                null);
        poseStack.popPose();
    }

    private static Player findPlayer(AvatarRenderState renderState) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        return mc.level.getEntity(renderState.id) instanceof Player player ? player : null;
    }
}
