package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public interface IPersistentDataProvider {
    CompoundTag getPersistentData();

    static CompoundTag of(Entity entity) {
        return ((IPersistentDataProvider) entity).getPersistentData();
    }
}
