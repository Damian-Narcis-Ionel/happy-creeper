package com.damian.happycreeper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperSleepHandler {
    private static final int BED_MONSTER_CHECK_RADIUS = 8;

    private TamedCreeperSleepHandler() {
    }

    @SubscribeEvent
    public static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() != Player.BedSleepingProblem.NOT_SAFE) {
            return;
        }

        ServerPlayer player = event.getEntity();
        BlockPos bedPos = event.getPos();
        AABB checkArea = new AABB(bedPos).inflate(BED_MONSTER_CHECK_RADIUS);

        boolean hasRealThreat = player.level()
                .getEntitiesOfClass(Monster.class, checkArea, monster -> monster.isAlive() && monster.isPreventingPlayerRest(player))
                .stream()
                .anyMatch(monster -> !(monster instanceof net.minecraft.world.entity.monster.Creeper creeper)
                        || CreeperState.get(creeper) != CreeperState.TAMED);

        if (!hasRealThreat) {
            event.setProblem(null);
        }
    }
}
