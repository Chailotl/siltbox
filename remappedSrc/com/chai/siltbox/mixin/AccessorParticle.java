package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;

@Mixin(Particle.class)
public interface AccessorParticle
{
	@Accessor("world")
	public ClientWorld getWorld();

	@Accessor("x")
	public double getX();

	@Accessor("y")
	public double getY();

	@Accessor("z")
	public double getZ();
}