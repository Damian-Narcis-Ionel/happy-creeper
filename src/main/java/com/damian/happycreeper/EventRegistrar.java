package com.damian.happycreeper;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public final class EventRegistrar {
    private EventRegistrar() {}

    public static void register() {
        UseEntityCallback.EVENT.register(CreeperInteractionHandler::onEntityInteract);

        ServerLivingEntityEvents.AFTER_DEATH.register(TamedCreeperCombatHandler::onLivingDeath);

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            CreeperHeadProtectionHandler.onIncomingDamage(entity, source);
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                TamedCreeperFollowHandler.onPlayerRespawn(newPlayer));
    }
}
