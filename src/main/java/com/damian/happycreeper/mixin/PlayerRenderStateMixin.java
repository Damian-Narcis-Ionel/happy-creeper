package com.damian.happycreeper.mixin;

import com.damian.happycreeper.client.HappyCreeperPlayerRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
abstract class PlayerRenderStateMixin implements HappyCreeperPlayerRenderState {
    @Unique
    private ItemStack happycreeper$headEquipment = ItemStack.EMPTY;

    @Override
    public ItemStack happycreeper$getHeadEquipment() {
        return happycreeper$headEquipment;
    }

    @Override
    public void happycreeper$setHeadEquipment(ItemStack headEquipment) {
        this.happycreeper$headEquipment = headEquipment;
    }
}
