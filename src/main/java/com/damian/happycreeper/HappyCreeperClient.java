package com.damian.happycreeper;

import com.damian.happycreeper.client.CreeperVariantTextureLayer;
import com.damian.happycreeper.client.HappyCreeperMaskLayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = HappyCreeper.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = HappyCreeper.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class HappyCreeperClient {
    public HappyCreeperClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        HappyCreeper.LOGGER.info("HELLO FROM CLIENT SETUP");
        HappyCreeper.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        CreeperRenderer creeperRenderer = event.getRenderer(EntityType.CREEPER);
        if (creeperRenderer != null) {
            creeperRenderer.addLayer(new CreeperVariantTextureLayer(creeperRenderer));
        }

        for (PlayerSkin.Model skinModel : event.getSkins()) {
            PlayerRenderer playerRenderer = event.getSkin(skinModel);
            if (playerRenderer != null) {
                playerRenderer.addLayer(new HappyCreeperMaskLayer(playerRenderer, event.getEntityModels()));
            }
        }
    }
}
