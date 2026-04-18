package com.damian.happycreeper.mixin;

import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Leashable.class)
interface LeashableMixin {
    @Inject(method = "getLeashOffset()Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void happycreeper$adjustCreeperLeashOffset(CallbackInfoReturnable<Vec3> cir) {
        if ((Object) this instanceof Creeper creeper) {
            cir.setReturnValue(new Vec3(0.0D, creeper.getBbHeight() * 0.7D, creeper.getBbWidth() * 0.15D));
        }
    }
}
