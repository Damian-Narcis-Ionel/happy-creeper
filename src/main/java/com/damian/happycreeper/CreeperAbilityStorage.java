package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;

public final class CreeperAbilityStorage {
    private static final String ABILITIES_TAG = "HappyCreeperAbilities";

    private CreeperAbilityStorage() {
    }

    public static boolean hasAbility(Creeper creeper, CreeperAbility ability) {
        return (IPersistentDataProvider.of(creeper).getIntOr(ABILITIES_TAG, 0) & ability.getMask()) != 0;
    }

    public static void grantAbility(Creeper creeper, CreeperAbility ability) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        data.putInt(ABILITIES_TAG, data.getIntOr(ABILITIES_TAG, 0) | ability.getMask());
        creeper.setPersistenceRequired();
    }

    public static int getBitmask(Creeper creeper) {
        return IPersistentDataProvider.of(creeper).getIntOr(ABILITIES_TAG, 0);
    }
}
