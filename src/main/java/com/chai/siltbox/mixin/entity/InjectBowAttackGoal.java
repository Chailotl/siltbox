package com.chai.siltbox.mixin.entity;

import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowAttackGoal.class)
public class InjectBowAttackGoal
{
	@Shadow private HostileEntity actor;

	@Inject(
		method = "tick",
		at = @At("TAIL"))
	private void makeSound(CallbackInfo info)
	{
		System.out.println(actor.getItemUseTime());

		if (actor.getItemUseTime() == 1)
		{
			actor.playSound(SoundEvents.ITEM_CROSSBOW_LOADING_START, 3f, 1f);
		}
	}
}