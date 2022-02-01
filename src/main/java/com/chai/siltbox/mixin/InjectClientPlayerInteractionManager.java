package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chai.siltbox.Main;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

@Mixin(ClientPlayerInteractionManager.class)
public class InjectClientPlayerInteractionManager
{
	@Inject(
		method = "interactBlock",
		at = @At("HEAD"),
		cancellable = true)
	private void cancelInteraction(ClientPlayerEntity player, ClientWorld world,
		Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info)
	{
		if (Main.ticksUntilCanInteract > 0)
		{
			info.setReturnValue(ActionResult.PASS);
		}
	}
}