package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.Main;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class InjectServerPlayerEntity
{
	@Inject(at = @At("HEAD"), method = "tick")
	private void updateSaturation(CallbackInfo info)
	{
		Main.onPlayerUpdate((ServerPlayerEntity) (Object) this);
	}
}