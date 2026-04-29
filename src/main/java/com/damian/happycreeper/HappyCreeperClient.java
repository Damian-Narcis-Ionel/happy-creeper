package com.damian.happycreeper;

import com.damian.happycreeper.client.CreeperChestplateLayer;
import com.damian.happycreeper.client.CreeperChestplateModel;
import com.damian.happycreeper.client.CreeperHelmetLayer;
import com.damian.happycreeper.client.CreeperHelmetModel;
import com.damian.happycreeper.client.CreeperScreen;
import com.damian.happycreeper.client.CreeperVariantTextureLayer;
import com.damian.happycreeper.client.HappyCreeperMaskLayer;
import com.damian.happycreeper.network.SyncColorVariantPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;

public class HappyCreeperClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncColorVariantPacket.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (context.client().level != null
                            && context.client().level.getEntity(payload.entityId()) instanceof Creeper creeper) {
                        IPersistentDataProvider.of(creeper).putInt(TamedCreeperAppearance.COLOR_VARIANT_TAG, payload.variant());
                    }
                }));

        MenuScreens.register(HappyCreeper.CREEPER_MENU, CreeperScreen::new);

        EntityModelLayerRegistry.registerModelLayer(CreeperHelmetModel.LAYER_LOCATION, CreeperHelmetModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(CreeperChestplateModel.LAYER_LOCATION, CreeperChestplateModel::createBodyLayer);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityType == EntityType.CREEPER) {
                @SuppressWarnings("unchecked")
                RenderLayerParent<CreeperRenderState, CreeperModel> creeperRenderer =
                        (RenderLayerParent<CreeperRenderState, CreeperModel>) entityRenderer;
                EquipmentLayerRenderer equipmentRenderer = new EquipmentLayerRenderer(
                        Minecraft.getInstance().getEquipmentModels(),
                        Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET));
                registrationHelper.register(new CreeperVariantTextureLayer(creeperRenderer));
                registrationHelper.register(new CreeperHelmetLayer(creeperRenderer, Minecraft.getInstance().getEntityModels(), equipmentRenderer));
                registrationHelper.register(new CreeperChestplateLayer(creeperRenderer, Minecraft.getInstance().getEntityModels(), equipmentRenderer));
            } else if (entityType == EntityType.PLAYER) {
                @SuppressWarnings("unchecked")
                RenderLayerParent<PlayerRenderState, PlayerModel> playerRenderer =
                        (RenderLayerParent<PlayerRenderState, PlayerModel>) entityRenderer;
                registrationHelper.register(new HappyCreeperMaskLayer(playerRenderer, Minecraft.getInstance().getEntityModels()));
            }
        });
    }
}
