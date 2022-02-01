package com.chai.siltbox.materials;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StrawMaterial implements ArmorMaterial
{
	public static final StrawMaterial INSTANCE = new StrawMaterial();

	private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
	private static final int[] PROTECTION_VALUES = new int[]{0, 0, 0, 0};

	@Override
	public int getDurability(EquipmentSlot slot)
	{
		return BASE_DURABILITY[slot.getEntitySlotId()] * 5;
	}

	@Override
	public int getProtectionAmount(EquipmentSlot slot)
	{
		return PROTECTION_VALUES[slot.getEntitySlotId()];
	}

	@Override
	public int getEnchantability()
	{
		return 15;
	}

	@Override
	public SoundEvent getEquipSound()
	{
		return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return Ingredient.ofItems(Items.WHEAT);
	}

	@Override
	public String getName()
	{
		return "straw";
	}

	@Override
	public float getToughness()
	{
		return 0;
	}

	@Override
	public float getKnockbackResistance()
	{
		return 0;
	}
}