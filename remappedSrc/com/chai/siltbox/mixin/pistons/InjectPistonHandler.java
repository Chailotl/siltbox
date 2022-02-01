package com.chai.siltbox.mixin.pistons;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(PistonHandler.class)
public abstract class InjectPistonHandler
{
	@Shadow private World world;
	@Shadow private Direction motionDirection;
	@Shadow private List<BlockPos> movedBlocks;
	@Shadow private static boolean isBlockSticky(Block block) { return false; }
	@Shadow private static boolean isAdjacentBlockStuck(Block block, Block block2) {return false;}
	@Shadow protected abstract boolean tryMove(BlockPos blockPos, Direction direction);
	@Shadow protected abstract boolean canMoveAdjacentBlock(BlockPos pos);

	private BlockPos currentPos;
	private BlockState currentState;
	private BlockState previousState;
	@Redirect(
		method = "tryMove",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
		ordinal = 0))
	private BlockState redirectGetBlockState_1_A(World world, BlockPos pos)
	{
		return currentState = previousState = world.getBlockState(pos);
	}

	@Redirect(
		method = "tryMove",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
		ordinal = 1))
	private BlockState redirectGetBlockState_1_B(World world, BlockPos pos)
	{
		previousState = currentState;
		currentState = world.getBlockState(pos);
		currentPos = pos;
		return currentState;
	}

	@Redirect(
		method = "tryMove",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z",
		ordinal = 0))
	private boolean redirectIsStickyBlock(Block block)
	{
		return blockCanBePulled(currentState) || isBlockSticky(block);
	}

	private boolean blockCanBePulled(BlockState state)
	{
		Block block = state.getBlock();

		if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)
		{
			return getDirectionToOtherChestHalf(state) == motionDirection.getOpposite();
		}
		else if (block == Blocks.CHAIN)
		{
			return isChainOnAxis(currentState, motionDirection);
		}

		return false;
	}

	@Redirect(
		method = "tryMove",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean isDraggingPreviousBlockBehind(Block previous, Block next)
	{
		if (previousState.getBlock() == Blocks.CHAIN && isChainOnAxis(previousState, motionDirection))
		{
			if ( (currentState.getBlock() == Blocks.CHAIN && isChainOnAxis(currentState, motionDirection))
				|| Block.sideCoversSmallSquare(world, currentPos, motionDirection))
			{
				return true;
			}
		}
		return isAdjacentBlockStuck(previous, next);
	}

	@Inject(
		method = "tryMove",
		at = @At(value = "INVOKE",
		target = "Ljava/util/List;get(I)Ljava/lang/Object;",
		shift = At.Shift.AFTER),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void stickToStickySide(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info,
		BlockState blockState_1, Block block_1, int int_1, int int_2, int int_4, BlockPos blockPos_3, int int_5, int int_6)
	{
		if (!stickToStickySide(blockPos_3))
		{
			info.setReturnValue(false);
		}
	}

	@Inject(
		method = "calculatePush",
		at = @At(value = "INVOKE",
		target = "Ljava/util/List;get(I)Ljava/lang/Object;",
		shift = At.Shift.AFTER),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void stickToStickySide(CallbackInfoReturnable<Boolean> info, int int_1)
	{
		if (!stickToStickySide(movedBlocks.get(int_1)))
		{
			info.setReturnValue(false);
		}

		BlockPos pos = movedBlocks.get(int_1);
		BlockState chainState = world.getBlockState(pos);
		if (chainState.getBlock() == Blocks.CHAIN && !isChainOnAxis(chainState, motionDirection)
			&& !canMoveAdjacentBlock(pos))
		{
			info.setReturnValue(false);
		}
	}

	private boolean stickToStickySide(BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Direction stickyDirection  = null;
		if(block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST)
		{
			stickyDirection = getDirectionToOtherChestHalf(state);
		}

		return stickyDirection == null || tryMove(pos.offset(stickyDirection), stickyDirection);
	}

	@Inject(
		method = "tryMove",
		at= @At(value = "INVOKE",
		target = "Lnet/minecraft/block/piston/PistonHandler;isBlockSticky(Lnet/minecraft/block/Block;)Z",
		ordinal = 1,
		shift = At.Shift.BEFORE),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void redirectIsStickyBlock(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> info,
		BlockState blockState, Block block, int i, int j, int l, BlockPos blockPos2, int m, int n, BlockPos blockPos3)
	{
		BlockState chainState = world.getBlockState(blockPos3);
		if (chainState.getBlock() == Blocks.CHAIN && !isChainOnAxis(chainState, motionDirection) && !canMoveAdjacentBlock(blockPos3))
		{
			info.setReturnValue(false);
		}
	}

	@Inject(
		method = "canMoveAdjacentBlock",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z",
		shift = At.Shift.BEFORE),
		locals = LocalCapture.CAPTURE_FAILHARD,
		cancellable = true)
	private void otherSideStickyCases(BlockPos pos, CallbackInfoReturnable<Boolean> info, BlockState blockState,
		Direction var3[], int var4, int var5, Direction direction, BlockPos blockPos, BlockState blockState2)
	{
		if (blockState.getBlock() == Blocks.CHAIN && isChainOnAxis(blockState, direction) && !blockState2.isAir())
		{
			Block otherBlock = blockState2.getBlock();
			if ((otherBlock == Blocks.CHAIN && (blockState.get(ChainBlock.AXIS) == blockState2.get(ChainBlock.AXIS)))
				|| otherBlock == Blocks.HONEY_BLOCK
				|| Block.sideCoversSmallSquare(world, blockPos, direction.getOpposite()))
			{
				if (!tryMove(blockPos, direction))
				{
					info.setReturnValue(false);
				}
			}
		}
	}

	@Redirect(
		method = "canMoveAdjacentBlock",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/piston/PistonHandler;isAdjacentBlockStuck(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;)Z"))
	private boolean isStuckSlimeStone(Block block, Block block2)
	{
		return isBlockSticky(block2) && isAdjacentBlockStuck(block, block2);
	}

	private Direction getDirectionToOtherChestHalf(BlockState state)
	{
		if (state.contains(ChestBlock.CHEST_TYPE))
		{
			return state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE ? ChestBlock.getFacing(state) : null;
		}
		return null;
	}

	private boolean isChainOnAxis(BlockState state, Direction stickDirection)
	{
		if (state.contains(ChainBlock.AXIS))
		{
			return stickDirection.getAxis() == state.get(ChainBlock.AXIS);
		}
		return false;
	}
}