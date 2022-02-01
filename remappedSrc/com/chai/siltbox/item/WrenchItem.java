package com.chai.siltbox.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WrenchItem extends Item
{
	public WrenchItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		BlockState newState = null;
		Block block = state.getBlock();


		if (block == Blocks.END_PORTAL_FRAME ||
			(state.getMaterial() == Material.SUPPORTED &&
			state.contains(Properties.WALL_MOUNT_LOCATION) && state.get(Properties.WALL_MOUNT_LOCATION) == WallMountLocation.WALL) ||
			(state.contains(Properties.CHEST_TYPE) && state.get(Properties.CHEST_TYPE) != ChestType.SINGLE) ||
			(state.contains(Properties.EXTENDED) && state.get(Properties.EXTENDED) == true))
		{
			return ActionResult.PASS;
		}

		if (state.contains(Properties.SLAB_TYPE))
		{
			switch(state.get(Properties.SLAB_TYPE))
			{
				case TOP:
					newState = state.with(Properties.SLAB_TYPE, SlabType.BOTTOM);
					break;
				case BOTTOM:
					newState = state.with(Properties.SLAB_TYPE, SlabType.TOP);
					break;
				default: break;
			}
		}
		else if (state.contains(Properties.ROTATION))
		{
			int rot = state.get(Properties.ROTATION);

			if (rot == 15)
			{
				newState = state.with(Properties.ROTATION, 0);
			}
			else
			{
				newState = state.with(Properties.ROTATION, rot+1);
			}
		}
		else if (state.contains(Properties.AXIS))
		{
			switch(state.get(Properties.AXIS))
			{
				case X:
					newState = state.with(Properties.AXIS, Direction.Axis.Y);
					break;
				case Y:
					newState = state.with(Properties.AXIS, Direction.Axis.Z);
					break;
				case Z:
					newState = state.with(Properties.AXIS, Direction.Axis.X);
					break;
				default: break;
			}
		}
		else if (state.contains(Properties.FACING))
		{
			switch(state.get(Properties.FACING))
			{
				case UP:
					newState = state.with(Properties.FACING, Direction.NORTH);
					break;
				case NORTH:
					newState = state.with(Properties.FACING, Direction.EAST);
					break;
				case EAST:
					newState = state.with(Properties.FACING, Direction.SOUTH);
					break;
				case SOUTH:
					newState = state.with(Properties.FACING, Direction.WEST);
					break;
				case WEST:
					newState = state.with(Properties.FACING, Direction.DOWN);
					break;
				case DOWN:
					newState = state.with(Properties.FACING, Direction.UP);
					break;
				default: break;
			}
		}
		else if (state.contains(Properties.HOPPER_FACING))
		{
			switch(state.get(Properties.HOPPER_FACING))
			{
				case DOWN:
					newState = state.with(Properties.HOPPER_FACING, Direction.NORTH);
					break;
				case NORTH:
					newState = state.with(Properties.HOPPER_FACING, Direction.EAST);
					break;
				case EAST:
					newState = state.with(Properties.HOPPER_FACING, Direction.SOUTH);
					break;
				case SOUTH:
					newState = state.with(Properties.HOPPER_FACING, Direction.WEST);
					break;
				case WEST:
					newState = state.with(Properties.HOPPER_FACING, Direction.DOWN);
					break;
				default: break;
			}
		}
		else if (state.contains(Properties.HORIZONTAL_FACING))
		{
			switch(state.get(Properties.HORIZONTAL_FACING))
			{
				case NORTH:
					newState = state.with(Properties.HORIZONTAL_FACING, Direction.EAST);
					break;
				case EAST:
					newState = state.with(Properties.HORIZONTAL_FACING, Direction.SOUTH);
					break;
				case SOUTH:
					newState = state.with(Properties.HORIZONTAL_FACING, Direction.WEST);
					break;
				case WEST:
					newState = state.with(Properties.HORIZONTAL_FACING, Direction.NORTH);
					break;
				default: break;
			}

			if (state.contains(Properties.BLOCK_HALF) && state.get(Properties.HORIZONTAL_FACING) == Direction.WEST)
			{
				BlockHalf half = state.get(Properties.BLOCK_HALF);
				newState = newState.with(Properties.BLOCK_HALF, half == BlockHalf.TOP ? BlockHalf.BOTTOM : BlockHalf.TOP);
			}
		}
		else if (state.contains(Properties.STRAIGHT_RAIL_SHAPE))
		{
			switch(state.get(Properties.STRAIGHT_RAIL_SHAPE))
			{
				case NORTH_SOUTH:
					newState = state.with(Properties.STRAIGHT_RAIL_SHAPE, RailShape.EAST_WEST);
					break;
				case EAST_WEST:
					newState = state.with(Properties.STRAIGHT_RAIL_SHAPE, RailShape.NORTH_SOUTH);
					break;
				default: break;
			}
		}
		else if (state.contains(Properties.RAIL_SHAPE))
		{
			switch(state.get(Properties.RAIL_SHAPE))
			{
				case NORTH_SOUTH:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.EAST_WEST);
					break;
				case EAST_WEST:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.NORTH_EAST);
					break;
				case NORTH_EAST:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.SOUTH_EAST);
					break;
				case SOUTH_EAST:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.SOUTH_WEST);
					break;
				case SOUTH_WEST:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.NORTH_WEST);
					break;
				case NORTH_WEST:
					newState = state.with(Properties.RAIL_SHAPE, RailShape.NORTH_SOUTH);
					break;
				default: break;
			}
		}

		if (newState != null)
		{
			context.getPlayer().playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
			world.setBlockState(pos, newState);
			return ActionResult.SUCCESS;
		}
		else
		{
			return ActionResult.PASS;
		}
	}
}