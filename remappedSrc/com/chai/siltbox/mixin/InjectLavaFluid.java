package com.chai.siltbox.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.Main;

import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(LavaFluid.class)
public class InjectLavaFluid
{
	private boolean isWater(World world, BlockPos pos)
	{
		return world.getFluidState(pos).getFluid().isIn(FluidTags.WATER);
	}

	@Inject(
		method = "randomDisplayTick",
		at = @At("HEAD"))
	private void lavaParticles(World world, BlockPos pos,
		FluidState state, Random random, CallbackInfo info)
	{
		BlockPos up = pos.up();
		if (random.nextInt(300) == 0)
		{
			BlockPos down = pos.down();
			if (world.getBlockState(up).isAir() && !world.getBlockState(up).isOpaqueFullCube(world, up) &&
				world.getFluidState(down).isIn(FluidTags.LAVA))
			{
				double x = pos.getX() + random.nextDouble();
				double y = pos.getY() + 1.0D;
				double z = pos.getZ() + random.nextDouble();
				world.addParticle(Main.BIG_LAVA_EMBER, x, y, z, 0.0D, 0.0D, 0.0D);
				world.playSound(x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.3F + random.nextFloat() * 0.2F, 0.7F + random.nextFloat() * 0.15F, false);
			}
		}

		if (isWater(world, up.east()) || isWater(world, up.west()) ||
			isWater(world, up.north()) || isWater(world, up.south()))
		{
			for(int i = 0; i < 2; ++i)
			{
				world.addParticle(Main.STEAM, pos.getX() + random.nextDouble(), pos.getY() + 1.2D, pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
			}
		}
	}
}