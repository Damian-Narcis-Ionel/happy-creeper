package com.damian.happycreeper.client;

import java.util.Comparator;
import java.util.List;

import com.damian.happycreeper.TamedCreeperAppearance;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;

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
    public void render(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CreeperRenderState renderState,
            float yRot,
            float xRot) {
        Creeper creeper = findCreeperAt(renderState);
        if (creeper == null) return;

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
            renderRainbow(poseStack, bufferSource, packedLight, renderState, creeper, xRot);
            return;
        }

        if (texture == null) {
            return;
        }

        renderColoredCutoutModel(getParentModel(), texture, poseStack, bufferSource, packedLight, renderState, FULL_COLOR);
    }

    private void renderRainbow(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CreeperRenderState renderState,
            Creeper creeper,
            float partialTick) {
        RainbowFrame frame = getRainbowFrame(creeper, partialTick);
        renderTranslucentTexture(poseStack, bufferSource, packedLight, renderState, frame.currentTexture, FULL_COLOR);

        if (frame.blend > 0.0F) {
            int overlayColor = ARGB.color(Math.round(frame.blend * 255.0F), 255, 255, 255);
            renderTranslucentTexture(poseStack, bufferSource, packedLight, renderState, frame.nextTexture, overlayColor);
        }
    }

    private void renderTranslucentTexture(PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CreeperRenderState renderState,
            ResourceLocation texture,
            int color) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        getParentModel().renderToBuffer(poseStack,
                vertexConsumer,
                packedLight,
                LivingEntityRenderer.getOverlayCoords(renderState, 0.0F),
                color);
    }

    private static RainbowFrame getRainbowFrame(Creeper creeper, float partialTick) {
        float cycleProgress = (((float) (creeper.tickCount % RAINBOW_CYCLE_TICKS)) + partialTick) / (float) RAINBOW_CYCLE_TICKS;
        int textureIndex = (creeper.tickCount / RAINBOW_CYCLE_TICKS + creeper.getId()) % RAINBOW_TEXTURES.length;
        int nextTextureIndex = (textureIndex + 1) % RAINBOW_TEXTURES.length;
        return new RainbowFrame(RAINBOW_TEXTURES[textureIndex], RAINBOW_TEXTURES[nextTextureIndex], cycleProgress);
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

    private record RainbowFrame(ResourceLocation currentTexture, ResourceLocation nextTexture, float blend) {
    }
}
