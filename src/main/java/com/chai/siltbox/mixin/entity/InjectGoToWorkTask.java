package com.chai.siltbox.mixin.entity;

import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GoToWorkTask.class)
public class InjectGoToWorkTask
{
	@Inject(
		method = "run",
		at = @At("HEAD"))
	private void lockJob(ServerWorld serverWorld, VillagerEntity villagerEntity,
		long l, CallbackInfo info)
	{
		if (villagerEntity.getExperience() == 0) { villagerEntity.setExperience(1); }
	}
}
