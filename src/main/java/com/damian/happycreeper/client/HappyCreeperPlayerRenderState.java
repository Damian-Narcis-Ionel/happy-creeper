package com.damian.happycreeper.client;

import net.minecraft.world.item.ItemStack;

public interface HappyCreeperPlayerRenderState {
    ItemStack happycreeper$getHeadEquipment();

    void happycreeper$setHeadEquipment(ItemStack headEquipment);
}
