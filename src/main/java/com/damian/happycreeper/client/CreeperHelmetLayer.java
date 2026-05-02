package com.damian.happycreeper.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.Equippable;

public final class CreeperHelmetLayer extends RenderLayer<CreeperRenderState, CreeperModel> {
    private final CreeperHelmetModel model;
    private final EquipmentLayerRenderer equipmentRenderer;

    public CreeperHelmetLayer(RenderLayerParent<CreeperRenderState, CreeperModel> renderer,
            net.minecraft.client.model.geom.EntityModelSet modelSet,
            EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.model = new CreeperHelmetModel(modelSet.bakeLayer(CreeperHelmetModel.LAYER_LOCATION));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CreeperRenderState renderState,
            float yRot,
            float xRot) {
        ItemStack helmet = renderState.headItem;
        if (helmet.isEmpty()) return;

        Equippable equippable = helmet.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != EquipmentSlot.HEAD) return;

        net.minecraft.resources.ResourceLocation modelId = equippable.model().orElse(null);
        if (modelId == null) return;

        this.model.setupAnim(renderState);
        this.equipmentRenderer.renderLayers(EquipmentModel.LayerType.HUMANOID, modelId, this.model, helmet, poseStack, bufferSource, packedLight);
    }
}
