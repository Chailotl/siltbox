package com.chai.siltbox.mixin.particle;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.fluid.Fluid;

@Mixin(BlockLeakParticle.class)
public interface AccessorBlockLeakParticle
{
	@Accessor("fluid")
	public Fluid getFluid();
}