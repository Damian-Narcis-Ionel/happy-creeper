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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

public final class CreeperHelmetLayer extends RenderLayer<Creeper, CreeperModel<Creeper>> {
    private final CreeperHelmetModel model;
    private final TextureAtlas armorTrimAtlas;

    public CreeperHelmetLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> renderer,
            EntityModelSet modelSet,
            ModelManager modelManager) {
        super(renderer);
        this.model = new CreeperHelmetModel(modelSet.bakeLayer(CreeperHelmetModel.LAYER_LOCATION));
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
        ItemStack helmet = creeper.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof ArmorItem armorItem) || armorItem.getEquipmentSlot() != EquipmentSlot.HEAD) {
            return;
        }

        this.model.setupAnim(creeper, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        int overlay = LivingEntityRenderer.getOverlayCoords(creeper, 0.0F);
        Holder<ArmorMaterial> armorMaterial = armorItem.getMaterial();
        int dyeColor = helmet.has(DataComponents.DYED_COLOR)
                ? helmet.get(DataComponents.DYED_COLOR).rgb() | 0xFF000000
                : 0xFFFFFFFF;

        for (ArmorMaterial.Layer armorLayer : armorMaterial.value().layers()) {
            int color = armorLayer.dyeable() ? dyeColor : 0xFFFFFFFF;
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(armorLayer.texture(false)));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, overlay, color);
        }

        ArmorTrim trim = helmet.get(DataComponents.TRIM);
        if (trim != null) {
            TextureAtlasSprite trimSprite = this.armorTrimAtlas.getSprite(trim.outerTexture(armorMaterial));
            VertexConsumer vertexConsumer = trimSprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }

        if (helmet.hasFoil()) {
            this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.armorEntityGlint()), packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
