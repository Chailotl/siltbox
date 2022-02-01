package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(GameRenderer.class)
public class InjectGameRenderer
{
	@Inject(
		method = "getNightVisionStrength",
		at = @At("HEAD"),
		cancellable = true)
	private static void removeEpilepsy(LivingEntity livingEntity,
		float f, CallbackInfoReturnable<Float> info)
	{
		int i = livingEntity.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration();
		info.setReturnValue(i > 200 ? 1f : i / 200f);
	}
}