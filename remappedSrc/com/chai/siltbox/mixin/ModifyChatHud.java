package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.hud.ChatHud;

@Mixin(ChatHud.class)
public class ModifyChatHud
{
	@ModifyArg(
		method = "render",
		index = 1,
		at = @At(value = "INVOKE",
		target = "Lcom/mojang/blaze3d/systems/RenderSystem;translatef(FFF)V",
		ordinal = 0))
	private float offsetY(float original)
	{
		return original - 10;
	}

	@ModifyConstant(
		method = "getText",
		constant = @Constant(doubleValue = 40.0))
	private double offsetChat(double original)
	{
		return original + 10;
	}
}