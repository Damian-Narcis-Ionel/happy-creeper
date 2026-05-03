package com.damian.happycreeper;

import com.damian.happycreeper.menu.CreeperMenu;
import com.damian.happycreeper.network.SyncColorVariantPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;

public class HappyCreeper implements ModInitializer {
    public static final String MODID = "happycreeper";

    public static final Item BISCUIT = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "biscuit"),
            new Item(itemProps("biscuit").food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build())));
    public static final Item ANTI_BLAST_BISCUIT = registerSimple("anti_blast_biscuit");
    public static final Item SWEET_GUNPOWDER_BISCUIT = registerSimple("sweet_gunpowder_biscuit");
    public static final Item RAINBOW_BISCUIT = registerSimple("rainbow_biscuit");
    public static final Item LAVA_BISCUIT = registerSimple("lava_biscuit");
    public static final Item FISH_BISCUIT = registerSimple("fish_biscuit");
    public static final Item EXTREME_BLAST_BISCUIT = registerSimple("extreme_blast_biscuit");
    public static final Item SLIME_BISCUIT = registerSimple("slime_biscuit");
    public static final Item FAKE_HAPPY_CREEPER_HEAD = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MODID, "fake_happy_creeper_head"),
            new FakeCreeperHeadItem(itemProps("fake_happy_creeper_head")
                    .stacksTo(1)
                    .component(DataComponents.EQUIPPABLE,
                            Equippable.builder(EquipmentSlot.HEAD)
                                    .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                                    .build())));

    public static final MenuType<CreeperMenu> CREEPER_MENU = Registry.register(
            BuiltInRegistries.MENU,
            ResourceLocation.fromNamespaceAndPath(MODID, "creeper_menu"),
            new ExtendedScreenHandlerType<>((syncId, inventory, entityId) -> new CreeperMenu(syncId, inventory, entityId), ByteBufCodecs.VAR_INT));

    private static Item.Properties itemProps(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MODID, name)));
    }

    private static Item registerSimple(String name) {
        var id = ResourceLocation.fromNamespaceAndPath(MODID, name);
        return Registry.register(BuiltInRegistries.ITEM, id,
                new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(SyncColorVariantPacket.TYPE, SyncColorVariantPacket.CODEC);
        EventRegistrar.register();
        LootInjector.register();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                    HappyCreeperDevCommands.register(dispatcher));
        }

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
