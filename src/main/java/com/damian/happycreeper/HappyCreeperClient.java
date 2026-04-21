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
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = HappyCreeper.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = HappyCreeper.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class HappyCreeperClient {
    @SubscribeEvent
    static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CreeperChestplateModel.LAYER_LOCATION, CreeperChestplateModel::createBodyLayer);
        event.registerLayerDefinition(CreeperHelmetModel.LAYER_LOCATION, CreeperHelmetModel::createBodyLayer);
    }

    @SubscribeEvent
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

    @SubscribeEvent
    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(HappyCreeper.CREEPER_MENU.get(), CreeperScreen::new);
    }
}
