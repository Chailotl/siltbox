package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.interfaces.IPlayerEntity;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(HungerManager.class)
public class MixinHungerManager
{
	private PlayerEntity player;

	@ModifyConstant(
		method = "update",
		constant = @Constant(intValue = 18))
	private int modifyRegenFoodLevel(int prevFoodLevel)
	{
		return 6;
	}

	@Inject(
		method = "update",
		at = @At("HEAD"))
	private void getPlayer(PlayerEntity original, CallbackInfo info)
	{
		player = original;
	}

	@ModifyVariable(
		method = "update",
		ordinal = 0,
		at = @At(value = "INVOKE_ASSIGN"))
	private boolean stopRegenIfThirsty(boolean original)
	{
		if (((IPlayerEntity) player).getThirstManager().getWaterLevel() < 6f)
		{
			return false;
		}

		return original;
	}
}