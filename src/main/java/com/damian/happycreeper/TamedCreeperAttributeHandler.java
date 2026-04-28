package com.damian.happycreeper;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;

public final class TamedCreeperAttributeHandler {
    private static final ResourceLocation TAMED_HEALTH_BONUS_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_health_bonus");
    private static final ResourceLocation TAMED_ARMOR_BONUS_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_armor_bonus");
    private static final ResourceLocation TAMED_TOUGHNESS_BONUS_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_toughness_bonus");
    private static final ResourceLocation TAMED_KNOCKBACK_BONUS_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_knockback_bonus");
    private static final ResourceLocation LEGACY_ARMOR_NORMALIZATION_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_armor_normalization");
    private static final ResourceLocation LEGACY_TOUGHNESS_NORMALIZATION_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_toughness_normalization");
    private static final ResourceLocation LEGACY_KNOCKBACK_NORMALIZATION_ID = ResourceLocation.fromNamespaceAndPath(HappyCreeper.MODID, "tamed_knockback_normalization");
    private static final AttributeModifier TAMED_HEALTH_BONUS = new AttributeModifier(TAMED_HEALTH_BONUS_ID, 20.0D, Operation.ADD_VALUE);

    private TamedCreeperAttributeHandler() {}

    public static void tick(Creeper creeper) {
        AttributeInstance maxHealth = creeper.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        AttributeInstance armor = creeper.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughness = creeper.getAttribute(Attributes.ARMOR_TOUGHNESS);
        AttributeInstance knockbackResistance = creeper.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        clearLegacyArmorNormalization(creeper);
        if (CreeperState.get(creeper) == CreeperState.TAMED) {
            applyTamedHealthBonus(creeper, maxHealth);
            applyArmorPieceBonuses(creeper, armor, armorToughness, knockbackResistance);
            return;
        }
        removeBonusModifier(armor, TAMED_ARMOR_BONUS_ID);
        removeBonusModifier(armorToughness, TAMED_TOUGHNESS_BONUS_ID);
        removeBonusModifier(knockbackResistance, TAMED_KNOCKBACK_BONUS_ID);
        if (maxHealth.removeModifier(TAMED_HEALTH_BONUS_ID) && creeper.getHealth() > creeper.getMaxHealth()) {
            creeper.setHealth(creeper.getMaxHealth());
        }
    }

    private static void applyTamedHealthBonus(Creeper creeper, AttributeInstance maxHealth) {
        if (maxHealth.hasModifier(TAMED_HEALTH_BONUS_ID)) return;
        maxHealth.addOrReplacePermanentModifier(TAMED_HEALTH_BONUS);
        creeper.setHealth(Math.min(creeper.getHealth() + (float)TAMED_HEALTH_BONUS.amount(), creeper.getMaxHealth()));
    }

    private static void clearLegacyArmorNormalization(Creeper creeper) {
        removeLegacyModifier(creeper.getAttribute(Attributes.ARMOR), LEGACY_ARMOR_NORMALIZATION_ID);
        removeLegacyModifier(creeper.getAttribute(Attributes.ARMOR_TOUGHNESS), LEGACY_TOUGHNESS_NORMALIZATION_ID);
        removeLegacyModifier(creeper.getAttribute(Attributes.KNOCKBACK_RESISTANCE), LEGACY_KNOCKBACK_NORMALIZATION_ID);
    }

    private static void applyArmorPieceBonuses(Creeper creeper, AttributeInstance armor, AttributeInstance armorToughness, AttributeInstance knockbackResistance) {
        double armorBonus = getEquippedAttributeValue(creeper, Attributes.ARMOR);
        double toughnessBonus = getEquippedAttributeValue(creeper, Attributes.ARMOR_TOUGHNESS);
        double knockbackBonus = getEquippedAttributeValue(creeper, Attributes.KNOCKBACK_RESISTANCE);
        applyOrRemoveBonusModifier(armor, TAMED_ARMOR_BONUS_ID, armorBonus);
        applyOrRemoveBonusModifier(armorToughness, TAMED_TOUGHNESS_BONUS_ID, toughnessBonus);
        applyOrRemoveBonusModifier(knockbackResistance, TAMED_KNOCKBACK_BONUS_ID, knockbackBonus);
    }

    private static double getEquippedAttributeValue(Creeper creeper, Holder<Attribute> attribute) {
        return getSlotAttributeValue(creeper.getItemBySlot(EquipmentSlot.HEAD), EquipmentSlot.HEAD, attribute)
                + getSlotAttributeValue(creeper.getItemBySlot(EquipmentSlot.CHEST), EquipmentSlot.CHEST, attribute);
    }

    private static double getSlotAttributeValue(ItemStack stack, EquipmentSlot slot, Holder<Attribute> attribute) {
        if (stack.isEmpty()) return 0.0D;
        final double[] total = {0.0D};
        stack.forEachModifier(slot, (holder, modifier) -> {
            if (holder.equals(attribute) && modifier.operation() == Operation.ADD_VALUE) {
                total[0] += modifier.amount();
            }
        });
        return total[0];
    }

    private static void applyOrRemoveBonusModifier(AttributeInstance attribute, ResourceLocation modifierId, double amount) {
        if (attribute == null) return;
        if (Math.abs(amount) < 1.0E-6D) {
            attribute.removeModifier(modifierId);
            return;
        }
        attribute.addOrReplacePermanentModifier(new AttributeModifier(modifierId, amount, Operation.ADD_VALUE));
    }

    private static void removeLegacyModifier(AttributeInstance attribute, ResourceLocation modifierId) {
        if (attribute != null) attribute.removeModifier(modifierId);
    }

    private static void removeBonusModifier(AttributeInstance attribute, ResourceLocation modifierId) {
        if (attribute != null) attribute.removeModifier(modifierId);
    }
}
