package com.chai.siltbox.item;

import com.chai.siltbox.Main;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MarshmallowOnAStickItem extends Item
{
	public MarshmallowOnAStickItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack stack = user.getStackInHand(hand);
		int cookedState = getCookedState(stack);
		Item item = Main.MARSHMALLOW;
		switch (cookedState)
		{
			case 1:
				item = Main.COOKED_MARSHMALLOW;
				break;
			case 2:
				item = Main.BURNT_MARSHMALLOW;
				break;
		}

		System.out.println(cookedState);
		System.out.println(item);

		user.giveItemStack(item.getDefaultStack());
		return TypedActionResult.success(Items.STICK.getDefaultStack(), world.isClient());
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (!world.isClient() && selected && world.getTime() % 20 == 0)
		{
			BlockPos pos = entity.getBlockPos();

			for (int i = -2; i <= 2; ++i)
			{
				for (int j = -2; j <= 2; ++j)
				{
					Block block = world.getBlockState(pos.add(i, 0, j)).getBlock();
					if (BlockTags.CAMPFIRES.contains(block))
					{
						if (stack.hasTag())
						{
							CompoundTag tag = stack.getTag();
							tag.putInt("CookedTime", tag.getInt("CookedTime") + 1);
							stack.setTag(tag);
						}
						else
						{
							CompoundTag tag = new CompoundTag();
							tag.putInt("CookedTime", 1);
							stack.setTag(tag);
						}
						return;
					}
				}
			}
		}
	}

	public static int getCookedState(ItemStack stack)
	{
		if (!stack.hasTag()) { return 0; }

		int secondsCooked = stack.getTag().getInt("CookedTime");
		if (secondsCooked < 15)
		{
			return 0;
		}
		else if (secondsCooked <= 25)
		{
			return 1;
		}
		else
		{
			return 2;
		}
	}
}