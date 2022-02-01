package com.chai.siltbox.temperature;

import com.chai.siltbox.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		World world = player.world;
		BlockPos posOne = player.getBlockPos();

		float heat = 0f;

		for (int x = -3; x <= 3; ++x)
		{
			for (int y = -3; y <= 3; ++y)
			{
				for (int z = -3; z <= 3; ++z)
				{
					BlockPos posTwo = posOne.north(x).up(y).east(z);

					if (world.getBlockState(posTwo).isIn(Main.HOT_BLOCKS) || world.getFluidState(posTwo).isIn(Main.HOT_FLUIDS))
					{
						//if (posTwo.getY() > posOne.getY()) { posTwo = posTwo.down(); }
						int dist = Math.max(1, posOne.getManhattanDistance(posTwo));
						heat += 1f / (float) dist;
					}
				}
			}
		}

		return Math.min(5, heat);
	}
}