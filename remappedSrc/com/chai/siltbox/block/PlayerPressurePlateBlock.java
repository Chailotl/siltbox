package com.chai.siltbox.block;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PlayerPressurePlateBlock extends AbstractPressurePlateBlock
{
	public static final BooleanProperty POWERED;

	public PlayerPressurePlateBlock(Settings settings)
	{
		super(settings);
		setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
	}

	@Override
	protected void playPressSound(WorldAccess world, BlockPos pos)
	{
		world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
	}

	@Override
	protected void playDepressSound(WorldAccess world, BlockPos pos)
	{
		world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.75F);
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos)
	{
		Box box = BOX.offset(pos);
		List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

		if (!list.isEmpty())
		{
			Iterator<PlayerEntity> iterator = list.iterator();

			while(iterator.hasNext())
			{
				if (!((Entity)iterator.next()).canAvoidTraps())
				{
					return 15;
				}
			}
		}

		return 0;
	}

	@Override
	protected int getRedstoneOutput(BlockState state)
	{
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	protected BlockState setRedstoneOutput(BlockState state, int rsOut)
	{
		return state.with(POWERED, rsOut > 0);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(POWERED);
	}

	static
	{
		POWERED = Properties.POWERED;
	}
}