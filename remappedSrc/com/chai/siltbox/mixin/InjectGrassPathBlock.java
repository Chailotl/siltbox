package com.chai.siltbox.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrassPathBlock.class)
public abstract class InjectGrassPathBlock extends Block
{
	public InjectGrassPathBlock(Settings settings)
	{
		super(settings);
	}

	@Inject(
		method = "getStateForNeighborUpdate",
		at = @At("HEAD"),
		cancellable = true)
	private void ignoreFenceGates(BlockState state, Direction direction, BlockState newState,
		WorldAccess world, BlockPos pos, BlockPos posFrom, CallbackInfoReturnable<BlockState> info)
	{
		if (direction == Direction.UP && state.getBlock().isIn(BlockTags.FENCE_GATES))
		{
			info.setReturnValue(super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom));
		}
	}
}
