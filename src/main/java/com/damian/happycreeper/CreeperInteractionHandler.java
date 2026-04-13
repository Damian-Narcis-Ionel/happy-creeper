package com.damian.happycreeper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

        Level level = event.getLevel();
        if (level.isClientSide()) {
            return;
        }

        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }

        ItemStack stack = player.getItemInHand(hand);
        CreeperState currentState = CreeperState.get(creeper);
        boolean isSweetBiscuit = stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT.get());
        boolean isHealingGunpowder = stack.is(Items.GUNPOWDER);
        boolean isRainbowBiscuit = stack.is(HappyCreeper.RAINBOW_BISCUIT.get());
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
            sendFeedback(player, creeper, "message.happycreeper.creeper_weakened");
            event.setCanceled(true);
            return;
        }

        if (!isSweetBiscuit && !isHealingGunpowder && !isRainbowBiscuit && !isBlueDye && !isCyanDye && !isGrayDye && !isLimeDye && !isYellowDye && !isPinkDye && !isPurpleDye && !isRedDye && !isBlackDye) {
            if (currentState == CreeperState.TAMED && stack.isEmpty() && TamedCreeperOwner.isOwner(creeper, player)) {
                boolean staying = TamedCreeperCommandState.toggleStaying(creeper);
                player.displayClientMessage(Component.translatable(staying
                        ? "message.happycreeper.creeper_staying"
                        : "message.happycreeper.creeper_following").withStyle(ChatFormatting.AQUA), true);
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

            consumeItem(player, stack);
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
            TamedCreeperAppearance.setVariant(creeper, variant);
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
}
