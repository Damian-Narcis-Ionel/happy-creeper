package com.damian.happycreeper.mixin;

import com.damian.happycreeper.CreeperState;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
abstract class MonsterMixin {
    @Inject(method = "isPreventingPlayerRest", at = @At("HEAD"), cancellable = true)
    private void happycreeper$allowTamedCreepersToNotBlockSleep(Player player, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof Creeper creeper && CreeperState.get(creeper) == CreeperState.TAMED) {
            cir.setReturnValue(false);
        }
    }
}
