package com.damian.happycreeper.client;

import com.damian.happycreeper.menu.CreeperMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Inventory;

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
    private static final int LABEL_COLOR = 0xFF404040;
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
        renderEffectTooltips(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        commandButton.setMessage(getCommandLabel());
        Creeper creeper = menu.getCreeper();
        Component screenTitle = creeper != null && creeper.hasCustomName() ? creeper.getDisplayName() : title;
        guiGraphics.drawString(font, screenTitle, 8, TITLE_Y, LABEL_COLOR, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, LABEL_COLOR, false);

        if (creeper == null) {
            return;
        }

        String healthText = menu.getDisplayedHealth() + "/" + menu.getDisplayedMaxHealth();
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEART_SPRITE, HEART_ICON_X, HEART_ICON_Y, HEART_ICON_SIZE, HEART_ICON_SIZE);
        guiGraphics.drawString(font, healthText, HEALTH_TEXT_X, HEALTH_VALUE_Y, LABEL_COLOR, false);
        guiGraphics.drawString(font, Component.translatable("screen.happycreeper.creeper.fuel"), FUEL_LABEL_X, FUEL_LABEL_Y, LABEL_COLOR, false);
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
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_SPRITE, x, y, 18, 18);
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
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_TEXTURE, left, top + INVENTORY_SECTION_Y, 0.0F, (float) INVENTORY_SECTION_Y, imageWidth, INVENTORY_SECTION_HEIGHT, 256, 256);
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
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, EFFECT_BACKGROUND_SMALL_SPRITE, x, y + i * EFFECT_SPACING, 32, 32);
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()), x + 7, y + 7 + i * EFFECT_SPACING, 18, 18);
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
                guiGraphics.setTooltipForNextFrame(font, tooltip, Optional.empty(), mouseX, mouseY);
                return;
            }
        }
    }

    private static List<MobEffectInstance> getSortedEffects(Creeper creeper) {
        List<MobEffectInstance> effects = new ArrayList<>(creeper.getActiveEffects());
        effects.sort(Comparator.comparing(effect -> Component.translatable(effect.getDescriptionId()).getString()));
        return effects;
    }
}
