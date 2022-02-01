package com.chai.siltbox.mixin.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.chai.siltbox.Main;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.tag.FluidTags;

@Mixin(WorldRenderer.class)
public class ModifyWorldRenderer
{
	@Unique
	private FluidState fluidState;

	@ModifyVariable(
		method = "tickRainSplashing",
		ordinal = 0,
		at = @At(value = "INVOKE_ASSIGN"))
	private FluidState checkFluidState(FluidState original)
	{
		fluidState = original;
		return original;
	}

	@ModifyArg(
		method = "tickRainSplashing",
		index = 0,
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
	private ParticleEffect modifyArg(ParticleEffect original)
	{
		return fluidState.isIn(FluidTags.WATER) ? Main.WATER_RIPPLE : original;
	}

	/*@ModifyVariable(
		method = "tickRainSplashing",
		ordinal = 0,
		at = @At(value = "INVOKE_ASSIGN"))
	private ParticleEffect modifyParticle(ParticleEffect original)
	{
		return fluidState.isIn(FluidTags.WATER) ? Main.WATER_RIPPLE : original;
	}*/
}