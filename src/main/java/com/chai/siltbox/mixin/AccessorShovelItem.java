package com.chai.siltbox.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;

@Mixin(ShovelItem.class)
public interface AccessorShovelItem
{
	@Accessor("PATH_STATES")
	public static Map<Block, BlockState> getPathStates()
	{
		throw new AssertionError();
	}
}
