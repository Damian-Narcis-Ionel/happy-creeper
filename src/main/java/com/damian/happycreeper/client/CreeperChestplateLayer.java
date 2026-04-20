package com.damian.happycreeper.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public final class CreeperChestplateLayer extends RenderLayer<CreeperRenderState, CreeperModel> {
    private final CreeperChestplateModel model;
    private final EquipmentLayerRenderer equipmentRenderer;

    public CreeperChestplateLayer(RenderLayerParent<CreeperRenderState, CreeperModel> renderer,
            EntityModelSet modelSet,
            EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.model = new CreeperChestplateModel(modelSet.bakeLayer(CreeperChestplateModel.LAYER_LOCATION));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, CreeperRenderState renderState, float yRot, float xRot) {
        ItemStack chestplate = renderState.getRenderDataOrDefault(HappyCreeperRenderStateKeys.CREEPER_CHESTPLATE, ItemStack.EMPTY);
        Equippable equippable = chestplate.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != EquipmentSlot.CHEST || equippable.assetId().isEmpty()) {
            return;
        }

        this.model.setupAnim(renderState);
        this.equipmentRenderer.renderLayers(
                EquipmentClientInfo.LayerType.HUMANOID,
                equippable.assetId().orElseThrow(),
                this.model,
                renderState,
                chestplate,
                poseStack,
                submitNodeCollector,
                packedLight,
                renderState.outlineColor);
    }
}
