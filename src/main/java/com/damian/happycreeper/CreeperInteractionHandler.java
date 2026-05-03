package com.damian.happycreeper;

import com.damian.happycreeper.menu.CreeperMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public final class CreeperInteractionHandler {
    private CreeperInteractionHandler() {}

    static InteractionResult onEntityInteract(Player player, Level level, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
        if (!(entity instanceof Creeper creeper)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        ItemStack stack = player.getItemInHand(hand);
        boolean isArmorPiece = isArmorItemForSlot(stack, EquipmentSlot.CHEST) || isArmorItemForSlot(stack, EquipmentSlot.HEAD);
        boolean isPotion = stack.is(Items.POTION);

        if (level.isClientSide()) {
            return clientInteractionResult(creeper, stack, isArmorPiece, isPotion);
        }

        CreeperState currentState = CreeperState.get(creeper);
        boolean isSweetBiscuit = stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT);
        boolean isLavaBiscuit = stack.is(HappyCreeper.LAVA_BISCUIT);
        boolean isFishBiscuit = stack.is(HappyCreeper.FISH_BISCUIT);
        boolean isExtremeBlastBiscuit = stack.is(HappyCreeper.EXTREME_BLAST_BISCUIT);
        boolean isSlimeBiscuit = stack.is(HappyCreeper.SLIME_BISCUIT);
        boolean isHealingGunpowder = stack.is(Items.GUNPOWDER);
        boolean isRainbowBiscuit = stack.is(HappyCreeper.RAINBOW_BISCUIT);
        boolean isLead = stack.is(Items.LEAD);
        boolean isBlueDye = stack.is(Items.BLUE_DYE);
        boolean isCyanDye = stack.is(Items.CYAN_DYE);
        boolean isGrayDye = stack.is(Items.GRAY_DYE);
        boolean isLimeDye = stack.is(Items.LIME_DYE);
        boolean isYellowDye = stack.is(Items.YELLOW_DYE);
        boolean isPinkDye = stack.is(Items.PINK_DYE);
        boolean isPurpleDye = stack.is(Items.PURPLE_DYE);
        boolean isRedDye = stack.is(Items.RED_DYE);
        boolean isBlackDye = stack.is(Items.BLACK_DYE);

        if (stack.is(HappyCreeper.ANTI_BLAST_BISCUIT)) {
            return handleAntiBlastBiscuit(player, creeper, stack, currentState);
        }

        if (isLead) {
            if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
            if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
            return InteractionResult.PASS;
        }

        if (isPotion) {
            if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
            if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
            PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            return applyPotionToCreeper(player, creeper, stack, potionContents) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        if (isArmorPiece) {
            if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
            if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
            openCreeperMenu(creeper, player);
            return InteractionResult.SUCCESS;
        }

        if (!isSweetBiscuit && !isHealingGunpowder && !isRainbowBiscuit && !isLavaBiscuit && !isFishBiscuit
                && !isExtremeBlastBiscuit && !isSlimeBiscuit && !isBlueDye && !isCyanDye && !isGrayDye
                && !isLimeDye && !isYellowDye && !isPinkDye && !isPurpleDye && !isRedDye && !isBlackDye) {
            return handleEmptyHandInteraction(player, creeper, stack, currentState);
        }

        if (currentState == CreeperState.NORMAL) {
            player.displayClientMessage(Component.translatable("message.happycreeper.need_to_weaken_first").withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        }

        if (isRainbowBiscuit) {
            return handleRainbowBiscuit(player, creeper, stack, currentState);
        }

        if (isLavaBiscuit || isFishBiscuit || isExtremeBlastBiscuit || isSlimeBiscuit) {
            return handleAbilityBiscuit(player, creeper, stack, currentState, isLavaBiscuit, isFishBiscuit, isSlimeBiscuit);
        }

        if (isBlueDye || isCyanDye || isGrayDye || isLimeDye || isYellowDye || isPinkDye || isPurpleDye || isRedDye || isBlackDye) {
            return handleDye(player, creeper, stack, currentState, isBlueDye, isCyanDye, isGrayDye, isLimeDye, isYellowDye, isPinkDye, isPurpleDye, isRedDye, isBlackDye);
        }

        if (isHealingGunpowder) {
            return handleHealingGunpowder(player, creeper, stack, currentState);
        }

        if (currentState == CreeperState.TAMED) {
            return handleSweetBiscuitHealing(player, creeper, stack);
        }

        return handleTaming(player, creeper, stack);
    }

    private static InteractionResult clientInteractionResult(Creeper creeper, ItemStack stack, boolean isArmorPiece, boolean isPotion) {
        boolean looksTamed = TamedCreeperAppearance.getVariant(creeper) != TamedCreeperAppearance.NONE_VARIANT;
        boolean isModBiscuit = stack.is(HappyCreeper.ANTI_BLAST_BISCUIT)
                || stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT)
                || stack.is(HappyCreeper.RAINBOW_BISCUIT)
                || stack.is(HappyCreeper.LAVA_BISCUIT)
                || stack.is(HappyCreeper.FISH_BISCUIT)
                || stack.is(HappyCreeper.EXTREME_BLAST_BISCUIT)
                || stack.is(HappyCreeper.SLIME_BISCUIT);
        boolean isDye = isSupportedDye(stack);
        if (stack.isEmpty() || isModBiscuit || (looksTamed && (stack.is(Items.LEAD) || isArmorPiece || isPotion
                || stack.is(Items.GUNPOWDER) || isDye))) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult handleAntiBlastBiscuit(Player player, Creeper creeper, ItemStack stack, CreeperState currentState) {
        if (!isWearingCreeperHead(player)) {
            player.displayClientMessage(Component.translatable("message.happycreeper.need_creeper_head").withStyle(ChatFormatting.RED), true);
            return InteractionResult.SUCCESS;
        }
        if (currentState.isAtLeastWeakened()) {
            player.displayClientMessage(Component.translatable("message.happycreeper.already_weakened").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        consumeItem(player, stack);
        CreeperState.set(creeper, CreeperState.WEAKENED);
        creeper.setPersistenceRequired();
        sendFeedback(player, creeper, "message.happycreeper.creeper_weakened");
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleEmptyHandInteraction(Player player, Creeper creeper, ItemStack stack, CreeperState currentState) {
        if (currentState != CreeperState.TAMED || !stack.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!TamedCreeperOwner.isOwner(creeper, player)) {
            return InteractionResult.PASS;
        }
        if (creeper.isLeashed() && creeper.getLeashHolder() == player) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            boolean staying = TamedCreeperCommandState.toggleStaying(creeper);
            player.displayClientMessage(Component.translatable(staying
                    ? "message.happycreeper.creeper_staying"
                    : "message.happycreeper.creeper_following").withStyle(ChatFormatting.AQUA), true);
        } else {
            openCreeperMenu(creeper, player);
        }
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleRainbowBiscuit(Player player, Creeper creeper, ItemStack stack, CreeperState currentState) {
        if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
        if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
        if (TamedCreeperAppearance.getVariant(creeper) == TamedCreeperAppearance.RAINBOW_VARIANT) {
            player.displayClientMessage(Component.translatable("message.happycreeper.already_same_color").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        consumeItem(player, stack);
        TamedCreeperAppearance.setVariant(creeper, TamedCreeperAppearance.RAINBOW_VARIANT);
        sendFeedback(player, creeper, "message.happycreeper.creeper_recolored_rainbow");
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleAbilityBiscuit(Player player, Creeper creeper, ItemStack stack, CreeperState currentState,
                                                           boolean isLavaBiscuit, boolean isFishBiscuit, boolean isSlimeBiscuit) {
        if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
        if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
        CreeperAbility ability = isLavaBiscuit ? CreeperAbility.FIRE_RESISTANCE
                : isFishBiscuit ? CreeperAbility.SWIM_SPEED
                : isSlimeBiscuit ? CreeperAbility.SLIME_JUMP
                : CreeperAbility.EXTREME_BLAST;
        if (CreeperAbilityStorage.hasAbility(creeper, ability)) {
            player.displayClientMessage(Component.translatable("message.happycreeper.ability_already_granted").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        consumeItem(player, stack);
        CreeperAbilityStorage.grantAbility(creeper, ability);
        String messageKey = isLavaBiscuit ? "message.happycreeper.ability_fire_resistance"
                : isFishBiscuit ? "message.happycreeper.ability_swim_speed"
                : isSlimeBiscuit ? "message.happycreeper.ability_slime_jump"
                : "message.happycreeper.ability_extreme_blast";
        sendFeedback(player, creeper, messageKey);
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleDye(Player player, Creeper creeper, ItemStack stack, CreeperState currentState,
                                               boolean isBlueDye, boolean isCyanDye, boolean isGrayDye, boolean isLimeDye,
                                               boolean isYellowDye, boolean isPinkDye, boolean isPurpleDye, boolean isRedDye, boolean isBlackDye) {
        if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
        if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
        int variant;
        String messageKey;
        if (isBlueDye)        { variant = TamedCreeperAppearance.BLUE_VARIANT;   messageKey = "message.happycreeper.creeper_recolored_blue"; }
        else if (isCyanDye)   { variant = TamedCreeperAppearance.CYAN_VARIANT;   messageKey = "message.happycreeper.creeper_recolored_cyan"; }
        else if (isGrayDye)   { variant = TamedCreeperAppearance.GRAY_VARIANT;   messageKey = "message.happycreeper.creeper_recolored_gray"; }
        else if (isLimeDye)   { variant = TamedCreeperAppearance.HAPPY_VARIANT;  messageKey = "message.happycreeper.creeper_recolored_happy"; }
        else if (isYellowDye) { variant = TamedCreeperAppearance.YELLOW_VARIANT; messageKey = "message.happycreeper.creeper_recolored_yellow"; }
        else if (isPinkDye)   { variant = TamedCreeperAppearance.PINK_VARIANT;   messageKey = "message.happycreeper.creeper_recolored_pink"; }
        else if (isPurpleDye) { variant = TamedCreeperAppearance.PURPLE_VARIANT; messageKey = "message.happycreeper.creeper_recolored_purple"; }
        else if (isBlackDye)  { variant = TamedCreeperAppearance.BLACK_VARIANT;  messageKey = "message.happycreeper.creeper_recolored_black"; }
        else                  { variant = TamedCreeperAppearance.RED_VARIANT;    messageKey = "message.happycreeper.creeper_recolored_red"; }
        if (TamedCreeperAppearance.getVariant(creeper) == variant) {
            player.displayClientMessage(Component.translatable("message.happycreeper.already_same_color").withStyle(ChatFormatting.YELLOW), true);
            return InteractionResult.SUCCESS;
        }
        TamedCreeperAppearance.setVariant(creeper, variant);
        consumeItem(player, stack);
        sendFeedback(player, creeper, messageKey);
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleHealingGunpowder(Player player, Creeper creeper, ItemStack stack, CreeperState currentState) {
        if (currentState != CreeperState.TAMED) return InteractionResult.PASS;
        if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
        if (!canHeal(creeper, player)) return InteractionResult.SUCCESS;
        consumeItem(player, stack);
        creeper.heal(2.0F);
        sendFeedback(player, creeper, "message.happycreeper.creeper_healed");
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleSweetBiscuitHealing(Player player, Creeper creeper, ItemStack stack) {
        if (!requireOwner(creeper, player)) return InteractionResult.SUCCESS;
        if (!canHeal(creeper, player)) return InteractionResult.SUCCESS;
        consumeItem(player, stack);
        creeper.heal(4.0F);
        sendFeedback(player, creeper, "message.happycreeper.creeper_healed");
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleTaming(Player player, Creeper creeper, ItemStack stack) {
        consumeItem(player, stack);
        CreeperState.set(creeper, CreeperState.TAMED);
        creeper.setPersistenceRequired();
        TamedCreeperOwner.setOwner(creeper, player);
        TamedCreeperCommandState.setStaying(creeper, false);
        TamedCreeperAppearance.setVariant(creeper, TamedCreeperAppearance.HAPPY_VARIANT);
        sendFeedback(player, creeper, "message.happycreeper.creeper_tamed");
        return InteractionResult.SUCCESS;
    }

    private static boolean requireOwner(Creeper creeper, Player player) {
        if (TamedCreeperOwner.isOwner(creeper, player)) {
            return true;
        }
        player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed").withStyle(ChatFormatting.YELLOW), true);
        return false;
    }

    private static boolean canHeal(Creeper creeper, Player player) {
        if (creeper.getHealth() >= creeper.getMaxHealth()) {
            player.displayClientMessage(Component.translatable("message.happycreeper.already_full_health").withStyle(ChatFormatting.YELLOW), true);
            return false;
        }
        return true;
    }

    private static boolean isSupportedDye(ItemStack stack) {
        return stack.is(Items.BLUE_DYE) || stack.is(Items.CYAN_DYE) || stack.is(Items.GRAY_DYE)
                || stack.is(Items.LIME_DYE) || stack.is(Items.YELLOW_DYE) || stack.is(Items.PINK_DYE)
                || stack.is(Items.PURPLE_DYE) || stack.is(Items.RED_DYE) || stack.is(Items.BLACK_DYE);
    }

    private static void consumeItem(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) stack.shrink(1);
    }

    private static boolean isArmorItemForSlot(ItemStack stack, EquipmentSlot slot) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.slot() == slot;
    }

    private static boolean applyPotionToCreeper(Player player, Creeper creeper, ItemStack stack, PotionContents potionContents) {
        boolean[] applied = {false};
        potionContents.forEachEffect(effect -> {
            applyPotionEffect(player, creeper, effect);
            applied[0] = true;
        });
        if (!applied[0]) {
            player.displayClientMessage(Component.translatable("message.happycreeper.potion_no_effect").withStyle(ChatFormatting.YELLOW), true);
            return true;
        }
        ItemStack remainder = stack.is(Items.POTION) ? new ItemStack(Items.GLASS_BOTTLE) : ItemStack.EMPTY;
        player.setItemInHand(InteractionHand.MAIN_HAND,
                ItemUtils.createFilledResult(stack, player, remainder.isEmpty() ? ItemStack.EMPTY : remainder.copy()));
        sendFeedback(player, creeper, "message.happycreeper.creeper_drank_potion");
        return true;
    }

    private static void applyPotionEffect(Player player, LivingEntity target, MobEffectInstance effect) {
        if (effect.getEffect().value().isInstantenous()) {
            effect.getEffect().value().applyInstantenousEffect(
                    (ServerLevel) player.level(), player, player, target, effect.getAmplifier(), 1.0D);
            return;
        }
        target.addEffect(new MobEffectInstance(effect));
    }

    private static void sendFeedback(Player player, Creeper creeper, String messageKey) {
        player.displayClientMessage(Component.translatable(messageKey).withStyle(ChatFormatting.GREEN), true);
        if (player.level() instanceof ServerLevel serverLevel) {
            var particleType = "message.happycreeper.creeper_tamed".equals(messageKey)
                    ? net.minecraft.core.particles.ParticleTypes.HEART
                    : net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER;
            serverLevel.sendParticles(particleType, creeper.getX(), creeper.getY() + 1.0D, creeper.getZ(),
                    10, 0.3D, 0.4D, 0.3D, 0.01D);
        }
    }

    private static boolean isWearingCreeperHead(Player player) {
        return HappyCreeper.isCreeperDisguise(player.getItemBySlot(EquipmentSlot.HEAD));
    }

    private static void openCreeperMenu(Creeper creeper, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        serverPlayer.openMenu(new ExtendedScreenHandlerFactory<Integer>() {
            @Override
            public Integer getScreenOpeningData(ServerPlayer p) {
                return creeper.getId();
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("screen.happycreeper.creeper.title");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player p) {
                return new CreeperMenu(syncId, inventory, creeper);
            }
        });
    }
}
