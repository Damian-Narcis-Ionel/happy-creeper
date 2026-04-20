package com.damian.happycreeper.mixin;

import com.damian.happycreeper.HappyCreeper;
import com.damian.happycreeper.client.HappyCreeperRenderStateKeys;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
abstract class AvatarRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
            at = @At("TAIL"))
    private void happycreeper$storeMaskState(Avatar avatar, AvatarRenderState renderState, float partialTick, CallbackInfo ci) {
        ItemStack headStack = avatar.getItemBySlot(EquipmentSlot.HEAD).copy();
        renderState.setRenderData(HappyCreeperRenderStateKeys.PLAYER_HEAD_ITEM, headStack);
        if (headStack.is(HappyCreeper.FAKE_HAPPY_CREEPER_HEAD.get())) {
            renderState.headItem.clear();
        }
    }
}
