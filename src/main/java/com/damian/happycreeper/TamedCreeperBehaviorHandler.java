package com.damian.happycreeper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperBehaviorHandler {
    private TamedCreeperBehaviorHandler() {
    }

    @SubscribeEvent
    public static void onCreeperTargetChange(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            return;
        }

        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (newTarget instanceof Player) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED) {
            return;
        }

        if (!(creeper.getTarget() instanceof Player player)) {
            return;
        }

        if (TamedCreeperOwner.hasOwner(creeper) && TamedCreeperOwner.isOwner(creeper, player)) {
            clearAggression(creeper);
            return;
        }

        clearAggression(creeper);
    }

    private static void clearAggression(Creeper creeper) {
        creeper.setTarget(null);
        creeper.setSwellDir(-1);
    }
}
