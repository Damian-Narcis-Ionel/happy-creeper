package com.damian.happycreeper;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

public final class FakeCreeperHeadItem extends Item {
    public FakeCreeperHeadItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Equippable equippable = stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        if (equippable != null && equippable.slot() == EquipmentSlot.HEAD) {
            return equippable.swapWithEquipmentSlot(stack, player);
        }

        return super.use(level, player, hand);
    }
}
