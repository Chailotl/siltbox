package com.chai.siltbox.block;

import com.chai.siltbox.Main;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class NetherForgeRecipe extends AbstractCookingRecipe
{
	public NetherForgeRecipe(Identifier id, String group, Ingredient input,
		ItemStack output, float experience, int cookTime)
	{
		super(Main.NETHER_FORGE_RECIPE, id, group, input, output, experience, cookTime);
	}

	@Override
	public ItemStack getRecipeKindIcon()
	{
		return new ItemStack(Main.NETHER_FORGE);
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return Main.NETHER_FORGE_SERIALIZER;
	}
}