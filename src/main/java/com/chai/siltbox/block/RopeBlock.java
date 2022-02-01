package com.chai.siltbox.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class RopeBlock extends Block implements Waterloggable
{
	public static final BooleanProperty HOOK;
	public static final BooleanProperty END;
	public static final BooleanProperty WATERLOGGED;
	protected static final VoxelShape SHAPE;

	public RopeBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(HOOK, false).with(END, false).with(WATERLOGGED, false));
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).getBlock() == this || Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		return super.getPlacementState(ctx)
			.with(HOOK, getHook(world, pos)).with(END, getEnd(world, pos))
			.with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
	}

	private boolean getHook(BlockView world, BlockPos pos)
	{
		return world.getBlockState(pos.up()).getBlock() != this;
	}

	private boolean getEnd(BlockView world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock() != this;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
		BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		if (state.get(WATERLOGGED))
		{
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		if (!state.canPlaceAt(world, pos))
		{
			return Blocks.AIR.getDefaultState();
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom)
			.with(HOOK, getHook(world, pos)).with(END, getEnd(world, pos));
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
	{
		return !(Boolean)state.get(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(HOOK, END, WATERLOGGED);
	}

	static {
		HOOK = BooleanProperty.of("hook");
		END = BooleanProperty.of("end");
		WATERLOGGED = Properties.WATERLOGGED;
		SHAPE = Block.createCuboidShape(6, 0, 6, 10, 16, 10);
	}
}
