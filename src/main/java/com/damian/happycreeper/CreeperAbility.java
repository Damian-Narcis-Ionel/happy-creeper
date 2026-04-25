package com.damian.happycreeper;

public enum CreeperAbility {
    FIRE_RESISTANCE(0),
    SWIM_SPEED(1),
    EXTREME_BLAST(2),
    SLIME_JUMP(3);

    private final int bit;

    CreeperAbility(int bit) {
        this.bit = bit;
    }

    public int getMask() {
        return 1 << bit;
    }
}
