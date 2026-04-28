package com.damian.happycreeper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperFuelHandler {
    private static final String HEAL_COOLDOWN_TAG = "HappyCreeperFuelHealCooldown";
    private static final int SELF_HEAL_COOLDOWN_TICKS = 40;

    private TamedCreeperFuelHandler() {}

    public static void tick(Creeper creeper) {
        tickCooldown(creeper);
        if (CreeperState.get(creeper) != CreeperState.TAMED) return;
        float maxHealth = creeper.getMaxHealth();
        float currentHealth = creeper.getHealth();
        if (maxHealth <= 0.0F || currentHealth >= maxHealth * 0.5F || currentHealth >= maxHealth) return;
        if (getCooldown(creeper) > 0) return;
        float healAmount = CreeperFuelStorage.consumeFuelAndGetHealAmount(creeper);
        if (healAmount <= 0.0F) return;
        creeper.heal(healAmount);
        creeper.setPersistenceRequired();
        setCooldown(creeper, SELF_HEAL_COOLDOWN_TICKS);
        spawnHealParticles(creeper);
    }

    private static void tickCooldown(Creeper creeper) {
        int cooldown = getCooldown(creeper);
        if (cooldown > 0) setCooldown(creeper, cooldown - 1);
    }

    private static int getCooldown(Creeper creeper) {
        return IPersistentDataProvider.of(creeper).getInt(HEAL_COOLDOWN_TAG);
    }

    private static void setCooldown(Creeper creeper, int cooldown) {
        if (cooldown <= 0) {
            IPersistentDataProvider.of(creeper).remove(HEAL_COOLDOWN_TAG);
            return;
        }
        IPersistentDataProvider.of(creeper).putInt(HEAL_COOLDOWN_TAG, cooldown);
    }

    private static void spawnHealParticles(Creeper creeper) {
        if (creeper.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    creeper.getX(), creeper.getY() + 1.0D, creeper.getZ(),
                    3, 0.2D, 0.3D, 0.2D, 0.01D);
        }
    }
}
