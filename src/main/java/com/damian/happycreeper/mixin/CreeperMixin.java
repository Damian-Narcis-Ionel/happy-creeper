package com.damian.happycreeper.mixin;

import com.damian.happycreeper.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
abstract class CreeperMixin implements IPersistentDataProvider {
    @Unique
    private CompoundTag happycreeper$extraData;

    @Override
    public CompoundTag getPersistentData() {
        if (happycreeper$extraData == null) {
            happycreeper$extraData = new CompoundTag();
        }
        return happycreeper$extraData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void happycreeper$saveExtraData(ValueOutput output, CallbackInfo ci) {
        if (happycreeper$extraData != null && !happycreeper$extraData.isEmpty()) {
            output.store("HappyCreeperExtraData", CompoundTag.CODEC, happycreeper$extraData);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void happycreeper$loadExtraData(ValueInput input, CallbackInfo ci) {
        happycreeper$extraData = input.read("HappyCreeperExtraData", CompoundTag.CODEC).orElse(null);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void happycreeper$onTick(CallbackInfo ci) {
        Creeper creeper = (Creeper)(Object)this;
        if (creeper.level().isClientSide()) return;
        TamedCreeperAppearance.ensureTamedAppearance(creeper);
        CreeperPersistenceHandler.tick(creeper);
        TamedCreeperAttributeHandler.tick(creeper);
        TamedCreeperAbilityHandler.tick(creeper);
        TamedCreeperFollowHandler.tick(creeper);
        TamedCreeperBehaviorHandler.tick(creeper);
        TamedCreeperCombatHandler.tick(creeper);
        TamedCreeperFuelHandler.tick(creeper);
        WeakenedCreeperExplosionHandler.tick(creeper);
        CreeperHeadProtectionHandler.tick(creeper);
    }
}
