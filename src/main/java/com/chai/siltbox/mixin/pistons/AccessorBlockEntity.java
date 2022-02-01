package com.chai.siltbox.mixin.pistons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity
{
	@Accessor("cachedState")
	public void setCachedState(BlockState state);
}