package com.damian.happycreeper.client;

import com.damian.happycreeper.HappyCreeper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

public final class HappyCreeperRenderStateKeys {
    public static final ContextKey<Integer> CREEPER_VARIANT = new ContextKey<>(
            ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_variant"));
    public static final ContextKey<ItemStack> CREEPER_HELMET = new ContextKey<>(
            ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_helmet"));
    public static final ContextKey<ItemStack> CREEPER_CHESTPLATE = new ContextKey<>(
            ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_chestplate"));

    private HappyCreeperRenderStateKeys() {
    }
}
