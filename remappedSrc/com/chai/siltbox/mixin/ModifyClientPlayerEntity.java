package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.chai.siltbox.IPlayerEntity;

import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ModifyClientPlayerEntity
{
	@ModifyConstant(
		method = "tickMovement",
		constant = @Constant(floatValue = 6.0F))
	private float modifySprintFoodLevel(float prevFoodLevel)
	{
		return 0.0f;
	}

	@ModifyVariable(
		method = "tickMovement",
		ordinal = 4,
		at = @At(value = "INVOKE_ASSIGN"))
	private boolean stopSprintIfThirsty(boolean original)
	{
		if (((IPlayerEntity)this).getThirstManager().getWaterLevel() == 0)
		{
			return false;
		}

		return original;
	}
}