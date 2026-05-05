package com.damian.happycreeper;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperAbilityHandler {
    private static final Identifier SWIM_SPEED_BONUS_ID = Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "swim_speed_bonus");
    private static final double SWIM_SPEED_BONUS = 0.4D;
    private static final int FIRE_RESISTANCE_REFRESH_THRESHOLD = 40;
    private static final int FIRE_RESISTANCE_DURATION = 200;

    private TamedCreeperAbilityHandler() {}

    public static void tick(Creeper creeper) {
        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            removeSwimSpeedModifier(creeper);
            return;
        }
        tickFireResistance(creeper);
        tickSwimSpeed(creeper);
    }

    private static void tickFireResistance(Creeper creeper) {
        if (!CreeperAbilityStorage.hasAbility(creeper, CreeperAbility.FIRE_RESISTANCE)) return;
        MobEffectInstance existing = creeper.getEffect(MobEffects.FIRE_RESISTANCE);
        if (existing != null && existing.getDuration() > FIRE_RESISTANCE_REFRESH_THRESHOLD) return;
        creeper.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_DURATION, 0, false, false, true));
    }

    private static void tickSwimSpeed(Creeper creeper) {
        AttributeInstance movementSpeed = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) return;
        boolean hasAbility = CreeperAbilityStorage.hasAbility(creeper, CreeperAbility.SWIM_SPEED);
        boolean inWater = creeper.isInWater();
        if (hasAbility && inWater) {
            if (!movementSpeed.hasModifier(SWIM_SPEED_BONUS_ID)) {
                movementSpeed.addOrReplacePermanentModifier(new AttributeModifier(SWIM_SPEED_BONUS_ID, SWIM_SPEED_BONUS, Operation.ADD_VALUE));
            }
            creeper.setAirSupply(creeper.getMaxAirSupply());
        } else {
            movementSpeed.removeModifier(SWIM_SPEED_BONUS_ID);
        }
    }

    private static void removeSwimSpeedModifier(Creeper creeper) {
        AttributeInstance movementSpeed = creeper.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed != null) movementSpeed.removeModifier(SWIM_SPEED_BONUS_ID);
    }
}
