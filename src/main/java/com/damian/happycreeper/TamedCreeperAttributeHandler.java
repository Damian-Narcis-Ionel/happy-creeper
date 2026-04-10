package com.damian.happycreeper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = HappyCreeper.MODID)
public final class TamedCreeperAttributeHandler {
    private static final ResourceLocation TAMED_HEALTH_BONUS_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_health_bonus");
    private static final AttributeModifier TAMED_HEALTH_BONUS = new AttributeModifier(TAMED_HEALTH_BONUS_ID, 20.0D, Operation.ADD_VALUE);

    private TamedCreeperAttributeHandler() {
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Creeper creeper)) {
            return;
        }

        AttributeInstance maxHealth = creeper.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }

        if (CreeperState.get(creeper) == CreeperState.TAMED) {
            applyTamedHealthBonus(creeper, maxHealth);
            return;
        }

        if (maxHealth.removeModifier(TAMED_HEALTH_BONUS_ID) && creeper.getHealth() > creeper.getMaxHealth()) {
            creeper.setHealth(creeper.getMaxHealth());
        }
    }

    private static void applyTamedHealthBonus(Creeper creeper, AttributeInstance maxHealth) {
        if (maxHealth.hasModifier(TAMED_HEALTH_BONUS_ID)) {
            return;
        }

        maxHealth.addOrReplacePermanentModifier(TAMED_HEALTH_BONUS);
        creeper.setHealth(Math.min(creeper.getHealth() + (float) TAMED_HEALTH_BONUS.amount(), creeper.getMaxHealth()));
    }
}
