package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Mixin(ArmorStandEntity.class)
public abstract class InjectArmorStandEntity
{
	@Shadow
	public abstract void setShowArms(boolean showArms);

	@Shadow
	public abstract boolean shouldShowArms();

	@Inject(
		method = "interactAt",
		at = @At("HEAD"))
	private void changePose(PlayerEntity player, Vec3d hitPos,
		Hand hand, CallbackInfoReturnable<ActionResult> info)
	{
		if (player.isSneaking())
		{
			setShowArms(!shouldShowArms());
		}
	}
}