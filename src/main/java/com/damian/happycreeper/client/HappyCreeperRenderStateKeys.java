package com.damian.happycreeper.client;

import com.damian.happycreeper.HappyCreeper;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;

public final class HappyCreeperRenderStateKeys {
    public static final ContextKey<Integer> CREEPER_VARIANT = new ContextKey<>(
            Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_variant"));
    public static final ContextKey<ItemStack> CREEPER_HELMET = new ContextKey<>(
            Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_helmet"));
    public static final ContextKey<ItemStack> CREEPER_CHESTPLATE = new ContextKey<>(
            Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "creeper_chestplate"));
    public static final ContextKey<ItemStack> PLAYER_HEAD_ITEM = new ContextKey<>(
            Identifier.fromNamespaceAndPath(HappyCreeper.MODID, "player_head_item"));

    private HappyCreeperRenderStateKeys() {
    }
}
