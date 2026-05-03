package com.damian.happycreeper;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

public final class CreeperHeadProtectionHandler {
    private static final String REVENGE_TARGET_TAG = "HappyCreeperRevengeTarget";
    private static final String REVENGE_TICKS_TAG = "HappyCreeperRevengeTicks";
    private static final int REVENGE_DURATION_TICKS = 20 * 10;

    private CreeperHeadProtectionHandler() {}

    public static boolean shouldBlockTarget(Creeper creeper, LivingEntity target) {
        return isProtectedPlayer(target) && !isRevengeTarget(creeper, target);
    }

    public static void tick(Creeper creeper) {
        tickRevengeTimer(creeper);
        if (!isProtectedPlayer(creeper.getTarget()) || isRevengeTarget(creeper, creeper.getTarget())) return;
        creeper.setTarget(null);
        creeper.setSwellDir(-1);
    }

    public static void onIncomingDamage(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof Creeper creeper)) return;
        Player attacker = getResponsiblePlayer(source);
        if (attacker == null) return;
        setRevengeTarget(creeper, attacker);
        creeper.setTarget(attacker);
    }

    private static boolean isProtectedPlayer(LivingEntity entity) {
        return entity instanceof Player player
                && HappyCreeper.isCreeperDisguise(player.getItemBySlot(EquipmentSlot.HEAD));
    }

    private static Player getResponsiblePlayer(DamageSource source) {
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player) return player;
        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof Projectile projectile && projectile.getOwner() instanceof Player player) return player;
        return null;
    }

    private static void setRevengeTarget(Creeper creeper, Player player) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        data.store(REVENGE_TARGET_TAG, UUIDUtil.CODEC, player.getUUID());
        data.putInt(REVENGE_TICKS_TAG, REVENGE_DURATION_TICKS);
    }

    private static boolean isRevengeTarget(Creeper creeper, LivingEntity target) {
        if (!(target instanceof Player player)) return false;
        CompoundTag data = IPersistentDataProvider.of(creeper);
        if (data.getIntOr(REVENGE_TICKS_TAG, 0) <= 0) return false;
        return data.read(REVENGE_TARGET_TAG, UUIDUtil.CODEC)
                .map(revengeTarget -> revengeTarget.equals(player.getUUID()))
                .orElse(false);
    }

    private static void tickRevengeTimer(Creeper creeper) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        int remainingTicks = data.getIntOr(REVENGE_TICKS_TAG, 0);
        if (remainingTicks <= 0) { clearRevengeTarget(data); return; }
        data.putInt(REVENGE_TICKS_TAG, remainingTicks - 1);
        if (remainingTicks - 1 <= 0) clearRevengeTarget(data);
    }

    private static void clearRevengeTarget(CompoundTag data) {
        data.remove(REVENGE_TARGET_TAG);
        data.remove(REVENGE_TICKS_TAG);
    }
}
