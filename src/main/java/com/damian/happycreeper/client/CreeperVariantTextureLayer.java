package com.damian.happycreeper.client;

import com.damian.happycreeper.TamedCreeperAppearance;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public final class CreeperVariantTextureLayer extends RenderLayer<CreeperRenderState, CreeperModel> {
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

    public CreeperVariantTextureLayer(RenderLayerParent<CreeperRenderState, CreeperModel> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, CreeperRenderState renderState, float yRot, float xRot) {
        if (renderState.isInvisible) {
            return;
        }

        int variant = renderState.getRenderDataOrDefault(
                HappyCreeperRenderStateKeys.CREEPER_VARIANT,
                TamedCreeperAppearance.NONE_VARIANT);
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
            submitRainbow(poseStack, submitNodeCollector, packedLight, renderState);
            return;
        }

        if (texture == null) {
            return;
        }

        int overlay = LivingEntityRenderer.getOverlayCoords(renderState, 0.0F);
        renderColoredCutoutModel(getParentModel(), texture, poseStack, submitNodeCollector, packedLight, renderState, FULL_COLOR, overlay);
    }

    private void submitRainbow(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, CreeperRenderState renderState) {
        RainbowFrame frame = getRainbowFrame(renderState);
        submitTexture(poseStack, submitNodeCollector, packedLight, renderState, frame.currentTexture(), FULL_COLOR, 0);

        if (frame.blend() > 0.0F) {
            int alpha = Math.round(frame.blend() * 255.0F);
            int color = ARGB.color(alpha, 255, 255, 255);
            submitTexture(poseStack, submitNodeCollector, packedLight, renderState, frame.nextTexture(), color, 1);
        }
    }

    private void submitTexture(PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int packedLight,
            CreeperRenderState renderState,
            ResourceLocation texture,
            int color,
            int order) {
        OrderedSubmitNodeCollector ordered = submitNodeCollector.order(order);
        ordered.submitModel(
                getParentModel(),
                renderState,
                poseStack,
                RenderType.entityTranslucent(texture),
                packedLight,
                LivingEntityRenderer.getOverlayCoords(renderState, 0.0F),
                color,
                null,
                renderState.outlineColor,
                null);
    }

    private static RainbowFrame getRainbowFrame(CreeperRenderState renderState) {
        float cyclePosition = renderState.ageInTicks / (float) RAINBOW_CYCLE_TICKS;
        int baseFrame = Mth.floor(cyclePosition);
        float cycleProgress = cyclePosition - baseFrame;
        int phaseOffset = Mth.floor((float) ((renderState.x + renderState.z) * 2.0D));
        int textureIndex = Math.floorMod(baseFrame + phaseOffset, RAINBOW_TEXTURES.length);
        int nextTextureIndex = (textureIndex + 1) % RAINBOW_TEXTURES.length;
        return new RainbowFrame(RAINBOW_TEXTURES[textureIndex], RAINBOW_TEXTURES[nextTextureIndex], cycleProgress);
    }

    private record RainbowFrame(ResourceLocation currentTexture, ResourceLocation nextTexture, float blend) {
    }
}
