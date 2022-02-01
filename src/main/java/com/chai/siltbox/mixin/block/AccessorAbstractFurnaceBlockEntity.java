package com.chai.siltbox.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AccessorAbstractFurnaceBlockEntity
{
	@Accessor("burnTime")
	public void setBurnTime(int newTime);

	@Accessor("fuelTime")
	public void setFuelTime(int newTime);
}