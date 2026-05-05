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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.monster.creeper.CreeperModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.world.entity.EntityType;

public class HappyCreeperClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncColorVariantPacket.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    if (context.client().level != null
                            && context.client().level.getEntity(payload.entityId()) instanceof net.minecraft.world.entity.monster.Creeper creeper) {
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
                registrationHelper.register(new CreeperVariantTextureLayer(creeperRenderer));
                registrationHelper.register(new CreeperHelmetLayer(creeperRenderer, context.getModelSet(), context.getEquipmentRenderer()));
                registrationHelper.register(new CreeperChestplateLayer(creeperRenderer, context.getModelSet(), context.getEquipmentRenderer()));
            } else if (entityType == EntityType.PLAYER) {
                @SuppressWarnings("unchecked")
                RenderLayerParent<AvatarRenderState, PlayerModel> playerRenderer =
                        (RenderLayerParent<AvatarRenderState, PlayerModel>) entityRenderer;
                registrationHelper.register(new HappyCreeperMaskLayer(playerRenderer, context.getModelSet()));
            }
        });
    }
}
