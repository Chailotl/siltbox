package com.chai.siltbox.mixin.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class InjectHopperBlockEntity extends LootableContainerBlockEntity
{
	@Shadow private DefaultedList<ItemStack> inventory;

	protected InjectHopperBlockEntity(BlockEntityType<?> blockEntityType)
	{
		super(blockEntityType);
	}

	@Inject(
		method = "insert",
		at = @At("HEAD"),
		cancellable = true)
	private void insert(CallbackInfoReturnable<Boolean> info)
	{
		// Check for jukeboxes
		Direction direction = getCachedState().get(HopperBlock.FACING);
		BlockPos blockPos = pos.offset(direction);
		BlockEntity blockEnt = world.getBlockEntity(blockPos);

		if (blockEnt instanceof JukeboxBlockEntity)
		{
			JukeboxBlockEntity jukebox = (JukeboxBlockEntity) blockEnt;

			// Check if they're empty
			if (jukebox.getRecord().isEmpty())
			{
				// See if we are storing any discs
				for (int i = 0; i < size(); ++i)
				{
					ItemStack stack = getStack(i);
					if (!stack.isEmpty() && stack.getItem() instanceof MusicDiscItem)
					{
						// Found a valid disc, do stuff with it
						((JukeboxBlock) Blocks.JUKEBOX).setRecord(world, blockPos,
								  world.getBlockState(blockPos), stack.copy());
						world.syncWorldEvent(null, 1010, blockPos, Item.getRawId(stack.getItem()));

						setStack(i, ItemStack.EMPTY);

						info.setReturnValue(true);
						break;
					}
				}
			}
		}
	}

	@Inject(
		method = "extract",
		at = @At("HEAD"),
		cancellable = true)
	private static void extract(Hopper hopper, CallbackInfoReturnable<Boolean> info)
	{
		// Check for
		BlockPos pos = new BlockPos(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
		BlockPos blockPos = pos.offset(Direction.UP);
		BlockEntity blockEnt = hopper.getWorld().getBlockEntity(blockPos);

		if (blockEnt instanceof JukeboxBlockEntity)
		{
			JukeboxBlockEntity jukebox = (JukeboxBlockEntity) blockEnt;

			// Check if they're not empty
			ItemStack stack = jukebox.getRecord();
			if (!stack.isEmpty())
			{
				// Attempt transfer
				ItemStack stack2 = HopperBlockEntity.transfer(null, hopper, stack.copy(), null);

				if (stack2.isEmpty())
				{
					// Remove music disc
					hopper.markDirty();
					hopper.getWorld().syncWorldEvent(1010, blockPos, 0);
					jukebox.clear();

					info.setReturnValue(true);
				}
			}
		}
	}
}