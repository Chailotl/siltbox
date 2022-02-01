package com.chai.siltbox.block;

import com.chai.siltbox.EmptySlot;
import com.chai.siltbox.Main;
import com.chai.siltbox.mixin.block.AccessorAbstractFurnaceScreenHandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class NetherForgeScreenHandler extends AbstractFurnaceScreenHandler
{
	private void changeSlots(PlayerInventory playerInventory)
	{
		AccessorAbstractFurnaceScreenHandler acc = (AccessorAbstractFurnaceScreenHandler)this;
		slots.set(0, new Slot(acc.getInventory(), 0, 56, 35));
		slots.set(1, new EmptySlot(acc.getInventory(), 1));
	}

	public NetherForgeScreenHandler(int i, PlayerInventory playerInventory)
	{
		super(Main.NETHER_FORGE_SCREEN_HANDLER, Main.NETHER_FORGE_RECIPE, RecipeBookCategory.BLAST_FURNACE, i, playerInventory);

		changeSlots(playerInventory);
	}

	public NetherForgeScreenHandler(int i, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(Main.NETHER_FORGE_SCREEN_HANDLER, Main.NETHER_FORGE_RECIPE, RecipeBookCategory.BLAST_FURNACE, i, playerInventory, inventory, propertyDelegate);

		changeSlots(playerInventory);
	}
}