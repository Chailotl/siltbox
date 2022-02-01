package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;

public class WetnessModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		return player.isWet() ? -1f : 0;
	}

	public static float getInsulation(PlayerEntity player)
	{
		return player.isWet() ? -15f : 0;
	}
}