package com.damian.happycreeper;

import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class WeakenedCreeperExplosionHandler {
    private static final String FIZZLE_COOLDOWN_TAG = "HappyCreeperFizzleCooldown";
    private static final int FIZZLE_COOLDOWN_TICKS = 40;

    private WeakenedCreeperExplosionHandler() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        CreeperState state = CreeperState.get(creeper);
        if (state == CreeperState.NORMAL) {
            return;
        }

        if (state == CreeperState.WEAKENED) {
            spawnIdleWeakenedParticles(creeper);
        }

        CompoundTag data = creeper.getPersistentData();
        int cooldown = data.getIntOr(FIZZLE_COOLDOWN_TAG, 0);
        if (cooldown > 0) {
            data.putInt(FIZZLE_COOLDOWN_TAG, cooldown - 1);
            creeper.setSwellDir(-1);
            return;
        }

        if (creeper.getSwellDir() <= 0) {
            return;
        }

        data.putInt(FIZZLE_COOLDOWN_TAG, FIZZLE_COOLDOWN_TICKS);
        creeper.setSwellDir(-1);
        creeper.setTarget(null);
        spawnFizzleEffects(creeper);
    }

    private static void spawnFizzleEffects(Creeper creeper) {
        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(ParticleTypes.SMOKE,
                creeper.getX(),
                creeper.getY() + 1.0D,
                creeper.getZ(),
                16,
                0.25D,
                0.35D,
                0.25D,
                0.01D);
        serverLevel.sendParticles(ParticleTypes.POOF,
                creeper.getX(),
                creeper.getY() + 0.8D,
                creeper.getZ(),
                8,
                0.2D,
                0.2D,
                0.2D,
                0.02D);
        serverLevel.playSound(null,
                creeper.blockPosition(),
                SoundEvents.FIRE_EXTINGUISH,
                SoundSource.HOSTILE,
                0.8F,
                0.8F + creeper.getRandom().nextFloat() * 0.2F);
    }

    private static void spawnIdleWeakenedParticles(Creeper creeper) {
        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (creeper.tickCount % 10 != 0) {
            return;
        }

        double red = 0.65D;
        double green = 0.78D;
        double blue = 0.55D;
        serverLevel.sendParticles(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float) red, (float) green, (float) blue),
                creeper.getX(),
                creeper.getY() + 1.0D,
                creeper.getZ(),
                5,
                red,
                green,
                blue,
                1.0D);
    }
}
