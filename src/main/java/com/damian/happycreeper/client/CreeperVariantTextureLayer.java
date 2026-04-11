package com.damian.happycreeper.client;

import com.damian.happycreeper.TamedCreeperAppearance;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.DyeColor;

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
    private static final int RAINBOW_CYCLE_TICKS = 25;

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
            case TamedCreeperAppearance.RAINBOW_VARIANT -> HAPPY_TEXTURE;
            default -> null;
        };

        if (texture == null) {
            return;
        }

        int color = variant == TamedCreeperAppearance.RAINBOW_VARIANT ? getRainbowColor(creeper, partialTick) : FULL_COLOR;
        renderColoredCutoutModel(getParentModel(), texture, poseStack, bufferSource, packedLight, creeper, color);
    }

    private static int getRainbowColor(Creeper creeper, float partialTick) {
        int colorIndex = creeper.tickCount / RAINBOW_CYCLE_TICKS + creeper.getId();
        DyeColor[] colors = DyeColor.values();
        int currentIndex = colorIndex % colors.length;
        int nextIndex = (colorIndex + 1) % colors.length;
        float blend = ((float) (creeper.tickCount % RAINBOW_CYCLE_TICKS) + partialTick) / (float) RAINBOW_CYCLE_TICKS;

        int currentColor = FastColor.ARGB32.opaque(colors[currentIndex].getTextureDiffuseColor());
        int nextColor = FastColor.ARGB32.opaque(colors[nextIndex].getTextureDiffuseColor());
        return FastColor.ARGB32.lerp(blend, currentColor, nextColor);
    }
}
