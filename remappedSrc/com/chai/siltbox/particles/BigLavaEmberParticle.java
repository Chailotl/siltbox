package com.chai.siltbox.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;

public class BigLavaEmberParticle extends SpriteBillboardParticle
{
	private BigLavaEmberParticle(ClientWorld world, double x, double y, double z)
	{
		super(world, x, y, z, 0.0D, 0.0D, 0.0D);
		velocityX *= 0.800000011920929D;
		velocityY *= 0.800000011920929D;
		velocityZ *= 0.800000011920929D;
		velocityY = (random.nextFloat() * 0.4F + 0.5F);
		scale *= (random.nextFloat() * 2.0F + 2F);
		maxAge = (int)(32D / (Math.random() * 0.6D + 0.4D));
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public int getColorMultiplier(float tint)
	{
		int i = super.getColorMultiplier(tint);
		int k = i >> 16 & 255;
		return 240 | k << 16;
	}

	@Override
	public float getSize(float tickDelta)
	{
		float f = (age + tickDelta) / maxAge;
		return scale * (1.0F - f * f);
	}

	@Override
	public void tick()
	{
		prevPosX = x;
		prevPosY = y;
		prevPosZ = z;
		if (random.nextFloat() > (float)age / (float)maxAge)
		{
			world.addParticle(ParticleTypes.SMOKE, x, y, z, velocityX, velocityY, velocityZ);
		}

		if (age++ >= maxAge)
		{
			markDead();
		}
		else
		{
			velocityY -= 0.03D;
			move(velocityX, velocityY, velocityZ);
			velocityX *= 0.9990000128746033D;
			velocityY *= 0.9990000128746033D;
			velocityZ *= 0.9990000128746033D;
			if (onGround)
			{
				velocityX *= 0.699999988079071D;
				velocityZ *= 0.699999988079071D;
			}

		}
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
			BigLavaEmberParticle particle = new BigLavaEmberParticle(clientWorld, d, e, f);
			particle.setSprite(provider);
			return particle;
		}
	}
}