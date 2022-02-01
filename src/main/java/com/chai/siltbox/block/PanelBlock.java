package com.chai.siltbox.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PanelBlock extends Block
{
	protected static final VoxelShape SHAPE;

	public PanelBlock(AbstractBlock.Settings settings)
	{
		super(settings);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return !world.isAir(pos.down());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
		BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state,
		BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	static {
		SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	}
}
