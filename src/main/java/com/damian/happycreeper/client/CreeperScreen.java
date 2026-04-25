package com.damian.happycreeper.client;

import com.damian.happycreeper.CreeperAbility;
import com.damian.happycreeper.HappyCreeper;
import com.damian.happycreeper.menu.CreeperMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CreeperScreen extends AbstractContainerScreen<CreeperMenu> {
    private static final int BACKGROUND_WIDTH = 176;
    private static final int BACKGROUND_HEIGHT = 166;
    private static final int INVENTORY_SECTION_Y = 83;
    private static final int INVENTORY_SECTION_HEIGHT = 83;
    private static final int TOP_PANEL_X = 7;
    private static final int TOP_PANEL_Y = 17;
    private static final int TOP_PANEL_WIDTH = 162;
    private static final int TOP_PANEL_HEIGHT = 58;
    private static final int PREVIEW_LEFT_X = 53;
    private static final int PREVIEW_TOP_Y = 21;
    private static final int PREVIEW_RIGHT_X = 86;
    private static final int PREVIEW_BOTTOM_Y = 75;
    private static final int PREVIEW_SIZE = 20;
    private static final int ARMOR_SLOT_X = 8;
    private static final int HELMET_SLOT_Y = 19;
    private static final int CHESTPLATE_SLOT_Y = 41;
    private static final int TITLE_Y = 6;
    private static final int HEALTH_VALUE_Y = 63;
    private static final int HEART_ICON_X = 10;
    private static final int HEART_ICON_Y = 63;
    private static final int HEART_ICON_SIZE = 9;
    private static final int HEALTH_TEXT_X = 22;
    private static final int FUEL_SLOT_X = 30;
    private static final int FUEL_SLOT_Y = 41;
    private static final int FUEL_LABEL_X = 30;
    private static final int FUEL_LABEL_Y = 31;
    private static final int BUTTON_X = 88;
    private static final int BUTTON_Y = 46;
    private static final int BUTTON_WIDTH = 72;
    private static final int BUTTON_HEIGHT = 18;
    private static final ResourceLocation INVENTORY_TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot");
    private static final ResourceLocation HEART_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation EFFECT_BACKGROUND_SMALL_SPRITE = ResourceLocation.withDefaultNamespace("container/inventory/effect_background_small");
    private static final int EFFECT_X = 177;
    private static final int EFFECT_Y = 18;
    private static final int EFFECT_SPACING = 33;
    private static final int ABILITY_ICON_START_X = 90;
    private static final int ABILITY_ICON_Y = 20;
    private static final int ABILITY_ICON_SPACING = 20;
    private Button commandButton;

    public CreeperScreen(CreeperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = BACKGROUND_WIDTH;
        imageHeight = BACKGROUND_HEIGHT;
        inventoryLabelY = 75;
        inventoryLabelX = 8;
    }

    @Override
    protected void init() {
        super.init();
        commandButton = Button.builder(getCommandLabel(), button -> {
            if (minecraft != null && minecraft.gameMode != null) {
                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            }
        }).bounds(leftPos + BUTTON_X, topPos + BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        addRenderableWidget(commandButton);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderAbilityIcons(guiGraphics);
        renderEffectTooltips(guiGraphics, mouseX, mouseY);
        renderAbilityTooltips(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        commandButton.setMessage(getCommandLabel());
        Creeper creeper = menu.getCreeper();
        Component screenTitle = creeper != null && creeper.hasCustomName() ? creeper.getDisplayName() : title;
        guiGraphics.drawString(font, screenTitle, 8, TITLE_Y, 0x404040, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0x404040, false);

        if (creeper == null) {
            return;
        }

        guiGraphics.blitSprite(RenderType::guiTextured, HEART_SPRITE, HEART_ICON_X, HEART_ICON_Y, HEART_ICON_SIZE, HEART_ICON_SIZE);
        guiGraphics.drawString(font,
                Component.translatable("screen.happycreeper.creeper.health", menu.getDisplayedHealth(), menu.getDisplayedMaxHealth()),
                HEALTH_TEXT_X,
                HEALTH_VALUE_Y,
                0x404040,
                false);
        guiGraphics.drawString(font, Component.translatable("screen.happycreeper.creeper.fuel"), FUEL_LABEL_X, FUEL_LABEL_Y, 0x404040, false);
    }

    private Component getCommandLabel() {
        return Component.translatable(menu.isStaying()
                ? "screen.happycreeper.creeper.command.staying"
                : "screen.happycreeper.creeper.command.following");
    }

    private static void renderWindow(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xFF373737);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFFF8F8F8);
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFFC6C6C6);
        guiGraphics.fill(x + 1, y + height - 1, x + width, y + height, 0xFF555555);
        guiGraphics.fill(x + width - 1, y + 1, x + width, y + height, 0xFF555555);
    }

    private static void renderInsetPanel(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        guiGraphics.fill(x, y, x + width, y + height, 0xFF8B8B8B);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF373737);
        guiGraphics.fill(x + 1, y + 1, x + width - 2, y + height - 2, 0xFF8B8B8B);
        guiGraphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, 0xFFEAEAEA);
    }

    private static void renderSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blitSprite(RenderType::guiTextured, SLOT_SPRITE, x, y, 18, 18);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        renderWindow(guiGraphics, left, top, imageWidth, imageHeight);
        renderInsetPanel(guiGraphics, left + TOP_PANEL_X, top + TOP_PANEL_Y, TOP_PANEL_WIDTH, TOP_PANEL_HEIGHT);
        renderSlot(guiGraphics, left + ARMOR_SLOT_X, top + HELMET_SLOT_Y);
        renderSlot(guiGraphics, left + ARMOR_SLOT_X, top + CHESTPLATE_SLOT_Y);
        renderSlot(guiGraphics, left + FUEL_SLOT_X, top + FUEL_SLOT_Y);
        guiGraphics.blit(RenderType::guiTextured, INVENTORY_TEXTURE, left, top + INVENTORY_SECTION_Y, 0.0F, (float) INVENTORY_SECTION_Y, imageWidth, INVENTORY_SECTION_HEIGHT, 256, 256);
        renderEffects(guiGraphics, left, top);

        Creeper creeper = menu.getCreeper();
        if (creeper != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    left + PREVIEW_LEFT_X,
                    top + PREVIEW_TOP_Y,
                    left + PREVIEW_RIGHT_X,
                    top + PREVIEW_BOTTOM_Y,
                    PREVIEW_SIZE,
                    0.0625F,
                    mouseX,
                    mouseY,
                    creeper);
        }
    }

    private void renderEffects(GuiGraphics guiGraphics, int left, int top) {
        Creeper creeper = menu.getCreeper();
        if (creeper == null || creeper.getActiveEffects().isEmpty() || minecraft == null) {
            return;
        }

        List<MobEffectInstance> effects = getSortedEffects(creeper);
        int x = left + EFFECT_X;
        int y = top + EFFECT_Y;
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance effect = effects.get(i);
            guiGraphics.blitSprite(RenderType::guiTextured, EFFECT_BACKGROUND_SMALL_SPRITE, x, y + i * EFFECT_SPACING, 32, 32);
            TextureAtlasSprite sprite = minecraft.getMobEffectTextures().get(effect.getEffect());
            guiGraphics.blitSprite(RenderType::guiTextured, sprite, x + 7, y + 7 + i * EFFECT_SPACING, 18, 18);
        }
    }

    private void renderEffectTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Creeper creeper = menu.getCreeper();
        if (creeper == null || creeper.getActiveEffects().isEmpty() || minecraft == null) {
            return;
        }

        List<MobEffectInstance> effects = getSortedEffects(creeper);
        int x = leftPos + EFFECT_X;
        int y = topPos + EFFECT_Y;
        for (int i = 0; i < effects.size(); i++) {
            int effectY = y + i * EFFECT_SPACING;
            if (mouseX >= x && mouseX < x + 32 && mouseY >= effectY && mouseY < effectY + 32) {
                MobEffectInstance effect = effects.get(i);
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable(effect.getDescriptionId()));
                guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                return;
            }
        }
    }

    private static List<MobEffectInstance> getSortedEffects(Creeper creeper) {
        List<MobEffectInstance> effects = new ArrayList<>(creeper.getActiveEffects());
        effects.sort(Comparator.comparing(effect -> Component.translatable(effect.getDescriptionId()).getString()));
        return effects;
    }

    private void renderAbilityIcons(GuiGraphics guiGraphics) {
        int slot = 0;
        for (CreeperAbility ability : CreeperAbility.values()) {
            if (!menu.hasAbility(ability)) {
                continue;
            }
            int x = leftPos + ABILITY_ICON_START_X + slot * ABILITY_ICON_SPACING;
            int y = topPos + ABILITY_ICON_Y;
            guiGraphics.renderItem(getAbilityIcon(ability), x, y);
            slot++;
        }
    }

    private void renderAbilityTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int slot = 0;
        for (CreeperAbility ability : CreeperAbility.values()) {
            if (!menu.hasAbility(ability)) {
                continue;
            }
            int x = leftPos + ABILITY_ICON_START_X + slot * ABILITY_ICON_SPACING;
            int y = topPos + ABILITY_ICON_Y;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                guiGraphics.renderTooltip(font, Component.translatable(getAbilityTooltipKey(ability)), mouseX, mouseY);
                return;
            }
            slot++;
        }
    }

    private static ItemStack getAbilityIcon(CreeperAbility ability) {
        return switch (ability) {
            case FIRE_RESISTANCE -> new ItemStack(HappyCreeper.LAVA_BISCUIT.get());
            case SWIM_SPEED -> new ItemStack(HappyCreeper.FISH_BISCUIT.get());
            case EXTREME_BLAST -> new ItemStack(HappyCreeper.EXTREME_BLAST_BISCUIT.get());
            case SLIME_JUMP -> new ItemStack(HappyCreeper.SLIME_BISCUIT.get());
        };
    }

    private static String getAbilityTooltipKey(CreeperAbility ability) {
        return switch (ability) {
            case FIRE_RESISTANCE -> "ability.happycreeper.fire_resistance";
            case SWIM_SPEED -> "ability.happycreeper.swim_speed";
            case EXTREME_BLAST -> "ability.happycreeper.extreme_blast";
            case SLIME_JUMP -> "ability.happycreeper.slime_jump";
        };
    }
}
