package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;

@Mixin(TridentItem.class)
public class InjectTridentItem
{
	public boolean canRepair(ItemStack stack, ItemStack ingredient)
	{
		return Items.PRISMARINE_SHARD == ingredient.getItem();
	}
}