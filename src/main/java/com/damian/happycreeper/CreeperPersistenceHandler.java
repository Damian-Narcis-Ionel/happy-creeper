package com.damian.happycreeper;

import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class CreeperPersistenceHandler {
    private CreeperPersistenceHandler() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        if (!CreeperState.get(creeper).isAtLeastWeakened()) {
            return;
        }

        if (!creeper.isPersistenceRequired()) {
            creeper.setPersistenceRequired();
        }
    }
}
