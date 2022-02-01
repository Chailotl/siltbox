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
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class LeafParticle extends SpriteBillboardParticle
{
	private static final int startAge = 10;
	protected final float rotateFactor;
	protected float gravityFactor = 0.1f;
	protected float angleFactor = 0f;

	protected LeafParticle(ClientWorld world, double x, double y, double z, double r, double g, double b, SpriteProvider provider)
	{
		super(world, x, y, z, r, g, b);
		setSprite(provider);

		collidesWithWorld = true;
		gravityStrength = 0.1f;
		maxAge = 200;

		velocityX = 0;
		velocityY = 0;
		velocityZ = 0;

		colorAlpha = 0;
		colorRed = (float) r;
		colorGreen = (float) g;
		colorBlue = (float) b;
		rotateFactor = 4f + ((float) Math.random() * 3f);

		scale = 5f / 32f;
	}

	protected float getAngle()
	{
		return angleFactor + (float) Math.sin(age / (rotateFactor + (maxAge - age) / 100f)) / 2f;
	}

	@Override
	public void tick()
	{
		super.tick();

		if (age <= startAge)
		{
			colorAlpha += 0.1f;
			velocityY = 0;
		}
		else if (onGround || velocityY == 0)
		{
			if (colorAlpha > 0.01f)
			{
				colorAlpha -= 0.01f;
			}
			else
			{
				markDead();
			}
		}

		prevAngle = angle;

		if (world.getFluidState(new BlockPos(x, y, z)).isIn(FluidTags.WATER))
		{
			// Float on top of water
			velocityY = 0;
			gravityStrength = 0;
		}
		else
		{
			gravityStrength = gravityFactor;
			if (!onGround)
			{
				angle = getAngle();
			}
		}
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
			return new LeafParticle(world, x, y, z, velX, velY, velZ, provider);
		}
	}
}