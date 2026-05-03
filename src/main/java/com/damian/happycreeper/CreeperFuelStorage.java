package com.damian.happycreeper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class CreeperFuelStorage {
    private static final String FUEL_STACK_TAG = "HappyCreeperFuelStack";
    private static final float GUNPOWDER_HEAL_AMOUNT = 8.0F;
    private static final float SWEET_BISCUIT_HEAL_AMOUNT = 12.0F;

    private CreeperFuelStorage() {
    }

    public static ItemStack getFuelStack(Creeper creeper) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        if (!data.contains(FUEL_STACK_TAG)) {
            return ItemStack.EMPTY;
        }

        return data.read(FUEL_STACK_TAG, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    public static void setFuelStack(Creeper creeper, ItemStack stack) {
        CompoundTag data = IPersistentDataProvider.of(creeper);
        if (stack.isEmpty()) {
            data.remove(FUEL_STACK_TAG);
            return;
        }

        data.store(FUEL_STACK_TAG, ItemStack.OPTIONAL_CODEC, stack);
    }

    public static boolean isFuelItem(ItemStack stack) {
        return stack.is(Items.GUNPOWDER) || stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT);
    }

    public static float consumeFuelAndGetHealAmount(Creeper creeper) {
        ItemStack fuelStack = getFuelStack(creeper);
        if (!isFuelItem(fuelStack)) {
            return 0.0F;
        }

        float healAmount = getHealAmount(fuelStack);
        fuelStack.shrink(1);
        setFuelStack(creeper, fuelStack);
        return healAmount;
    }

    private static float getHealAmount(ItemStack stack) {
        if (stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT)) {
            return SWEET_BISCUIT_HEAL_AMOUNT;
        }

        if (stack.is(Items.GUNPOWDER)) {
            return GUNPOWDER_HEAL_AMOUNT;
        }

        return 0.0F;
    }
}
