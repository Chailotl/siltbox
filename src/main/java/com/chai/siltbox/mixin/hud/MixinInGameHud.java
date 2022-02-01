package com.chai.siltbox.mixin.hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(InGameHud.class)
public class MixinInGameHud
{
	@Shadow private MinecraftClient client;

	@Inject(
		method = "renderStatusEffectOverlay",
		at = @At("HEAD"),
		cancellable = true)
	private void removeStatusOverlay(MatrixStack matrices, CallbackInfo info)
	{
		info.cancel();
	}

	@Redirect(
		method = "render",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z"))
	private boolean showJumpBarWhenJumping(ClientPlayerEntity player)
	{
		return (player.isCreative() && player.hasJumpingMount()) || player.method_3151() > 0f;
	}

	@Redirect(
		method = "renderStatusBars",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
	private int showHungerBar(InGameHud hud, LivingEntity entity)
	{
		return 0;
	}

	@ModifyConstant(
		method = "renderMountHealth",
		constant = @Constant(intValue = 39))
	private int modifyMountHealthHeight(int original)
	{
		return original + (!client.player.isCreative() ? 20 : 0);
	}

	@ModifyConstant(
		method = "renderStatusBars",
		constant = @Constant(intValue = 10, ordinal = 7))
	private int modifyAirMeterHeight(int original)
	{
		return 0;
	}
}
