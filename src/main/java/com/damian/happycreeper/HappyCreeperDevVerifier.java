package com.damian.happycreeper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public final class HappyCreeperDevVerifier {
    private static final double SEARCH_RADIUS = 16.0D;

    private HappyCreeperDevVerifier() {}

    public static int verifyNearest(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("This command can only be used by a player."));
            return 0;
        }

        Optional<Creeper> nearest = player.level()
                .getEntitiesOfClass(Creeper.class, new AABB(player.blockPosition()).inflate(SEARCH_RADIUS))
                .stream()
                .min(Comparator.comparingDouble((Creeper creeper) -> player.distanceToSqr(creeper)));
        if (nearest.isEmpty()) {
            source.sendFailure(Component.literal("No creeper found within 16 blocks."));
            return 0;
        }

        return sendVerificationReport(source, player, nearest.get());
    }

    public static int verifyById(CommandSourceStack source, int entityId) {
        Entity entity = source.getLevel().getEntity(entityId);
        if (!(entity instanceof Creeper creeper)) {
            source.sendFailure(Component.literal("Entity " + entityId + " is not a creeper in this level."));
            return 0;
        }

        return sendVerificationReport(source, source.getPlayer(), creeper);
    }

    private static int sendVerificationReport(CommandSourceStack source, ServerPlayer player, Creeper creeper) {
        CreeperState state = CreeperState.get(creeper);
        Optional<UUID> ownerUuid = TamedCreeperOwner.getOwnerUuid(creeper);
        Optional<String> ownerName = TamedCreeperOwner.getOwnerName(creeper);
        boolean isOwner = player != null && TamedCreeperOwner.isOwner(creeper, player);
        boolean uiShouldOpen = player != null && state == CreeperState.TAMED && isOwner;
        int variant = TamedCreeperAppearance.getVariant(creeper);
        String abilities = Arrays.stream(CreeperAbility.values())
                .filter(ability -> CreeperAbilityStorage.hasAbility(creeper, ability))
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        ItemStack fuelStack = CreeperFuelStorage.getFuelStack(creeper);
        boolean staying = TamedCreeperCommandState.isStaying(creeper);

        source.sendSuccess(() -> Component.literal("Happy Creeper verify")
                .withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("Entity id: " + creeper.getId() + ", UUID: " + creeper.getUUID()), false);
        source.sendSuccess(() -> Component.literal("State: " + state.name()), false);
        source.sendSuccess(() -> Component.literal("Owner UUID: " + ownerUuid.map(UUID::toString).orElse("<missing>")), false);
        source.sendSuccess(() -> Component.literal("Owner name: " + ownerName.orElse("<missing>")), false);
        if (player != null) {
            source.sendSuccess(() -> Component.literal("Player UUID: " + player.getUUID()), false);
            source.sendSuccess(() -> Component.literal("Player name: " + player.getScoreboardName()), false);
            source.sendSuccess(() -> Component.literal("Owner match: " + yesNo(isOwner)), false);
            source.sendSuccess(() -> Component.literal("UI access for current player: " + yesNo(uiShouldOpen)), false);
        }
        source.sendSuccess(() -> Component.literal("Color variant: " + variant), false);
        source.sendSuccess(() -> Component.literal("Staying: " + yesNo(staying)), false);
        source.sendSuccess(() -> Component.literal("Abilities: " + (abilities.isBlank() ? "<none>" : abilities)), false);
        source.sendSuccess(() -> Component.literal("Fuel: " + describeItemStack(fuelStack)), false);
        source.sendSuccess(() -> Component.literal("Health: " + creeper.getHealth() + "/" + creeper.getMaxHealth()), false);

        return 1;
    }

    private static String yesNo(boolean value) {
        return value ? "yes" : "no";
    }

    private static String describeItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        return stack.getCount() + "x " + stack.getDisplayName().getString();
    }
}
