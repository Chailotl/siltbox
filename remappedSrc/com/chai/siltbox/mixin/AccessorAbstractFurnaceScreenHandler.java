package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;

@Mixin(AbstractFurnaceScreenHandler.class)
public interface AccessorAbstractFurnaceScreenHandler
{
	@Accessor("inventory")
	public Inventory getInventory();
}