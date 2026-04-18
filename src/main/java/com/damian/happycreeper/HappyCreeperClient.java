package com.damian.happycreeper;

import com.damian.happycreeper.client.CreeperChestplateLayer;
import com.damian.happycreeper.client.CreeperChestplateModel;
import com.damian.happycreeper.client.CreeperHelmetLayer;
import com.damian.happycreeper.client.CreeperHelmetModel;
import com.damian.happycreeper.client.CreeperScreen;
import com.damian.happycreeper.client.CreeperVariantTextureLayer;
import com.damian.happycreeper.client.HappyCreeperRenderStateKeys;
import com.damian.happycreeper.client.HappyCreeperMaskLayer;

import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@Mod(value = HappyCreeper.MODID, dist = Dist.CLIENT)
public class HappyCreeperClient {
    public HappyCreeperClient(IEventBus modEventBus) {
        modEventBus.addListener(HappyCreeperClient::onRegisterLayerDefinitions);
        modEventBus.addListener(HappyCreeperClient::onAddLayers);
        modEventBus.addListener(HappyCreeperClient::onRegisterMenuScreens);
        modEventBus.addListener(HappyCreeperClient::onRegisterRenderStateModifiers);
    }

    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CreeperChestplateModel.LAYER_LOCATION, CreeperChestplateModel::createBodyLayer);
        event.registerLayerDefinition(CreeperHelmetModel.LAYER_LOCATION, CreeperHelmetModel::createBodyLayer);
    }

    static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        CreeperRenderer creeperRenderer = event.getRenderer(EntityType.CREEPER);
        if (creeperRenderer != null) {
            creeperRenderer.addLayer(new CreeperVariantTextureLayer(creeperRenderer));
            creeperRenderer.addLayer(new CreeperHelmetLayer(creeperRenderer, event.getEntityModels(), event.getContext().getEquipmentRenderer()));
            creeperRenderer.addLayer(new CreeperChestplateLayer(creeperRenderer, event.getEntityModels(), event.getContext().getEquipmentRenderer()));
        }

        for (PlayerSkin.Model skinModel : event.getSkins()) {
            PlayerRenderer playerRenderer = event.getSkin(skinModel);
            if (playerRenderer != null) {
                playerRenderer.addLayer(new HappyCreeperMaskLayer(playerRenderer, event.getEntityModels()));
            }
        }
    }

    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(HappyCreeper.CREEPER_MENU.get(), CreeperScreen::new);
    }

    static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(CreeperRenderer.class, (Creeper creeper, CreeperRenderState renderState) -> {
            renderState.setRenderData(HappyCreeperRenderStateKeys.CREEPER_VARIANT, TamedCreeperAppearance.getVariant(creeper));
            renderState.setRenderData(HappyCreeperRenderStateKeys.CREEPER_HELMET, creeper.getItemBySlot(EquipmentSlot.HEAD).copy());
            renderState.setRenderData(HappyCreeperRenderStateKeys.CREEPER_CHESTPLATE, creeper.getItemBySlot(EquipmentSlot.CHEST).copy());
        });

        event.registerEntityModifier(PlayerRenderer.class, (Player player, net.minecraft.client.renderer.entity.state.PlayerRenderState renderState) -> {
            ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD).copy();
            renderState.setRenderData(HappyCreeperRenderStateKeys.PLAYER_HEAD_ITEM, headStack);
            if (headStack.is(HappyCreeper.FAKE_HAPPY_CREEPER_HEAD.get())) {
                renderState.headItem.clear();
            }
        });
    }
}
