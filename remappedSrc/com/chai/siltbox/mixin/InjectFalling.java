package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.particle.BlockLeakParticle.Falling;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@Mixin(Falling.class)
public class InjectFalling
{
	public int getColorMultiplier(float tint)
	{
		if (((AccessorBlockLeakParticle) this).getFluid().isIn(FluidTags.LAVA))
		{
			return 15728880;
		}
		AccessorParticle acc = (AccessorParticle) this;
		BlockPos blockPos = new BlockPos(acc.getX(), acc.getY(), acc.getZ());
		return acc.getWorld().isChunkLoaded(blockPos) ? WorldRenderer.getLightmapCoordinates(acc.getWorld(), blockPos) : 0;
	}
}