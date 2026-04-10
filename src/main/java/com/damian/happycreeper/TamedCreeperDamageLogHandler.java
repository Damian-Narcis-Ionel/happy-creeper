package com.damian.happycreeper;

import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperDamageLogHandler {
    private TamedCreeperDamageLogHandler() {
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (CreeperState.get(creeper) != CreeperState.TAMED || !TamedCreeperOwner.hasOwner(creeper)) {
            return;
        }

        TamedCreeperLogHelper.logDamage(creeper, event.getSource(), event.getNewDamage());
    }
}
