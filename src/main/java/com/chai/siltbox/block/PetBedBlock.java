package com.chai.siltbox.block;

import com.chai.siltbox.interfaces.IFollower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

public class PetBedBlock extends Block
{
	public static final BooleanProperty OCCUPIED;
	protected static final VoxelShape SHAPE;
	private final DyeColor color;

	public PetBedBlock(DyeColor color, Settings settings)
	{
		super(settings);
		this.color = color;
		setDefaultState(stateManager.getDefaultState().with(OCCUPIED, false));
	}

	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		super.onBreak(world, pos, state, player);

		// If occupied, find the pet and remove their wander origin
		if (!world.isClient() && state.get(OCCUPIED))
		{
			List<TameableEntity> targets = world.getEntitiesByClass(TameableEntity.class, new Box(pos).expand(60), null);

			for (TameableEntity ent : targets)
			{
				BlockPos wander = ((IFollower) ent).getWanderOrigin();
				if (wander != null && wander.equals(pos))
				{
					((IFollower) ent).removeWanderOrigin();
					break;
				}
			}
		}
	}

	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		// Summon pet
		if (state.get(OCCUPIED))
		{
			if (!world.isClient())
			{
				List<TameableEntity> targets = world.getEntitiesByClass(TameableEntity.class, new Box(pos).expand(60), null);

				for (TameableEntity ent : targets)
				{
					BlockPos wander = ((IFollower) ent).getWanderOrigin();
					if (wander != null && wander.equals(pos))
					{
						ent.refreshPositionAndAngles((double) pos.getX() + 0.5D, pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, ent.yaw, ent.pitch);
						break;
					}
				}
			}

			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state,
		BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(OCCUPIED);
	}

	static {
		OCCUPIED = Properties.OCCUPIED;
		SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
	}
}