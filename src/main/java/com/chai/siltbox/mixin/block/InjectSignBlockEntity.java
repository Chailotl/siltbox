package com.chai.siltbox.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

@Mixin(SignBlockEntity.class)
public class InjectSignBlockEntity
{
	@Shadow
	private boolean editable;

	@Inject(
		method = "onActivate",
		at = @At("HEAD"))
	private void editSign(PlayerEntity player, CallbackInfoReturnable<ActionResult> info)
	{
		if (player.isSneaking())
		{
			editable = true;
			player.openEditSignScreen((SignBlockEntity) (Object) this);
		}
	}
}