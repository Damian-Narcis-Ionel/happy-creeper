package com.damian.happycreeper;

import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperLogHelper {
    private TamedCreeperLogHelper() {
    }

    public static void logDamage(Creeper creeper, DamageSource source, float amount) {
        TamedCreeperOwner.getOwner(creeper).ifPresent(owner -> owner.sendSystemMessage(Component.translatable(
                "message.happycreeper.damage_log",
                formatAmount(amount),
                describeDamageSource(source),
                formatAmount(creeper.getHealth()),
                formatAmount(creeper.getMaxHealth()))));
    }

    public static void logHealing(Creeper creeper, float amount) {
        TamedCreeperOwner.getOwner(creeper).ifPresent(owner -> owner.sendSystemMessage(Component.translatable(
                "message.happycreeper.heal_log",
                formatAmount(amount),
                formatAmount(creeper.getHealth()),
                formatAmount(creeper.getMaxHealth()))));
    }

    private static Component describeDamageSource(DamageSource source) {
        Entity causingEntity = source.getEntity();
        if (causingEntity != null) {
            return causingEntity.getDisplayName();
        }

        Entity directEntity = source.getDirectEntity();
        if (directEntity != null) {
            return directEntity.getDisplayName();
        }

        return Component.literal(prettyName(source.getMsgId()));
    }

    private static String prettyName(String raw) {
        String normalized = raw.replace('_', ' ');
        if (normalized.isEmpty()) {
            return "Unknown";
        }

        return normalized.substring(0, 1).toUpperCase(Locale.ROOT) + normalized.substring(1);
    }

    private static String formatAmount(float value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
