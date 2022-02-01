package com.chai.siltbox.particles;

import com.chai.siltbox.OpenSimplex2F;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class FireflyParticle extends SpriteBillboardParticle
{
	private final float speedFactor = 1/10f;
	private final float noiseFactor = 1/20f;

	private float flashFactor;
	private OpenSimplex2F noise;

	protected FireflyParticle(ClientWorld world, double x, double y, double z, SpriteProvider provider)
	{
		super(world, x, y, z);
		setSprite(provider);

		velocityX = 0;
		velocityY = 0.1f;
		velocityZ = 0;

		colorAlpha = 0;
		colorRed = 187f/255f;
		colorGreen = 1f;
		colorBlue = 107f/255f;

		if (world.getRandom().nextInt(10) == 1)
		{
			colorRed = 107f/255f;
			colorGreen = 250/255f;
			colorBlue = 1f;
		}

		maxAge = 400;
		scale = 1/4f;

		flashFactor = 8f + ((float) Math.random() * 4f);
		noise = new OpenSimplex2F(world.getRandom().nextLong());
	}

	@Override
	public void tick()
	{
		super.tick();

		colorAlpha = (float) Math.max(Math.sin(Math.PI + age / flashFactor), 0);

		velocityX = noise.noise2(x * noiseFactor, z * noiseFactor) * speedFactor;
		velocityY = noise.noise2(x * noiseFactor - 50f, z * noiseFactor + 100f) * speedFactor * 0.5f;
		velocityZ = noise.noise2(x * noiseFactor + 100f, z * noiseFactor - 50f) * speedFactor;
	}

	@Override
	protected int getColorMultiplier(float tint)
	{
		return 15728880;
	}

	@Override
	public ParticleTextureSheet getType()
	{
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public static class DefaultFactory implements ParticleFactory<DefaultParticleType>
	{
		private final SpriteProvider provider;

		public DefaultFactory(SpriteProvider provider)
		{
			this.provider = provider;
		}

		@Override
		public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velX, double velY, double velZ)
		{
			return new FireflyParticle(world, x, y, z, provider);
		}
	}
}