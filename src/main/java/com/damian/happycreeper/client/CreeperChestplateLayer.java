package com.damian.happycreeper.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class CreeperChestplateLayer extends RenderLayer<Creeper, CreeperModel<Creeper>> {
    private final CreeperChestplateModel model;
    private final TextureAtlas armorTrimAtlas;

    public CreeperChestplateLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> renderer,
            EntityModelSet modelSet,
            ModelManager modelManager) {
        super(renderer);
        this.model = new CreeperChestplateModel(modelSet.bakeLayer(CreeperChestplateModel.LAYER_LOCATION));
        this.armorTrimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    @Override
    public void render(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Creeper creeper,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        ItemStack chestplate = creeper.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestplate.getItem() instanceof ArmorItem armorItem) || armorItem.getEquipmentSlot() != EquipmentSlot.CHEST) {
            return;
        }

        this.model.setupAnim(creeper, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        IClientItemExtensions extensions = IClientItemExtensions.of(chestplate);
        extensions.setupModelAnimations(creeper, chestplate, EquipmentSlot.CHEST, this.model, limbSwing, limbSwingAmount, partialTick, ageInTicks, netHeadYaw, headPitch);

        int overlay = LivingEntityRenderer.getOverlayCoords(creeper, 0.0F);
        int fallbackColor = extensions.getDefaultDyeColor(chestplate);
        Holder<ArmorMaterial> armorMaterial = armorItem.getMaterial();

        for (int layerIndex = 0; layerIndex < armorMaterial.value().layers().size(); layerIndex++) {
            ArmorMaterial.Layer armorLayer = armorMaterial.value().layers().get(layerIndex);
            int color = extensions.getArmorLayerTintColor(chestplate, creeper, armorLayer, layerIndex, fallbackColor);
            if (color == 0) {
                continue;
            }

            ResourceLocation texture = ClientHooks.getArmorTexture(creeper, chestplate, armorLayer, false, EquipmentSlot.CHEST);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(texture));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, color);
        }

        ArmorTrim trim = chestplate.get(DataComponents.TRIM);
        if (trim != null) {
            TextureAtlasSprite trimSprite = this.armorTrimAtlas.getSprite(trim.outerTexture(armorMaterial));
            VertexConsumer vertexConsumer = trimSprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (chestplate.hasFoil()) {
            this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
