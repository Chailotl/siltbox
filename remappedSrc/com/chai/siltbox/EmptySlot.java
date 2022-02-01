package com.chai.siltbox;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class EmptySlot extends Slot
{
	public EmptySlot(Inventory inventory, int index)
	{
		super(inventory, index, 0, 0);
	}

	@Override
	public boolean canInsert(ItemStack stack)
	{
		return false;
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity)
	{
		return false;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean doDrawHoveringEffect()
	{
		return false;
	}
}