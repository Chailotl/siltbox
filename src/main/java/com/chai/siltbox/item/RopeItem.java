package com.chai.siltbox.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RopeItem extends BlockItem
{
	public RopeItem(Block block, Settings settings)
	{
		super(block, settings);
	}

	@Nullable
	public ItemPlacementContext getPlacementContext(ItemPlacementContext context)
	{
		BlockPos.Mutable pos = context.getBlockPos().mutableCopy();
		World world = context.getWorld();

		pos = pos.move(context.getSide().getOpposite());

		while (world.getBlockState(pos).getBlock() == getBlock())
		{
			pos = pos.move(Direction.DOWN);
		}

		PlayerEntity player = context.getPlayer();
		if (player.isSneaking() && world.getBlockState(pos.up()).getBlock() == getBlock())
		{
			world.removeBlock(pos.up(), false); //, player);
			player.giveItemStack(getDefaultStack());
			return null;
		}

		if (world.getBlockState(pos).canReplace(context))
		{
			return ItemPlacementContext.offset(context, pos, Direction.DOWN);
		}

		return context;
	}
}
