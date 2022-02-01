package com.chai.siltbox.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SleepingBagBlock extends BedBlock
{
	protected static final VoxelShape SHAPE;

	public SleepingBagBlock(DyeColor color, Settings settings)
	{
		super(color, settings);
	}

	@Override
	public void onEntityLand(BlockView world, Entity entity)
	{
		// Sleeping bags ain't bouncy
		entity.setVelocity(entity.getVelocity().multiply(1.0D, 0.0D, 1.0D));
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	static
	{
		SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
	}
}