package com.damian.happycreeper;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class CreeperInteractionHandler {
    private CreeperInteractionHandler() {
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Creeper creeper)) {
            return;
        }

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }

        Level level = event.getLevel();
        ItemStack stack = player.getItemInHand(hand);
        boolean isChestplate = isArmorItemForSlot(stack, EquipmentSlot.CHEST);
        boolean isHelmet = isArmorItemForSlot(stack, EquipmentSlot.HEAD);
        boolean isArmorPiece = isChestplate || isHelmet;
        boolean isPotion = isPotionItem(stack);
        boolean isShiftEmptyHand = player.isShiftKeyDown() && stack.isEmpty();
        if (level.isClientSide()) {
            boolean looksTamed = TamedCreeperAppearance.getVariant(creeper) != TamedCreeperAppearance.NONE_VARIANT;
            if (looksTamed && (stack.is(Items.LEAD) || isArmorPiece || isPotion || isShiftEmptyHand)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
            return;
        }

        CreeperState currentState = CreeperState.get(creeper);
        boolean isSweetBiscuit = stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT.get());
        boolean isHealingGunpowder = stack.is(Items.GUNPOWDER);
        boolean isRainbowBiscuit = stack.is(HappyCreeper.RAINBOW_BISCUIT.get());
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

        if (stack.is(HappyCreeper.ANTI_BLAST_BISCUIT.get())) {
            if (!isWearingCreeperHead(player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.need_creeper_head")
                        .withStyle(ChatFormatting.RED), true);
                event.setCanceled(true);
                return;
            }

            if (currentState.isAtLeastWeakened()) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_weakened")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            consumeItem(player, stack);
            CreeperState.set(creeper, CreeperState.WEAKENED);
            creeper.setPersistenceRequired();
            sendFeedback(player, creeper, "message.happycreeper.creeper_weakened");
            event.setCanceled(true);
            return;
        }

        if (isLead) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
            }
            return;
        }

        if (isPotion) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (applyPotionToCreeper(player, creeper, stack, potionContents)) {
                event.setCanceled(true);
            }
            return;
        }

        if (isArmorPiece) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            openCreeperMenu(creeper, player);
            event.setCanceled(true);
            return;
        }

        if (!isSweetBiscuit && !isHealingGunpowder && !isRainbowBiscuit && !isBlueDye && !isCyanDye && !isGrayDye && !isLimeDye && !isYellowDye && !isPinkDye && !isPurpleDye && !isRedDye && !isBlackDye) {
            if (currentState == CreeperState.TAMED && stack.isEmpty() && TamedCreeperOwner.isOwner(creeper, player)) {
                if (creeper.isLeashed() && creeper.getLeashHolder() == player) {
                    return;
                }

                if (player.isShiftKeyDown()) {
                    boolean staying = TamedCreeperCommandState.toggleStaying(creeper);
                    player.displayClientMessage(Component.translatable(staying
                            ? "message.happycreeper.creeper_staying"
                            : "message.happycreeper.creeper_following").withStyle(ChatFormatting.AQUA), true);
                } else {
                    openCreeperMenu(creeper, player);
                }
                event.setCanceled(true);
            }
            return;
        }

        if (currentState == CreeperState.NORMAL) {
            player.displayClientMessage(Component.translatable("message.happycreeper.need_to_weaken_first")
                    .withStyle(ChatFormatting.RED), true);
            event.setCanceled(true);
            return;
        }

        if (isRainbowBiscuit) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            if (TamedCreeperAppearance.getVariant(creeper) == TamedCreeperAppearance.RAINBOW_VARIANT) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_same_color")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            consumeItem(player, stack);
            TamedCreeperAppearance.setVariant(creeper, TamedCreeperAppearance.RAINBOW_VARIANT);
            sendFeedback(player, creeper, "message.happycreeper.creeper_recolored_rainbow");
            event.setCanceled(true);
            return;
        }

        if (isBlueDye || isCyanDye || isGrayDye || isLimeDye || isYellowDye || isPinkDye || isPurpleDye || isRedDye || isBlackDye) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            int variant;
            String messageKey;
            if (isBlueDye) {
                variant = TamedCreeperAppearance.BLUE_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_blue";
            } else if (isCyanDye) {
                variant = TamedCreeperAppearance.CYAN_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_cyan";
            } else if (isGrayDye) {
                variant = TamedCreeperAppearance.GRAY_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_gray";
            } else if (isLimeDye) {
                variant = TamedCreeperAppearance.HAPPY_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_happy";
            } else if (isYellowDye) {
                variant = TamedCreeperAppearance.YELLOW_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_yellow";
            } else if (isPinkDye) {
                variant = TamedCreeperAppearance.PINK_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_pink";
            } else if (isPurpleDye) {
                variant = TamedCreeperAppearance.PURPLE_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_purple";
            } else if (isBlackDye) {
                variant = TamedCreeperAppearance.BLACK_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_black";
            } else {
                variant = TamedCreeperAppearance.RED_VARIANT;
                messageKey = "message.happycreeper.creeper_recolored_red";
            }

            if (TamedCreeperAppearance.getVariant(creeper) == variant) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_same_color")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            TamedCreeperAppearance.setVariant(creeper, variant);
            consumeItem(player, stack);
            sendFeedback(player, creeper, messageKey);
            event.setCanceled(true);
            return;
        }

        if (isHealingGunpowder) {
            if (currentState != CreeperState.TAMED) {
                return;
            }

            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            if (creeper.getHealth() >= creeper.getMaxHealth()) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_full_health")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            consumeItem(player, stack);
            creeper.heal(2.0F);
            sendFeedback(player, creeper, "message.happycreeper.creeper_healed");
            event.setCanceled(true);
            return;
        }

        if (currentState == CreeperState.TAMED) {
            if (!TamedCreeperOwner.isOwner(creeper, player)) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            if (creeper.getHealth() >= creeper.getMaxHealth()) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_full_health")
                        .withStyle(ChatFormatting.YELLOW), true);
                event.setCanceled(true);
                return;
            }

            consumeItem(player, stack);
            creeper.heal(4.0F);
            sendFeedback(player, creeper, "message.happycreeper.creeper_healed");
            event.setCanceled(true);
            return;
        }

        consumeItem(player, stack);
        CreeperState.set(creeper, CreeperState.TAMED);
        creeper.setPersistenceRequired();
        TamedCreeperOwner.setOwner(creeper, player.getUUID());
        TamedCreeperCommandState.setStaying(creeper, false);
        TamedCreeperAppearance.setVariant(creeper, TamedCreeperAppearance.HAPPY_VARIANT);
        sendFeedback(player, creeper, "message.happycreeper.creeper_tamed");
        event.setCanceled(true);
    }

    private static void consumeItem(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    private static boolean isArmorItemForSlot(ItemStack stack, EquipmentSlot slot) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null || equippable.slot() != slot) {
            return false;
        }

        return switch (slot) {
            case HEAD -> stack.is(ItemTags.HEAD_ARMOR);
            case CHEST -> stack.is(ItemTags.CHEST_ARMOR);
            default -> false;
        };
    }

    private static boolean isPotionItem(ItemStack stack) {
        return stack.is(Items.POTION);
    }

    private static boolean applyPotionToCreeper(Player player, Creeper creeper, ItemStack stack, PotionContents potionContents) {
        boolean[] applied = {false};
        potionContents.forEachEffect(effect -> {
            applyPotionEffect(player, creeper, effect);
            applied[0] = true;
        }, 1.0F);

        if (!applied[0]) {
            player.displayClientMessage(Component.translatable("message.happycreeper.potion_no_effect")
                    .withStyle(ChatFormatting.YELLOW), true);
            return true;
        }

        ItemStack remainder = stack.getItem().getCraftingRemainder();
        if (remainder.isEmpty() && stack.is(Items.POTION)) {
            remainder = new ItemStack(Items.GLASS_BOTTLE);
        }

        ItemStack resultStack = remainder.isEmpty() ? ItemStack.EMPTY : remainder.copy();
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemUtils.createFilledResult(stack, player, resultStack));
        sendFeedback(player, creeper, "message.happycreeper.creeper_drank_potion");
        return true;
    }

    private static void applyPotionEffect(Player player, LivingEntity target, MobEffectInstance effect) {
        if (effect.getEffect().value().isInstantenous()) {
            if (player.level() instanceof ServerLevel serverLevel) {
                effect.getEffect().value().applyInstantenousEffect(serverLevel, player, player, target, effect.getAmplifier(), 1.0D);
            }
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
            serverLevel.sendParticles(particleType,
                    creeper.getX(),
                    creeper.getY() + 1.0D,
                    creeper.getZ(),
                    10,
                    0.3D,
                    0.4D,
                    0.3D,
                    0.01D);
        }
    }

    private static boolean isWearingCreeperHead(Player player) {
        return HappyCreeper.isCreeperDisguise(player.getItemBySlot(EquipmentSlot.HEAD));
    }

    private static void openCreeperMenu(Creeper creeper, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        serverPlayer.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, menuPlayer) -> new com.damian.happycreeper.menu.CreeperMenu(containerId, inventory, creeper),
                        Component.translatable("screen.happycreeper.creeper.title")),
                buffer -> buffer.writeInt(creeper.getId()));
    }
}
