package com.damian.happycreeper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        ItemStack stack = player.getItemInHand(hand);
        CreeperState currentState = CreeperState.get(creeper);

        if (stack.is(HappyCreeper.ANTI_BLAST_BISCUIT.get())) {
            if (currentState.isAtLeastWeakened()) {
                player.displayClientMessage(Component.translatable("message.happycreeper.already_weakened")
                        .withStyle(ChatFormatting.YELLOW), true);
                return;
            }

            consumeItem(player, stack);
            CreeperState.set(creeper, CreeperState.WEAKENED);
            sendFeedback(player, creeper, "message.happycreeper.creeper_weakened");
            event.setCanceled(true);
            return;
        }

        if (!stack.is(HappyCreeper.SWEET_GUNPOWDER_BISCUIT.get())) {
            return;
        }

        if (currentState == CreeperState.NORMAL) {
            player.displayClientMessage(Component.translatable("message.happycreeper.need_to_weaken_first")
                    .withStyle(ChatFormatting.RED), true);
            event.setCanceled(true);
            return;
        }

        if (currentState == CreeperState.TAMED) {
            player.displayClientMessage(Component.translatable("message.happycreeper.already_tamed")
                    .withStyle(ChatFormatting.YELLOW), true);
            event.setCanceled(true);
            return;
        }

        consumeItem(player, stack);
        CreeperState.set(creeper, CreeperState.TAMED);
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
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER,
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
}
