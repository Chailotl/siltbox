package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.Item;

@Mixin(Item.class)
public interface AccessorItem
{
	@Accessor("maxCount")
	public void setMaxCount(int maxCount);
}