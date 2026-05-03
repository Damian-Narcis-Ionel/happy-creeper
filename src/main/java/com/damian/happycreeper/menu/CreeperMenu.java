package com.damian.happycreeper.menu;

import com.damian.happycreeper.CreeperAbility;
import com.damian.happycreeper.CreeperAbilityStorage;
import com.damian.happycreeper.CreeperFuelStorage;
import com.damian.happycreeper.CreeperState;
import com.damian.happycreeper.HappyCreeper;
import com.damian.happycreeper.TamedCreeperCommandState;
import com.damian.happycreeper.TamedCreeperOwner;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.Items;

public class CreeperMenu extends AbstractContainerMenu {
    private static final int FOLLOW_BUTTON_ID = 0;
    private static final int HELMET_SLOT = 0;
    private static final int CHESTPLATE_SLOT = 1;
    private static final int FUEL_SLOT = 2;
    private static final int PLAYER_INVENTORY_START = 3;
    private static final int PLAYER_HOTBAR_START = 30;
    private static final int PLAYER_SLOT_COUNT = 36;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + PLAYER_SLOT_COUNT;

    private static final int HELMET_X = 8;
    private static final int CHESTPLATE_X = 8;
    private static final int FUEL_X = 30;
    private static final int HELMET_Y = 19;
    private static final int CHESTPLATE_Y = 41;
    private static final int FUEL_Y = 41;

    private final Creeper creeper;
    private final DataSlot stayingState = DataSlot.standalone();
    private final DataSlot currentHealth = DataSlot.standalone();
    private final DataSlot maxHealth = DataSlot.standalone();
    private final DataSlot abilitiesBitmask = DataSlot.standalone();

    public CreeperMenu(int containerId, Inventory playerInventory, int creeperId) {
        this(containerId, playerInventory, getCreeper(playerInventory, creeperId));
    }

    public CreeperMenu(int containerId, Inventory playerInventory, Creeper creeper) {
        super(HappyCreeper.CREEPER_MENU, containerId);
        this.creeper = creeper;

        addSlot(new CreeperArmorSlot(
                EquipmentSlot.HEAD,
                HELMET_SLOT,
                HELMET_X,
                HELMET_Y,
                InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));
        addSlot(new CreeperArmorSlot(
                EquipmentSlot.CHEST,
                CHESTPLATE_SLOT,
                CHESTPLATE_X,
                CHESTPLATE_Y,
                InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
        addSlot(new CreeperFuelSlot(FUEL_SLOT, FUEL_X, FUEL_Y));

        addPlayerInventory(playerInventory);
        addDataSlot(stayingState);
        addDataSlot(currentHealth);
        addDataSlot(maxHealth);
        addDataSlot(abilitiesBitmask);
        broadcastChanges();
    }

    public Creeper getCreeper() {
        return creeper;
    }

    public boolean isStaying() {
        return stayingState.get() != 0;
    }

    public int getDisplayedHealth() {
        return currentHealth.get();
    }

    public int getDisplayedMaxHealth() {
        return maxHealth.get();
    }

    public boolean hasAbility(CreeperAbility ability) {
        return (abilitiesBitmask.get() & ability.getMask()) != 0;
    }

    @Override
    public void broadcastChanges() {
        if (creeper != null && !creeper.level().isClientSide()) {
            stayingState.set(TamedCreeperCommandState.isStaying(creeper) ? 1 : 0);
            currentHealth.set(Mth.ceil(creeper.getHealth()));
            maxHealth.set(Mth.ceil(creeper.getMaxHealth()));
            abilitiesBitmask.set(CreeperAbilityStorage.getBitmask(creeper));
        }
        super.broadcastChanges();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != FOLLOW_BUTTON_ID || !canUse(player)) {
            return false;
        }

        TamedCreeperCommandState.toggleStaying(creeper);
        creeper.getNavigation().stop();
        creeper.setTarget(null);
        creeper.setSwellDir(-1);
        broadcastChanges();
        return true;
    }

    @Override
    public boolean stillValid(Player player) {
        return canUse(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack sourceCopy = sourceStack.copy();

        if (index == HELMET_SLOT || index == CHESTPLATE_SLOT || index == FUEL_SLOT) {
            if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (isArmorItemForSlot(sourceStack, EquipmentSlot.HEAD)) {
            if (!moveItemStackTo(sourceStack, HELMET_SLOT, HELMET_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (isArmorItemForSlot(sourceStack, EquipmentSlot.CHEST)) {
            if (!moveItemStackTo(sourceStack, CHESTPLATE_SLOT, CHESTPLATE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (CreeperFuelStorage.isFuelItem(sourceStack)) {
            if (!moveItemStackTo(sourceStack, FUEL_SLOT, FUEL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_HOTBAR_START) {
            if (!moveItemStackTo(sourceStack, PLAYER_HOTBAR_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, PLAYER_HOTBAR_START, false)) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return sourceCopy;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != null || slot.index >= PLAYER_INVENTORY_START;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == -999 || slotId >= PLAYER_INVENTORY_START) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (!canUse(player)) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    private boolean canUse(Player player) {
        return creeper != null
                && creeper.isAlive()
                && CreeperState.get(creeper) == CreeperState.TAMED
                && TamedCreeperOwner.isOwner(creeper, player)
                && player.distanceToSqr(creeper) <= 64.0D;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
        }
    }

    private static boolean isArmorItemForSlot(ItemStack stack, EquipmentSlot slot) {
        Equippable equippable = stack.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slot;
    }

    private static Creeper getCreeper(Inventory playerInventory, int creeperId) {
        if (playerInventory.player.level().getEntity(creeperId) instanceof Creeper creeper) {
            return creeper;
        }

        throw new IllegalStateException("Could not find creeper for menu: " + creeperId);
    }

    private final class CreeperArmorSlot extends Slot {
        private final EquipmentSlot equipmentSlot;
        private final ResourceLocation emptyIcon;

        private CreeperArmorSlot(EquipmentSlot equipmentSlot, int index, int x, int y, ResourceLocation emptyIcon) {
            super(new SimpleContainer(2), index, x, y);
            this.equipmentSlot = equipmentSlot;
            this.emptyIcon = emptyIcon;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return isArmorItemForSlot(stack, equipmentSlot);
        }

        @Override
        public boolean mayPickup(Player player) {
            return canUse(player);
        }

        @Override
        public ItemStack getItem() {
            return creeper.getItemBySlot(equipmentSlot);
        }

        @Override
        public boolean hasItem() {
            return !creeper.getItemBySlot(equipmentSlot).isEmpty();
        }

        @Override
        public void set(ItemStack stack) {
            setArmor(stack);
            setChanged();
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            setArmor(newStack);
            setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack equipped = creeper.getItemBySlot(equipmentSlot);
            if (equipped.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }

            ItemStack removed = equipped.copyWithCount(Math.min(amount, equipped.getCount()));
            creeper.setItemSlot(equipmentSlot, ItemStack.EMPTY);
            return removed;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public ResourceLocation getNoItemIcon() {
            return emptyIcon;
        }

        @Override
        public void setChanged() {
            creeper.setPersistenceRequired();
            broadcastChanges();
        }

        private void setArmor(ItemStack stack) {
            ItemStack equipped = stack.copyWithCount(stack.isEmpty() ? 0 : 1);
            creeper.setItemSlot(equipmentSlot, equipped);
            creeper.setDropChance(equipmentSlot, equipped.isEmpty() ? 0.085F : 2.0F);
            creeper.setPersistenceRequired();
        }
    }

    private final class CreeperFuelSlot extends Slot {
        private CreeperFuelSlot(int index, int x, int y) {
            super(new SimpleContainer(1), 0, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return CreeperFuelStorage.isFuelItem(stack);
        }

        @Override
        public boolean mayPickup(Player player) {
            return canUse(player);
        }

        @Override
        public ItemStack getItem() {
            return CreeperFuelStorage.getFuelStack(creeper);
        }

        @Override
        public boolean hasItem() {
            return !CreeperFuelStorage.getFuelStack(creeper).isEmpty();
        }

        @Override
        public void set(ItemStack stack) {
            setFuel(stack);
            setChanged();
        }

        @Override
        public void setByPlayer(ItemStack newStack, ItemStack oldStack) {
            setFuel(newStack);
            setChanged();
        }

        @Override
        public ItemStack remove(int amount) {
            ItemStack fuelStack = CreeperFuelStorage.getFuelStack(creeper);
            if (fuelStack.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }

            int removedAmount = Math.min(amount, fuelStack.getCount());
            ItemStack removed = fuelStack.copyWithCount(removedAmount);
            ItemStack remaining = fuelStack.copy();
            remaining.shrink(removedAmount);
            CreeperFuelStorage.setFuelStack(creeper, remaining);
            return removed;
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public void setChanged() {
            creeper.setPersistenceRequired();
            broadcastChanges();
        }

        private void setFuel(ItemStack stack) {
            ItemStack fuel = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(Math.min(stack.getCount(), getMaxStackSize()));
            CreeperFuelStorage.setFuelStack(creeper, fuel);
            creeper.setPersistenceRequired();
        }
    }
}
