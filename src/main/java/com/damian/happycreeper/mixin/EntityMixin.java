package com.damian.happycreeper.mixin;

import com.damian.happycreeper.TamedCreeperAppearance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class EntityMixin {
    @Inject(method = "startSeenByPlayer", at = @At("RETURN"))
    private void happycreeper$onStartSeenByPlayer(ServerPlayer player, CallbackInfo ci) {
        if ((Object) this instanceof Creeper creeper) {
            TamedCreeperAppearance.syncToPlayer(creeper, player);
        }
    }

    @Inject(method = "getRopeHoldPosition", at = @At("HEAD"), cancellable = true)
    private void happycreeper$adjustCreeperLeashOffset(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if ((Object) this instanceof Creeper creeper) {
            cir.setReturnValue(new Vec3(0.0D, creeper.getBbHeight() * 0.7D, creeper.getBbWidth() * 0.15D));
        }
    }
}
