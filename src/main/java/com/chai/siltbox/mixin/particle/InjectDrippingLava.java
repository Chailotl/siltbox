package com.chai.siltbox.mixin.particle;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.particle.BlockLeakParticle.DrippingLava;

@Mixin(DrippingLava.class)
public class InjectDrippingLava
{
	protected int getColorMultiplier(float tint)
	{
		return 15728880;
	}
}