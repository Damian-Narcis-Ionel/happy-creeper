package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;

public final class CreeperAbilityStorage {
    private static final String ABILITIES_TAG = "HappyCreeperAbilities";

    private CreeperAbilityStorage() {
    }

    public static boolean hasAbility(Creeper creeper, CreeperAbility ability) {
        return (creeper.getPersistentData().getInt(ABILITIES_TAG) & ability.getMask()) != 0;
    }

    public static void grantAbility(Creeper creeper, CreeperAbility ability) {
        CompoundTag data = creeper.getPersistentData();
        data.putInt(ABILITIES_TAG, data.getInt(ABILITIES_TAG) | ability.getMask());
        creeper.setPersistenceRequired();
    }

    public static int getBitmask(Creeper creeper) {
        return creeper.getPersistentData().getInt(ABILITIES_TAG);
    }
}
