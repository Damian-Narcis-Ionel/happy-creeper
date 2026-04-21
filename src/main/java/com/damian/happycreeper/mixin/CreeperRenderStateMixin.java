package com.damian.happycreeper.mixin;

import com.damian.happycreeper.TamedCreeperAppearance;
import com.damian.happycreeper.client.HappyCreeperCreeperRenderState;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreeperRenderState.class)
abstract class CreeperRenderStateMixin implements HappyCreeperCreeperRenderState {
    @Unique
    private int happycreeper$variant = TamedCreeperAppearance.NONE_VARIANT;
    @Unique
    private ItemStack happycreeper$helmet = ItemStack.EMPTY;
    @Unique
    private ItemStack happycreeper$chestplate = ItemStack.EMPTY;

    @Override
    public int happycreeper$getVariant() {
        return happycreeper$variant;
    }

    @Override
    public void happycreeper$setVariant(int variant) {
        this.happycreeper$variant = variant;
    }

    @Override
    public ItemStack happycreeper$getHelmet() {
        return happycreeper$helmet;
    }

    @Override
    public void happycreeper$setHelmet(ItemStack helmet) {
        this.happycreeper$helmet = helmet;
    }

    @Override
    public ItemStack happycreeper$getChestplate() {
        return happycreeper$chestplate;
    }

    @Override
    public void happycreeper$setChestplate(ItemStack chestplate) {
        this.happycreeper$chestplate = chestplate;
    }
}
