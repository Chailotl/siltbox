package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;

public class AltitudeModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		int y = player.getBlockPos().getY();
		if (y > 64)
		{
			return (y - 64f) / -64f;
		}
		else
		{
			return (64f - y) / -64f;
		}
	}
}