package com.chai.siltbox.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SkippingEnchantment extends Enchantment
{
	public SkippingEnchantment()
	{
		super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST});
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack)
	{
		return stack.getItem() == Items.ELYTRA;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public int getMinPower(int level) { return 25; 	}

	@Override
	public int getMaxLevel() { return 1; }
}
