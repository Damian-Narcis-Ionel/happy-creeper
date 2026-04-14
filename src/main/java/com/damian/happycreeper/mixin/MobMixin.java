package com.damian.happycreeper.mixin;

import com.damian.happycreeper.CreeperState;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobMixin {
    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void happycreeper$allowTamedCreepersToBeLeashed(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Creeper creeper && CreeperState.get(creeper) == CreeperState.TAMED) {
            cir.setReturnValue(true);
        }
    }
}
