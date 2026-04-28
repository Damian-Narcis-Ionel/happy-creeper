package com.damian.happycreeper;

import net.minecraft.world.entity.monster.Creeper;

public final class CreeperPersistenceHandler {
    private CreeperPersistenceHandler() {}

    public static void tick(Creeper creeper) {
        if (!CreeperState.get(creeper).isAtLeastWeakened()) return;
        if (!creeper.isPersistenceRequired()) {
            creeper.setPersistenceRequired();
        }
    }
}
