package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(FireChargeItem.class)
public abstract class InjectFireChargeItem
{
	@Shadow
	abstract void playUseSound(World world, BlockPos pos);

	public UseAction getUseAction(ItemStack stack)
	{
		return UseAction.BOW;
	}

	public int getMaxUseTime(ItemStack stack)
	{
		return 72000;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		user.setCurrentHand(hand);
		return TypedActionResult.consume(itemStack);
	}

	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks)
	{
		if (getMaxUseTime(stack) - remainingUseTicks >= 10)
		{
			Vec3d vec = user.getRotationVector();
			SmallFireballEntity fireball = new SmallFireballEntity(world, user, vec.getX(), vec.getY(), vec.getZ());
			fireball.updatePosition(fireball.getX(), user.getEyeY(), fireball.getZ());
			fireball.setItem(stack);
			world.spawnEntity(fireball);

			playUseSound(world, user.getBlockPos());
			if (user instanceof PlayerEntity && !((PlayerEntity) user).abilities.creativeMode)
			{
				stack.decrement(1);
			}
		}
	}
}