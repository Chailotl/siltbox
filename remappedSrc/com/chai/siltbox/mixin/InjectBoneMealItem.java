package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BoneMealItem.class)
public class InjectBoneMealItem
{
	@Inject(at = @At("HEAD"), method = "useOnFertilizable", cancellable = true)
	private static void fertilizeSmallFlowers(ItemStack stack, World world,
			BlockPos pos, CallbackInfoReturnable<Boolean> info)
	{
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof FlowerBlock && block != Blocks.WITHER_ROSE)
		{
			Block.dropStack(world, pos, new ItemStack(block, 2));
			stack.decrement(1);
			info.setReturnValue(true);
		}
	}
}