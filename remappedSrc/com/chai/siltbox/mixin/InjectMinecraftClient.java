package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class InjectMinecraftClient
{
	@Inject(
		method = "getFramerateLimit",
		at = @At("HEAD"),
		cancellable = true)
	private void uncapMenu(CallbackInfoReturnable<Integer> info)
	{
		info.setReturnValue(((MinecraftClient)(Object)this).getWindow().getFramerateLimit());
	}
}