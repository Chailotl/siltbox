package com.chai.siltbox.mixin.particle;

import com.chai.siltbox.Main;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(TallFlowerBlock.class)
public class InjectTallFlowerBlock
{
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		if (!world.isRaining() && random.nextInt(30) == 0)
		{
			long time = world.getTimeOfDay();
			float temp = world.getBiome(pos).getTemperature();
			if (time >= 12000 && time <= 23000 && temp >= 0.5 && temp < 1)
			{
				world.addParticle(Main.FIREFLY, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 0, 0, 0);
			}
		}
	}
}