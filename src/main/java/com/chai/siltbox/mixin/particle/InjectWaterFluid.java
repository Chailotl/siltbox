package com.chai.siltbox.mixin.particle;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WaterFluid.class)
public class InjectWaterFluid
{
	private boolean isWater(World world, BlockPos pos)
	{
		return world.getFluidState(pos).getFluid() == Fluids.FLOWING_WATER;
	}

	@Inject(
		method = "randomDisplayTick",
		at = @At("HEAD"))
	private void waterParticles(World world, BlockPos pos,
		FluidState state, Random random, CallbackInfo info)
	{
		/*BlockPos up = pos.up();
		if (true || random.nextInt(30) == 0)
		{
			if (isWater(world, up))
			{
				double x = pos.getX() + random.nextInt(2); //random.nextDouble();
				double y = pos.getY() + random.nextDouble();
				double z = pos.getZ() + random.nextInt(2); //random.nextDouble();
				world.addParticle(ParticleTypes.SPLASH, x, y, z, 0.0D, 0.0D, 0.0D);
			}
		}*/
	}
}