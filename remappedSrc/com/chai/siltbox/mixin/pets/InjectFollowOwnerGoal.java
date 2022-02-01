package com.chai.siltbox.mixin.pets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chai.siltbox.IFollower;

import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.passive.TameableEntity;

@Mixin(FollowOwnerGoal.class)
public class InjectFollowOwnerGoal
{
	@Shadow private TameableEntity tameable;

	@Inject(
		method = "canStart",
		at = @At("HEAD"),
		cancellable = true)
	private void stopStart(CallbackInfoReturnable<Boolean> info)
	{
		if (!((IFollower) tameable).getFollowing())
		{
			info.setReturnValue(false);
		}
	}

	@Inject(
		method = "shouldContinue",
		at = @At("HEAD"),
		cancellable = true)
	private void doNotContinue(CallbackInfoReturnable<Boolean> info)
	{
		if (!((IFollower) tameable).getFollowing())
		{
			info.setReturnValue(false);
		}
	}
}