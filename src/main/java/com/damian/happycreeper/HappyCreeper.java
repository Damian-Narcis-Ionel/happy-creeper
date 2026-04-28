package com.damian.happycreeper;

import com.damian.happycreeper.menu.CreeperMenu;
import com.damian.happycreeper.network.SyncColorVariantPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class HappyCreeper implements ModInitializer {
    public static final String MODID = "happycreeper";

    public static final Item BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "biscuit"),
            new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build())));
    public static final Item ANTI_BLAST_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "anti_blast_biscuit"),
            new Item(new Item.Properties()));
    public static final Item SWEET_GUNPOWDER_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "sweet_gunpowder_biscuit"),
            new Item(new Item.Properties()));
    public static final Item RAINBOW_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "rainbow_biscuit"),
            new Item(new Item.Properties()));
    public static final Item LAVA_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "lava_biscuit"),
            new Item(new Item.Properties()));
    public static final Item FISH_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "fish_biscuit"),
            new Item(new Item.Properties()));
    public static final Item EXTREME_BLAST_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "extreme_blast_biscuit"),
            new Item(new Item.Properties()));
    public static final Item SLIME_BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "slime_biscuit"),
            new Item(new Item.Properties()));
    public static final Item FAKE_HAPPY_CREEPER_HEAD = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "fake_happy_creeper_head"),
            new FakeCreeperHeadItem(new Item.Properties().stacksTo(1)));

    public static final MenuType<CreeperMenu> CREEPER_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(MODID, "creeper_menu"),
            new ExtendedScreenHandlerType<>((syncId, inventory, entityId) -> new CreeperMenu(syncId, inventory, entityId), ByteBufCodecs.VAR_INT));

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SyncColorVariantPacket.TYPE, SyncColorVariantPacket.CODEC);
        EventRegistrar.register();
        LootInjector.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.accept(BISCUIT);
            entries.accept(ANTI_BLAST_BISCUIT);
            entries.accept(SWEET_GUNPOWDER_BISCUIT);
            entries.accept(RAINBOW_BISCUIT);
            entries.accept(LAVA_BISCUIT);
            entries.accept(FISH_BISCUIT);
            entries.accept(EXTREME_BLAST_BISCUIT);
            entries.accept(SLIME_BISCUIT);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entries -> entries.accept(FAKE_HAPPY_CREEPER_HEAD));
    }

    public static boolean isCreeperDisguise(ItemStack stack) {
        return stack.is(Items.CREEPER_HEAD) || stack.is(FAKE_HAPPY_CREEPER_HEAD);
    }
}
