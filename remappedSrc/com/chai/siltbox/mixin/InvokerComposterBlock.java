package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;

@Mixin(ComposterBlock.class)
public interface InvokerComposterBlock
{
	@Invoker("registerCompostableItem")
	public static void registerCompost(float levelIncreaseChance, ItemConvertible item)
	{
		throw new AssertionError();
	}
}