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
        CompoundTag data = IPersistentDataProvider.of(creeper);
        String value = data.getString(STATE_TAG);

        for (CreeperState state : values()) {
            if (state.serializedName.equals(value)) {
                return state;
            }
        }

        return NORMAL;
    }

    public static void set(Creeper creeper, CreeperState state) {
        IPersistentDataProvider.of(creeper).putString(STATE_TAG, state.serializedName);
    }

    public boolean isAtLeastWeakened() {
        return this == WEAKENED || this == TAMED;
    }
}
