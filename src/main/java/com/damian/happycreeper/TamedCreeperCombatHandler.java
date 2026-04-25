package com.damian.happycreeper;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperCombatHandler {
    private static final double DEFENSE_SCAN_RADIUS = 16.0D;
    private static final String BLAST_COOLDOWN_TAG = "HappyCreeperBlastCooldown";
    private static final String SLIME_JUMP_COOLDOWN_TAG = "HappyCreeperSlimeCooldown";
    private static final String SLIME_TARGET_ID_TAG = "HappyCreeperSlimeTargetId";
    private static final String SLIME_STICK_TICKS_TAG = "HappyCreeperSlimeStickTicks";
    private static final double COMBAT_CHASE_SPEED = 1.6D;
    private static final double BLAST_TRIGGER_DISTANCE_SQR = 9.0D;
    private static final double BLAST_RADIUS = 3.0D;
    private static final float BLAST_DAMAGE = 5.0F;
    private static final double BLAST_KNOCKBACK_STRENGTH = 1.15D;
    private static final int BLAST_COOLDOWN_TICKS = 20 * 3;
    private static final double DEATH_BLAST_RADIUS = 5.0D;
    private static final float DEATH_BLAST_DAMAGE = 10.0F;
    private static final double DEATH_BLAST_KNOCKBACK_STRENGTH = 1.8D;
    private static final int SLIME_JUMP_COOLDOWN_TICKS = 20 * 7;
    private static final int SLIME_STICK_DURATION_TICKS = 25;
    private static final double SLIME_JUMP_SPEED = 2.5D;
    private static final double SLIME_STICK_DISTANCE_SQR = 4.0D;
    private static final float SLIME_BLAST_MULTIPLIER = 4.0F;

    private TamedCreeperCombatHandler() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            return;
        }

        if (TamedCreeperCommandState.isStaying(creeper)) {
            return;
        }

        ServerPlayer owner = TamedCreeperOwner.getOwner(creeper).orElse(null);
        if (owner == null || !owner.isAlive() || owner.isSpectator()) {
            return;
        }

        CompoundTag data = creeper.getPersistentData();

        int cooldown = data.getIntOr(BLAST_COOLDOWN_TAG, 0);
        if (cooldown > 0) {
            data.putInt(BLAST_COOLDOWN_TAG, cooldown - 1);
        }

        int slimeCooldown = data.getIntOr(SLIME_JUMP_COOLDOWN_TAG, 0);
        if (slimeCooldown > 0) {
            data.putInt(SLIME_JUMP_COOLDOWN_TAG, slimeCooldown - 1);
        }

        if (tickActiveSlimeJump(creeper, owner, data)) {
            return;
        }

        LivingEntity currentTarget = creeper.getTarget();
        if (isValidThreatToOwner(currentTarget, owner)
                || isThreatToCreeper(creeper, currentTarget)
                || isOwnerAttackTarget(creeper, currentTarget, owner)) {
            if (tryStartSlimeJump(creeper, data, currentTarget)) {
                return;
            }

            if (cooldown <= 0 && isInBlastRange(creeper, currentTarget)) {
                performBlastShove(creeper, owner, currentTarget);
                data.putInt(BLAST_COOLDOWN_TAG, BLAST_COOLDOWN_TICKS);
            } else if (currentTarget != null && currentTarget.isAlive()) {
                creeper.getNavigation().moveTo(currentTarget, COMBAT_CHASE_SPEED);
            }
            return;
        }

        LivingEntity threat = findThreatToOwnerOrAttackTarget(creeper, owner);
        if (threat != null) {
            creeper.setTarget(threat);
            creeper.getNavigation().moveTo(threat, COMBAT_CHASE_SPEED);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            return;
        }

        ServerPlayer owner = TamedCreeperOwner.getOwner(creeper).orElse(null);
        performDeathBlast(creeper, owner);
    }

    private static boolean tickActiveSlimeJump(Creeper creeper, ServerPlayer owner, CompoundTag data) {
        int targetId = data.getIntOr(SLIME_TARGET_ID_TAG, 0);
        if (targetId == 0) {
            return false;
        }

        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            data.putInt(SLIME_TARGET_ID_TAG, 0);
            data.putInt(SLIME_STICK_TICKS_TAG, 0);
            return false;
        }

        Entity entity = serverLevel.getEntity(targetId);
        if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
            data.putInt(SLIME_TARGET_ID_TAG, 0);
            data.putInt(SLIME_STICK_TICKS_TAG, 0);
            return false;
        }

        int stickTicks = data.getIntOr(SLIME_STICK_TICKS_TAG, 0);

        if (stickTicks > 0) {
            creeper.getNavigation().stop();
            creeper.teleportTo(target.getX(), target.getY(), target.getZ());
            creeper.setDeltaMovement(Vec3.ZERO);
            data.putInt(SLIME_STICK_TICKS_TAG, stickTicks - 1);

            if (stickTicks == 1) {
                performSlimeExplosion(creeper, owner, target, serverLevel);
                data.putInt(SLIME_TARGET_ID_TAG, 0);
            }
        } else {
            creeper.getNavigation().moveTo(target, SLIME_JUMP_SPEED);
            if (creeper.distanceToSqr(target) <= SLIME_STICK_DISTANCE_SQR) {
                data.putInt(SLIME_STICK_TICKS_TAG, SLIME_STICK_DURATION_TICKS);
            }
        }

        return true;
    }

    private static boolean tryStartSlimeJump(Creeper creeper, CompoundTag data, LivingEntity target) {
        if (!CreeperAbilityStorage.hasAbility(creeper, CreeperAbility.SLIME_JUMP)) {
            return false;
        }

        if (data.getIntOr(SLIME_JUMP_COOLDOWN_TAG, 0) > 0) {
            return false;
        }

        if (target == null || !target.isAlive()) {
            return false;
        }

        if (isInBlastRange(creeper, target)) {
            return false;
        }

        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        data.putInt(SLIME_TARGET_ID_TAG, target.getId());
        data.putInt(SLIME_STICK_TICKS_TAG, 0);
        data.putInt(SLIME_JUMP_COOLDOWN_TAG, SLIME_JUMP_COOLDOWN_TICKS);

        Vec3 dir = target.position().subtract(creeper.position()).normalize();
        creeper.setDeltaMovement(dir.x * 0.8D, 0.55D, dir.z * 0.8D);
        creeper.hurtMarked = true;

        serverLevel.playSound(null, creeper.blockPosition(),
                SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 1.0F, 0.8F);

        return true;
    }

    private static void performSlimeExplosion(Creeper creeper, ServerPlayer owner, LivingEntity primaryTarget, ServerLevel serverLevel) {
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                creeper.getX(), creeper.getY() + 0.8D, creeper.getZ(),
                2, 0.2D, 0.2D, 0.2D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                creeper.getX(), creeper.getY() + 0.8D, creeper.getZ(),
                20, 0.5D, 0.4D, 0.5D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.POOF,
                creeper.getX(), creeper.getY() + 0.8D, creeper.getZ(),
                14, 0.4D, 0.3D, 0.4D, 0.03D);
        serverLevel.playSound(null, creeper.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.1F, 0.85F);

        for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, creeper.getBoundingBox().inflate(BLAST_RADIUS))) {
            if (!isCombatTarget(mob, owner)) {
                continue;
            }

            mob.hurt(serverLevel.damageSources().mobAttack(creeper), BLAST_DAMAGE * SLIME_BLAST_MULTIPLIER);
            knockBackFromCreeper(creeper, mob);
        }

        Vec3 away = creeper.position().subtract(primaryTarget.position());
        if (away.lengthSqr() > 1.0E-4D) {
            Vec3 push = away.normalize().scale(1.0D);
            creeper.setDeltaMovement(push.x, 0.55D, push.z);
            creeper.hurtMarked = true;
        }

        if (primaryTarget.isAlive()) {
            creeper.setTarget(primaryTarget);
        } else {
            creeper.setTarget(null);
        }
    }

    private static LivingEntity findThreatToOwnerOrAttackTarget(Creeper creeper, ServerPlayer owner) {
        LivingEntity ownerAttackTarget = owner.getLastHurtMob();
        if (isOwnerAttackTarget(creeper, ownerAttackTarget, owner)
                && creeper.distanceToSqr(ownerAttackTarget) <= DEFENSE_SCAN_RADIUS * DEFENSE_SCAN_RADIUS) {
            return ownerAttackTarget;
        }

        return creeper.level()
                .getEntitiesOfClass(Mob.class, owner.getBoundingBox().inflate(DEFENSE_SCAN_RADIUS),
                        mob -> isValidThreatToOwner(mob, owner) || isThreatToCreeper(creeper, mob))
                .stream()
                .min((left, right) -> Double.compare(creeper.distanceToSqr(left), creeper.distanceToSqr(right)))
                .orElse(null);
    }

    private static boolean isValidThreatToOwner(LivingEntity entity, ServerPlayer owner) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }

        if (!(mob instanceof Enemy)) {
            return false;
        }

        if (!mob.isAlive()) {
            return false;
        }

        return mob.getTarget() == owner;
    }

    private static boolean isThreatToCreeper(Creeper creeper, LivingEntity entity) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }

        if (!(mob instanceof Enemy)) {
            return false;
        }

        if (!mob.isAlive() || mob.isSpectator() || mob == creeper) {
            return false;
        }

        if (mob instanceof Creeper otherCreeper && CreeperState.get(otherCreeper) == CreeperState.TAMED) {
            return false;
        }

        return mob.getTarget() == creeper;
    }

    private static boolean isOwnerAttackTarget(Creeper creeper, LivingEntity entity, ServerPlayer owner) {
        if (!(entity instanceof Mob mob)) {
            return false;
        }

        if (!(mob instanceof Enemy)) {
            return false;
        }

        if (mob == creeper) {
            return false;
        }

        if (mob instanceof Creeper otherCreeper && CreeperState.get(otherCreeper) == CreeperState.TAMED) {
            return false;
        }

        return mob.isAlive() && !mob.isSpectator() && owner.hasLineOfSight(mob);
    }

    private static boolean isInBlastRange(Creeper creeper, LivingEntity target) {
        return target != null
                && target.isAlive()
                && creeper.distanceToSqr(target) <= BLAST_TRIGGER_DISTANCE_SQR;
    }

    private static void performBlastShove(Creeper creeper, ServerPlayer owner, LivingEntity primaryTarget) {
        creeper.setSwellDir(-1);
        creeper.getNavigation().stop();

        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                creeper.getX(),
                creeper.getY() + 0.8D,
                creeper.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D);
        serverLevel.sendParticles(ParticleTypes.POOF,
                creeper.getX(),
                creeper.getY() + 0.8D,
                creeper.getZ(),
                14,
                0.35D,
                0.25D,
                0.35D,
                0.03D);
        serverLevel.playSound(null,
                creeper.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.HOSTILE,
                0.8F,
                1.15F);

        double effectiveBlastRadius = CreeperAbilityStorage.hasAbility(creeper, CreeperAbility.EXTREME_BLAST)
                ? BLAST_RADIUS * 2.0D : BLAST_RADIUS;
        float effectiveBlastDamage = CreeperAbilityStorage.hasAbility(creeper, CreeperAbility.EXTREME_BLAST)
                ? BLAST_DAMAGE * 2.0F : BLAST_DAMAGE;

        for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, creeper.getBoundingBox().inflate(effectiveBlastRadius))) {
            if (!isCombatTarget(mob, owner)) {
                continue;
            }

            mob.hurt(serverLevel.damageSources().mobAttack(creeper), effectiveBlastDamage);
            knockBackFromCreeper(creeper, mob);
        }

        // Keep pressure on the main target if it survives the shove.
        if (primaryTarget.isAlive()) {
            creeper.setTarget(primaryTarget);
        } else {
            creeper.setTarget(null);
        }
    }

    private static void performDeathBlast(Creeper creeper, ServerPlayer owner) {
        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                creeper.getX(),
                creeper.getY() + 0.8D,
                creeper.getZ(),
                1,
                0.0D,
                0.0D,
                0.0D,
                0.0D);
        serverLevel.sendParticles(ParticleTypes.POOF,
                creeper.getX(),
                creeper.getY() + 0.8D,
                creeper.getZ(),
                24,
                0.6D,
                0.4D,
                0.6D,
                0.04D);
        serverLevel.playSound(null,
                creeper.blockPosition(),
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.HOSTILE,
                1.2F,
                0.9F);

        for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, creeper.getBoundingBox().inflate(DEATH_BLAST_RADIUS))) {
            if (!isDeathBlastTarget(mob, creeper, owner)) {
                continue;
            }

            mob.hurt(serverLevel.damageSources().explosion(creeper, null), DEATH_BLAST_DAMAGE);
            knockBackFromSource(creeper, mob, DEATH_BLAST_KNOCKBACK_STRENGTH);
        }
    }

    private static boolean isCombatTarget(Mob mob, ServerPlayer owner) {
        if (mob instanceof Creeper creeper && CreeperState.get(creeper) == CreeperState.TAMED) {
            return false;
        }

        return mob.isAlive()
                && mob instanceof Enemy
                && (mob.getTarget() == owner || owner.getLastHurtMob() == mob);
    }

    private static void knockBackFromCreeper(Creeper creeper, LivingEntity target) {
        knockBackFromSource(creeper, target, BLAST_KNOCKBACK_STRENGTH);
    }

    private static boolean isDeathBlastTarget(Mob mob, Creeper source, ServerPlayer owner) {
        if (!(mob instanceof Enemy) || !mob.isAlive() || mob.isSpectator() || mob == source) {
            return false;
        }

        if (mob instanceof Creeper creeper && CreeperState.get(creeper) == CreeperState.TAMED) {
            return false;
        }

        return true;
    }

    private static void knockBackFromSource(Creeper source, LivingEntity target, double strength) {
        Vec3 push = target.position().subtract(source.position());
        if (push.lengthSqr() < 1.0E-4D) {
            push = new Vec3((target.getRandom().nextDouble() - 0.5D) * 0.1D, 0.0D, (target.getRandom().nextDouble() - 0.5D) * 0.1D);
        }

        Vec3 normalized = push.normalize().scale(strength);
        target.push(normalized.x, 0.35D, normalized.z);
        target.hurtMarked = true;
    }
}
