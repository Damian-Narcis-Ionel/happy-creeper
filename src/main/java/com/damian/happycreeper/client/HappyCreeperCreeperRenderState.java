package com.damian.happycreeper.client;

import net.minecraft.world.item.ItemStack;

public interface HappyCreeperCreeperRenderState {
    int happycreeper$getVariant();

    void happycreeper$setVariant(int variant);

    ItemStack happycreeper$getHelmet();

    void happycreeper$setHelmet(ItemStack helmet);

    ItemStack happycreeper$getChestplate();

    void happycreeper$setChestplate(ItemStack chestplate);
}
