package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperCommandState {
    private static final String STAYING_TAG = "HappyCreeperStaying";

    private TamedCreeperCommandState() {
    }

    public static boolean isStaying(Creeper creeper) {
        return creeper.getPersistentData().getBooleanOr(STAYING_TAG, false);
    }

    public static void setStaying(Creeper creeper, boolean staying) {
        CompoundTag data = creeper.getPersistentData();
        data.putBoolean(STAYING_TAG, staying);
    }

    public static boolean toggleStaying(Creeper creeper) {
        boolean nextState = !isStaying(creeper);
        setStaying(creeper, nextState);
        return nextState;
    }
}
