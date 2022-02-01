package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.item.Item;

@Mixin(EntityShapeContext.class)
public interface AccessorEntityShapeContext
{
	@Accessor("heldItem")
	public Item getHeldItem();
}