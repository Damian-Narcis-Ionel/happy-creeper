package com.damian.happycreeper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;

public final class TamedCreeperBehaviorHandler {
    private TamedCreeperBehaviorHandler() {}

    public static boolean shouldBlockTarget(Creeper creeper, LivingEntity target) {
        return CreeperState.get(creeper) == CreeperState.TAMED && target instanceof Player;
    }

    public static void tick(Creeper creeper) {
        if (CreeperState.get(creeper) != CreeperState.TAMED) return;
        if (!(creeper.getTarget() instanceof Player)) return;
        clearAggression(creeper);
    }

    private static void clearAggression(Creeper creeper) {
        creeper.setTarget(null);
        creeper.setSwellDir(-1);
    }
}
