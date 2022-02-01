package com.chai.siltbox.particles;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class AcaciaLeafParticle extends OakLeafParticle
{
	protected AcaciaLeafParticle(ClientWorld world, double x, double y, double z, double r, double g, double b, SpriteProvider provider)
	{
		super(world, x, y, z, r, g, b, provider);
	}
}