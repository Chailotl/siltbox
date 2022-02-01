package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(MerchantEntity.class)
public class InjectMerchantEntity
{
	@Inject(
		method = "canBeLeashedBy",
		at = @At("HEAD"),
		cancellable = true)
	private void leashVillagers(PlayerEntity player,
		CallbackInfoReturnable<Boolean> info)
	{
		info.setReturnValue(true);
	}
}