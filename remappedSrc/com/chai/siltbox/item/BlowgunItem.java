package com.chai.siltbox.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class BlowgunItem extends RangedWeaponItem implements Vanishable
{
	public BlowgunItem(Settings settings)
	{
		super(settings);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (user instanceof PlayerEntity)
		{
			PlayerEntity playerEntity = (PlayerEntity)user;
			ItemStack itemStack = playerEntity.getArrowType(stack);
			if (!itemStack.isEmpty())
			{
				//int i = this.getMaxUseTime(stack) - remainingUseTicks;
				if (!world.isClient)
				{
					//ArrowItem arrowItem = ((ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW));
					//PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);
					PersistentProjectileEntity persistentProjectileEntity = null; //new SeedEntity(world, playerEntity);
					persistentProjectileEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 3.0F, 1.0F);

					stack.damage(1, (LivingEntity)playerEntity, (p) ->
					{
						p.sendToolBreakStatus(playerEntity.getActiveHand());
					});

					world.spawnEntity(persistentProjectileEntity);
				}

				world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + 0.5F);
				if (!playerEntity.abilities.creativeMode)
				{
					itemStack.decrement(1);
					if (itemStack.isEmpty())
					{
						playerEntity.inventory.removeOne(itemStack);
					}
				}

				playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
			}
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		boolean bl = !user.getArrowType(itemStack).isEmpty();
		if (!user.abilities.creativeMode && !bl)
		{
			return TypedActionResult.fail(itemStack);
		}
		else
		{
			user.setCurrentHand(hand);
			return TypedActionResult.consume(itemStack);
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack)
	{
		return 72000;
	}

	@Override
	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BOW;
	}

	@Override
	public Predicate<ItemStack> getProjectiles()
	{
		return (stack) ->
		{
			return stack.getItem() == Items.WHEAT_SEEDS;
		};
	}

	@Override
	public int getRange()
	{
		return 15;
	}
}