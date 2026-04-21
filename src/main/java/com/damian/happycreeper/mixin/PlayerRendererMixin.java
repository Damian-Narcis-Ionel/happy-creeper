package com.damian.happycreeper.mixin;

import com.damian.happycreeper.client.HappyCreeperPlayerRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
abstract class PlayerRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    private void happycreeper$extractRenderState(AbstractClientPlayer player, PlayerRenderState renderState, float partialTick, CallbackInfo ci) {
        ((HappyCreeperPlayerRenderState) renderState).happycreeper$setHeadEquipment(player.getItemBySlot(EquipmentSlot.HEAD).copy());
    }
}
