package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;

public enum CreeperState {
    NORMAL("normal"),
    WEAKENED("weakened"),
    TAMED("tamed");

    private static final String STATE_TAG = "HappyCreeperState";

    private final String serializedName;

    CreeperState(String serializedName) {
        this.serializedName = serializedName;
    }

    public static CreeperState get(Creeper creeper) {
        CompoundTag data = creeper.getPersistentData();
        String value = data.getStringOr(STATE_TAG, NORMAL.serializedName);

        for (CreeperState state : values()) {
            if (state.serializedName.equals(value)) {
                return state;
            }
        }

        return NORMAL;
    }

    public static void set(Creeper creeper, CreeperState state) {
        creeper.getPersistentData().putString(STATE_TAG, state.serializedName);
    }

    public boolean isAtLeastWeakened() {
        return this == WEAKENED || this == TAMED;
    }
}
