package com.damian.happycreeper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class HappyCreeperDevCommands {
    private HappyCreeperDevCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("happycreeper")
                .then(Commands.literal("verify")
                        .then(Commands.literal("nearest")
                                .executes(context -> HappyCreeperDevVerifier.verifyNearest(context.getSource())))
                        .then(Commands.literal("id")
                                .then(Commands.argument("entityId", IntegerArgumentType.integer(0))
                                        .executes(context -> HappyCreeperDevVerifier.verifyById(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "entityId")))))));
    }
}
