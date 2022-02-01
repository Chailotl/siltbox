package com.chai.siltbox.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class TrowelItem extends Item
{
	public TrowelItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		if (world.isClient) { return ActionResult.SUCCESS; }

		PlayerEntity player = context.getPlayer();
		PlayerInventory inventory = player.inventory;

		// Find valid slots
		List<Integer> slots = new ArrayList<>();
		for (int i = 0; i < 9; ++i)
		{
			if (inventory.getStack(i).getItem() instanceof BlockItem)
			{
				slots.add(i);
			}
		}

		if (slots.isEmpty()) { return ActionResult.PASS; }
		int slot = slots.get(world.random.nextInt(slots.size()));

		// Place block
		ItemStack stack = inventory.getStack(slot);

		ActionResult result = stack.useOnBlock(context);
		if (result.isAccepted())
		{
			Block block = ((BlockItem) stack.getItem()).getBlock();
			BlockSoundGroup soundGroup = block.getSoundGroup(block.getDefaultState());
			world.playSound(null, context.getBlockPos(), soundGroup.getPlaceSound(), SoundCategory.BLOCKS, (soundGroup.getVolume() + 1.0F) / 2.0F, soundGroup.getPitch() * 0.8F);

			if (!player.abilities.creativeMode)
			{
				stack.decrement(1);
				context.getStack().setCount(1);
			}

			return ActionResult.success(world.isClient);
		}
		else
		{
			return ActionResult.FAIL;
		}
	}
}