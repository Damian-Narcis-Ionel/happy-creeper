package com.damian.happycreeper.client;

import java.util.Comparator;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.phys.AABB;

public final class CreeperChestplateLayer extends RenderLayer<CreeperRenderState, CreeperModel> {
    private final CreeperChestplateModel model;
    private final EquipmentLayerRenderer equipmentRenderer;

    public CreeperChestplateLayer(RenderLayerParent<CreeperRenderState, CreeperModel> renderer,
            net.minecraft.client.model.geom.EntityModelSet modelSet,
            EquipmentLayerRenderer equipmentRenderer) {
        super(renderer);
        this.model = new CreeperChestplateModel(modelSet.bakeLayer(CreeperChestplateModel.LAYER_LOCATION));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void submit(PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            CreeperRenderState renderState,
            float yRot,
            float xRot) {
        Creeper creeper = findCreeperAt(renderState);
        if (creeper == null) return;

        ItemStack chestplate = creeper.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) return;

        Equippable equippable = chestplate.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != EquipmentSlot.CHEST) return;

        ResourceKey<EquipmentAsset> assetId = equippable.assetId().orElse(null);
        if (assetId == null) return;

        this.model.setupAnim(renderState);
        this.equipmentRenderer.renderLayers(EquipmentClientInfo.LayerType.HUMANOID, assetId, this.model, renderState, chestplate, poseStack,
                submitNodeCollector, packedLight, renderState.outlineColor);
    }

    private static Creeper findCreeperAt(CreeperRenderState renderState) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        double x = renderState.x, y = renderState.y, z = renderState.z;
        List<Creeper> candidates = mc.level.getEntitiesOfClass(Creeper.class,
                new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 1.5, z + 0.5));
        if (candidates.isEmpty()) return null;
        return candidates.stream()
                .min(Comparator.comparingDouble(c -> c.distanceToSqr(x, y, z)))
                .orElse(null);
    }
}
