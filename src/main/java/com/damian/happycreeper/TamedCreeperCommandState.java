package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;

public final class TamedCreeperCommandState {
    private static final String STAYING_TAG = "HappyCreeperStaying";

    private TamedCreeperCommandState() {
    }

    public static boolean isStaying(Creeper creeper) {
        return IPersistentDataProvider.of(creeper).getBoolean(STAYING_TAG);
    }

    public static void setStaying(Creeper creeper, boolean staying) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        data.putBoolean(STAYING_TAG, staying);
    }

    public static boolean toggleStaying(Creeper creeper) {
        boolean nextState = !isStaying(creeper);
        setStaying(creeper, nextState);
        return nextState;
    }
}
