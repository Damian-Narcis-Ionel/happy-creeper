package com.damian.happycreeper.client;

import com.damian.happycreeper.TamedCreeperAppearance;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.monster.Creeper;

public final class CreeperVariantTextureLayer extends RenderLayer<Creeper, CreeperModel<Creeper>> {
    private static final ResourceLocation HAPPY_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/happycreeper.png");
    private static final ResourceLocation BLUE_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/blue_creeper.png");
    private static final ResourceLocation CYAN_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/cyan_creeper.png");
    private static final ResourceLocation GRAY_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/gray_creeper.png");
    private static final ResourceLocation YELLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/yellow_creeper.png");
    private static final ResourceLocation PINK_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/pink_creeper.png");
    private static final ResourceLocation PURPLE_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/purple_creeper.png");
    private static final ResourceLocation RED_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/red_creeper.png");
    private static final ResourceLocation BLACK_TEXTURE = ResourceLocation.fromNamespaceAndPath("happycreeper", "textures/item/black_creeper.png");
    private static final int FULL_COLOR = 0xFFFFFFFF;
    private static final int RAINBOW_CYCLE_TICKS = 8;
    private static final ResourceLocation[] RAINBOW_TEXTURES = new ResourceLocation[] {
            RED_TEXTURE,
            YELLOW_TEXTURE,
            HAPPY_TEXTURE,
            CYAN_TEXTURE,
            BLUE_TEXTURE,
            PURPLE_TEXTURE,
            PINK_TEXTURE
    };

    public CreeperVariantTextureLayer(RenderLayerParent<Creeper, CreeperModel<Creeper>> renderer) {
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
        int variant = TamedCreeperAppearance.getVariant(creeper);
        ResourceLocation texture = switch (variant) {
            case TamedCreeperAppearance.HAPPY_VARIANT -> HAPPY_TEXTURE;
            case TamedCreeperAppearance.BLUE_VARIANT -> BLUE_TEXTURE;
            case TamedCreeperAppearance.CYAN_VARIANT -> CYAN_TEXTURE;
            case TamedCreeperAppearance.GRAY_VARIANT -> GRAY_TEXTURE;
            case TamedCreeperAppearance.YELLOW_VARIANT -> YELLOW_TEXTURE;
            case TamedCreeperAppearance.PINK_VARIANT -> PINK_TEXTURE;
            case TamedCreeperAppearance.PURPLE_VARIANT -> PURPLE_TEXTURE;
            case TamedCreeperAppearance.RED_VARIANT -> RED_TEXTURE;
            case TamedCreeperAppearance.BLACK_VARIANT -> BLACK_TEXTURE;
            case TamedCreeperAppearance.RAINBOW_VARIANT -> null;
            default -> null;
        };

        if (variant == TamedCreeperAppearance.RAINBOW_VARIANT) {
            renderRainbow(poseStack, bufferSource, packedLight, creeper, partialTick);
            return;
        }

        if (texture == null) {
            return;
        }

        renderColoredCutoutModel(getParentModel(), texture, poseStack, bufferSource, packedLight, creeper, FULL_COLOR);
    }

    private void renderRainbow(com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Creeper creeper,
            float partialTick) {
        RainbowFrame frame = getRainbowFrame(creeper, partialTick);
        renderTranslucentTexture(poseStack, bufferSource, packedLight, creeper, frame.currentTexture, FULL_COLOR);

        if (frame.blend > 0.0F) {
            int overlayColor = FastColor.ARGB32.color(Math.round(frame.blend * 255.0F), 255, 255, 255);
            renderTranslucentTexture(poseStack, bufferSource, packedLight, creeper, frame.nextTexture, overlayColor);
        }
    }

    private void renderTranslucentTexture(com.mojang.blaze3d.vertex.PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            Creeper creeper,
            ResourceLocation texture,
            int color) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        getParentModel().renderToBuffer(poseStack,
                vertexConsumer,
                packedLight,
                LivingEntityRenderer.getOverlayCoords(creeper, 0.0F),
                color);
    }

    private static RainbowFrame getRainbowFrame(Creeper creeper, float partialTick) {
        float cycleProgress = (((float) (creeper.tickCount % RAINBOW_CYCLE_TICKS)) + partialTick) / (float) RAINBOW_CYCLE_TICKS;
        int textureIndex = (creeper.tickCount / RAINBOW_CYCLE_TICKS + creeper.getId()) % RAINBOW_TEXTURES.length;
        int nextTextureIndex = (textureIndex + 1) % RAINBOW_TEXTURES.length;
        return new RainbowFrame(RAINBOW_TEXTURES[textureIndex], RAINBOW_TEXTURES[nextTextureIndex], cycleProgress);
    }

    private record RainbowFrame(ResourceLocation currentTexture, ResourceLocation nextTexture, float blend) {
    }
}
