package com.chai.siltbox.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.Block;
import net.minecraft.item.MiningToolItem;

@Mixin(MiningToolItem.class)
public interface AccessorMiningToolItem
{
	@Accessor("effectiveBlocks")
	public Set<Block> getEffectiveBlocks();
}
