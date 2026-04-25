package com.damian.happycreeper;

import com.damian.happycreeper.menu.CreeperMenu;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(HappyCreeper.MODID)
public class HappyCreeper {
    public static final String MODID = "happycreeper";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.MENU, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final DeferredItem<Item> BISCUIT = ITEMS.registerSimpleItem("biscuit",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.3f).build()));
    public static final DeferredItem<Item> ANTI_BLAST_BISCUIT = ITEMS.registerSimpleItem("anti_blast_biscuit", new Item.Properties());
    public static final DeferredItem<Item> SWEET_GUNPOWDER_BISCUIT = ITEMS.registerSimpleItem("sweet_gunpowder_biscuit", new Item.Properties());
    public static final DeferredItem<Item> RAINBOW_BISCUIT = ITEMS.registerSimpleItem("rainbow_biscuit", new Item.Properties());
    public static final DeferredItem<Item> LAVA_BISCUIT = ITEMS.registerSimpleItem("lava_biscuit", new Item.Properties());
    public static final DeferredItem<Item> FISH_BISCUIT = ITEMS.registerSimpleItem("fish_biscuit", new Item.Properties());
    public static final DeferredItem<Item> EXTREME_BLAST_BISCUIT = ITEMS.registerSimpleItem("extreme_blast_biscuit", new Item.Properties());
    public static final DeferredItem<Item> SLIME_BISCUIT = ITEMS.registerSimpleItem("slime_biscuit", new Item.Properties());
    public static final DeferredItem<FakeCreeperHeadItem> FAKE_HAPPY_CREEPER_HEAD = ITEMS.registerItem(
            "fake_happy_creeper_head",
            FakeCreeperHeadItem::new,
            new Item.Properties().stacksTo(1).equippable(EquipmentSlot.HEAD));
    public static final DeferredHolder<MenuType<?>, MenuType<CreeperMenu>> CREEPER_MENU = MENUS.register(
            "creeper_menu",
            () -> IMenuTypeExtension.create((windowId, inventory, extraData) -> new CreeperMenu(windowId, inventory, extraData.readInt())));

    public HappyCreeper(IEventBus modEventBus) {
        TamedCreeperAppearance.init();

        ITEMS.register(modEventBus);
        MENUS.register(modEventBus);
        ATTACHMENTS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(HappyCreeperNetwork::onRegisterPayloadHandlers);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(BISCUIT);
            event.accept(ANTI_BLAST_BISCUIT);
            event.accept(SWEET_GUNPOWDER_BISCUIT);
            event.accept(RAINBOW_BISCUIT);
            event.accept(LAVA_BISCUIT);
            event.accept(FISH_BISCUIT);
            event.accept(EXTREME_BLAST_BISCUIT);
            event.accept(SLIME_BISCUIT);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(FAKE_HAPPY_CREEPER_HEAD);
        }
    }

    public static boolean isCreeperDisguise(ItemStack stack) {
        return stack.is(Items.CREEPER_HEAD) || stack.is(FAKE_HAPPY_CREEPER_HEAD.get());
    }
}
