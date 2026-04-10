package com.damian.happycreeper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperFollowHandler {
    private static final double FOLLOW_SPEED = 1.35D;
    private static final double STOP_FOLLOWING_DISTANCE_SQR = 4.0D;
    private static final double START_FOLLOWING_DISTANCE_SQR = 36.0D;
    private static final double TELEPORT_DISTANCE_SQR = 144.0D;

    private TamedCreeperFollowHandler() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            return;
        }

        if (!(TamedCreeperOwner.getOwner(creeper).orElse(null) instanceof ServerPlayer owner)) {
            return;
        }

        if (!owner.isAlive() || owner.isSpectator()) {
            creeper.getNavigation().stop();
            return;
        }

        if (TamedCreeperCommandState.isStaying(creeper)) {
            creeper.getNavigation().stop();
            creeper.setTarget(null);
            creeper.setSwellDir(-1);
            return;
        }

        double distanceToOwnerSqr = creeper.distanceToSqr(owner);
        if (distanceToOwnerSqr >= TELEPORT_DISTANCE_SQR && tryTeleportNearOwner(creeper, owner)) {
            creeper.getNavigation().stop();
            return;
        }

        if (distanceToOwnerSqr >= START_FOLLOWING_DISTANCE_SQR) {
            creeper.getNavigation().moveTo(owner, FOLLOW_SPEED);
            return;
        }

        if (distanceToOwnerSqr <= STOP_FOLLOWING_DISTANCE_SQR) {
            creeper.getNavigation().stop();
        }
    }

    private static boolean tryTeleportNearOwner(Creeper creeper, ServerPlayer owner) {
        BlockPos ownerPos = owner.blockPosition();

        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                if (Math.abs(xOffset) < 2 && Math.abs(zOffset) < 2) {
                    continue;
                }

                BlockPos targetPos = ownerPos.offset(xOffset, 0, zOffset);
                if (canTeleportTo(creeper, targetPos)) {
                    creeper.teleportTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D);
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean canTeleportTo(Creeper creeper, BlockPos targetPos) {
        Level level = creeper.level();
        BlockPos belowPos = targetPos.below();
        if (!level.getBlockState(belowPos).blocksMotion()) {
            return false;
        }

        return level.noCollision(creeper, creeper.getBoundingBox().move(
                targetPos.getX() + 0.5D - creeper.getX(),
                targetPos.getY() - creeper.getY(),
                targetPos.getZ() + 0.5D - creeper.getZ()));
    }
}
