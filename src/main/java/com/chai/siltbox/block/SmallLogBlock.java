package com.chai.siltbox.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SmallLogBlock extends Block
{
	public static final EnumProperty<Direction.Axis> AXIS;
	protected static final VoxelShape X_SHAPE;
	protected static final VoxelShape Z_SHAPE;

	public SmallLogBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		Direction.Axis axis = ctx.getPlayerFacing().getAxis();
		return getDefaultState().with(AXIS, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		switch(state.get(AXIS))
		{
			case Z:
				return Z_SHAPE;
			case X:
			default:
				return X_SHAPE;
		}
	}

	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		switch(rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch(state.get(AXIS)) {
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	static {
		AXIS = Properties.HORIZONTAL_AXIS;
		X_SHAPE = Block.createCuboidShape(0, 0, 4, 16, 8, 12);
		Z_SHAPE = Block.createCuboidShape(4, 0, 0, 12, 8, 16);
	}
}
