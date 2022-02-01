package com.chai.siltbox.interfaces;

import com.chai.siltbox.ThirstManager;
import com.chai.siltbox.temperature.HeatManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IPlayerEntity
{
	ThirstManager getThirstManager();

	HeatManager getHeatManager();

	boolean canDrink(boolean ignoreThirst);

	ItemStack drinkLiquid(World world, ItemStack stack);
}