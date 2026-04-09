package com.damian.happycreeper;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class CreeperHeadProtectionHandler {
    private static final String REVENGE_TARGET_TAG = "HappyCreeperRevengeTarget";
    private static final String REVENGE_TICKS_TAG = "HappyCreeperRevengeTicks";
    private static final int REVENGE_DURATION_TICKS = 20 * 10;

    private CreeperHeadProtectionHandler() {
    }

    @SubscribeEvent
    public static void onCreeperTargetChange(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (isProtectedPlayer(newTarget) && !isRevengeTarget(creeper, newTarget)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        tickRevengeTimer(creeper);

        if (!isProtectedPlayer(creeper.getTarget()) || isRevengeTarget(creeper, creeper.getTarget())) {
            return;
        }

        creeper.setTarget(null);
        creeper.setSwellDir(-1);
    }

    @SubscribeEvent
    public static void onCreeperDamaged(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        Player attacker = getResponsiblePlayer(event.getSource());
        if (attacker == null) {
            return;
        }

        setRevengeTarget(creeper, attacker);
        creeper.setTarget(attacker);
    }

    private static boolean isProtectedPlayer(LivingEntity entity) {
        return entity instanceof Player player
                && player.getItemBySlot(EquipmentSlot.HEAD).is(Items.CREEPER_HEAD);
    }

    private static Player getResponsiblePlayer(DamageSource source) {
        Entity sourceEntity = source.getEntity();
        if (sourceEntity instanceof Player player) {
            return player;
        }

        Entity directEntity = source.getDirectEntity();
        if (directEntity instanceof Projectile projectile && projectile.getOwner() instanceof Player player) {
            return player;
        }

        return null;
    }

    private static void setRevengeTarget(Creeper creeper, Player player) {
        CompoundTag data = creeper.getPersistentData();
        data.putUUID(REVENGE_TARGET_TAG, player.getUUID());
        data.putInt(REVENGE_TICKS_TAG, REVENGE_DURATION_TICKS);
    }

    private static boolean isRevengeTarget(Creeper creeper, LivingEntity target) {
        if (!(target instanceof Player player)) {
            return false;
        }

        CompoundTag data = creeper.getPersistentData();
        if (!data.hasUUID(REVENGE_TARGET_TAG) || data.getInt(REVENGE_TICKS_TAG) <= 0) {
            return false;
        }

        UUID revengeTarget = data.getUUID(REVENGE_TARGET_TAG);
        return revengeTarget.equals(player.getUUID());
    }

    private static void tickRevengeTimer(Creeper creeper) {
        CompoundTag data = creeper.getPersistentData();
        int remainingTicks = data.getInt(REVENGE_TICKS_TAG);
        if (remainingTicks <= 0) {
            clearRevengeTarget(data);
            return;
        }

        data.putInt(REVENGE_TICKS_TAG, remainingTicks - 1);
        if (remainingTicks - 1 <= 0) {
            clearRevengeTarget(data);
        }
    }

    private static void clearRevengeTarget(CompoundTag data) {
        data.remove(REVENGE_TARGET_TAG);
        data.remove(REVENGE_TICKS_TAG);
    }
}
