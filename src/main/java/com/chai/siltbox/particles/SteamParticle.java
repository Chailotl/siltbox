package com.chai.siltbox.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SteamParticle extends AscendingParticle
{
	protected SteamParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider)
	{
		super(world, x, y, z, 0.1F, 0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.3F, 8, 0.004D, true);

		float f = world.random.nextFloat() * 0.5f + 0.5f;
		this.colorRed = f;
		this.colorGreen = f;
		this.colorBlue = f;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType>
	{
		private final SpriteProvider provider;

		public DefaultFactory(SpriteProvider spriteProvider)
		{
			provider = spriteProvider;
		}

		@Override
		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i)
		{
			return new SteamParticle(clientWorld, d, e, f, g, h, i, 1.0F, provider);
		}
	}
}
