package com.chai.siltbox.mixin.hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(InGameOverlayRenderer.class)
public class InjectInGameOverlayRenderer
{
	@Inject(
		method = "renderFireOverlay",
		at = @At("HEAD"))
	private static void offsetFire(MinecraftClient client,
		MatrixStack matrices, CallbackInfo info)
	{
		double offset = -0.2;
		if (client.player.isCreative() || client.player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))
		{
			offset = -10;
		}
		RenderSystem.translated(0, offset, 0);
	}
}