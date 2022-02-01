package com.chai.siltbox.block;

import java.util.Random;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SafeFireBlock extends AbstractFireBlock
{
	public static final IntProperty AGE;

	public SafeFireBlock(Settings settings)
	{
		super(settings, 1f);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{

		world.getBlockTickScheduler().schedule(pos, this, 30 + random.nextInt(10));

		if (!state.canPlaceAt(world, pos))
		{
			world.removeBlock(pos, false);
		}

		int i = state.get(AGE);


		if (i == 15 && random.nextInt(4) == 0)
		{
			world.removeBlock(pos, false);
			return;
		}
		else if (i < 15)
		{
			state = state.with(AGE, i + 1);
			world.setBlockState(pos, state, 4);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);
		world.getBlockTickScheduler().schedule(pos, this, 30 + world.random.nextInt(10));
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock() != Blocks.AIR;
	}

	@Override
	protected boolean isFlammable(BlockState state)
	{
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AGE);
	}

	static {
		AGE = Properties.AGE_15;
	}
}