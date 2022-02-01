package com.chai.siltbox.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BambooSpikesBlock extends Block
{
	protected static final VoxelShape SHAPE;

	public BambooSpikesBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onLandedUpon(World world, BlockPos pos, Entity entity, float distance)
	{
		entity.handleFallDamage(distance * 4f, 1.0F);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return hasTopRim(world, pos.down()); //!world.isAir(pos.down());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
		BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	static {
		SHAPE = Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
	}
}
