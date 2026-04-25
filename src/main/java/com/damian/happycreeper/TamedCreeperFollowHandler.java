package com.damian.happycreeper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
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

        if (creeper.level() != owner.level()) {
            if (tryChangeDimensionNearOwner(creeper, owner)) {
                return;
            }

            creeper.getNavigation().stop();
            return;
        }

        double distanceToOwnerSqr = creeper.distanceToSqr(owner);
        if (distanceToOwnerSqr >= TELEPORT_DISTANCE_SQR && tryTeleportNearOwner(creeper, owner)) {
            creeper.getNavigation().stop();
            return;
        }

        boolean hasCombatTarget = creeper.getTarget() != null && creeper.getTarget().isAlive();

        if (distanceToOwnerSqr >= START_FOLLOWING_DISTANCE_SQR) {
            if (!hasCombatTarget) {
                creeper.getNavigation().moveTo(owner, FOLLOW_SPEED);
            }
            return;
        }

        if (distanceToOwnerSqr <= STOP_FOLLOWING_DISTANCE_SQR && !hasCombatTarget) {
            creeper.getNavigation().stop();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncOwnedCreepersToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncOwnedCreepersToPlayer(player);
        }
    }

    private static boolean tryChangeDimensionNearOwner(Creeper creeper, ServerPlayer owner) {
        if (!(owner.level() instanceof ServerLevel destinationLevel)) {
            return false;
        }

        Creeper teleportedCreeper = (Creeper) creeper.changeDimension(
                new DimensionTransition(destinationLevel, owner, DimensionTransition.PLACE_PORTAL_TICKET));
        if (teleportedCreeper == null) {
            return false;
        }

        teleportedCreeper.getNavigation().stop();
        return tryTeleportNearOwner(teleportedCreeper, owner);
    }

    private static void syncOwnedCreepersToPlayer(ServerPlayer player) {
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }

        if (player.getServer() == null) {
            return;
        }

        for (ServerLevel level : player.getServer().getAllLevels()) {
            for (var entity : level.getAllEntities()) {
                if (!(entity instanceof Creeper creeper)) {
                    continue;
                }

                if (CreeperState.get(creeper) != CreeperState.TAMED) {
                    continue;
                }

                if (!TamedCreeperOwner.isOwner(creeper, player)) {
                    continue;
                }

                if (TamedCreeperCommandState.isStaying(creeper)) {
                    continue;
                }

                if (creeper.level() != player.level()) {
                    tryChangeDimensionNearOwner(creeper, player);
                } else {
                    tryTeleportNearOwner(creeper, player);
                }
            }
        }
    }

    private static boolean tryTeleportNearOwner(Creeper creeper, ServerPlayer owner) {
        BlockPos ownerPos = owner.blockPosition();
        Level level = owner.level();

        for (int xOffset = -2; xOffset <= 2; xOffset++) {
            for (int zOffset = -2; zOffset <= 2; zOffset++) {
                if (Math.abs(xOffset) < 2 && Math.abs(zOffset) < 2) {
                    continue;
                }

                BlockPos targetPos = ownerPos.offset(xOffset, 0, zOffset);
                if (canTeleportTo(creeper, level, targetPos)) {
                    creeper.teleportTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D);
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean canTeleportTo(Creeper creeper, Level level, BlockPos targetPos) {
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
