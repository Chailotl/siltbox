package com.chai.siltbox.item;

import com.chai.siltbox.Main;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class SteelToolMaterial implements ToolMaterial
{
	public static final SteelToolMaterial INSTANCE = new SteelToolMaterial();

	@Override
	public float getAttackDamage()
	{
		return 2;
	}

	@Override
	public int getDurability()
	{
		return 500;
	}

	@Override
	public int getEnchantability()
	{
		return 14;
	}

	@Override
	public int getMiningLevel()
	{
		return 2;
	}

	@Override
	public float getMiningSpeedMultiplier()
	{
		return 7;
	}

	@Override
	public Ingredient getRepairIngredient()
	{
		return Ingredient.ofItems(Main.STEEL_INGOT);
	}
}
