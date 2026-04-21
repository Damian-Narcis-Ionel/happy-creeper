package com.damian.happycreeper.mixin;

import com.damian.happycreeper.TamedCreeperAppearance;
import com.damian.happycreeper.client.HappyCreeperCreeperRenderState;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperRenderer.class)
abstract class CreeperRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/Creeper;Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;F)V", at = @At("TAIL"))
    private void happycreeper$extractRenderState(Creeper creeper, CreeperRenderState renderState, float partialTick, CallbackInfo ci) {
        HappyCreeperCreeperRenderState happyState = (HappyCreeperCreeperRenderState) renderState;
        happyState.happycreeper$setVariant(TamedCreeperAppearance.getVariant(creeper));
        happyState.happycreeper$setHelmet(creeper.getItemBySlot(EquipmentSlot.HEAD).copy());
        happyState.happycreeper$setChestplate(creeper.getItemBySlot(EquipmentSlot.CHEST).copy());
    }
}
