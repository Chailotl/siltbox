package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.interfaces.IFollower;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowOwnerGoal.class)
public class InjectFollowOwnerGoal
{
	@Shadow private TameableEntity tameable;
	@Shadow private EntityNavigation navigation;

	@Inject(
		method = "canStart",
		at = @At("HEAD"),
		cancellable = true)
	private void stopStart(CallbackInfoReturnable<Boolean> info)
	{
		// Do not follow if wandering
		if (!((IFollower) tameable).getFollowing())
		{
			info.setReturnValue(false);

			// Check if pet has a pet bed
			BlockPos pos = ((IFollower) tameable).getWanderOrigin();
			if (pos != null)
			{
				// See how far away they are
				double dist = tameable.getBlockPos().getSquaredDistance(pos);

				if (dist > 60 * 60)
				{
					// Teleport if forced far away
					tameable.detachLeash(true, true);
					tameable.refreshPositionAndAngles((double) pos.getX() + 0.5D, pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, tameable.yaw, tameable.pitch);
					navigation.stop();
				}
				else if (dist > 50 * 50)
				{
					// Move back into range
					Vec3d center = new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
					Vec3d vec = tameable.getPos().subtract(center).normalize().multiply(45.0D).add(center);
					navigation.startMovingTo(vec.getX(), vec.getY(), vec.getZ(), 1.0D);
				}
			}
		}
	}

	@Inject(
		method = "shouldContinue",
		at = @At("HEAD"),
		cancellable = true)
	private void doNotContinue(CallbackInfoReturnable<Boolean> info)
	{
		// Do not follow if wandering
		if (!((IFollower) tameable).getFollowing())
		{
			info.setReturnValue(false);
		}
	}
}