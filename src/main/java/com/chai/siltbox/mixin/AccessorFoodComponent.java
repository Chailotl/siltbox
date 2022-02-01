package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.FoodComponent;

@Mixin(FoodComponent.class)
public interface AccessorFoodComponent
{
	@Accessor("snack")
	public void setSnack(boolean snack);
}
