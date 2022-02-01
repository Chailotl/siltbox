package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;

public class WetnessModifier extends TemperatureModifier
{
	public static float getInsulation(PlayerEntity player)
	{
		return player.isWet() ? -1f : 0;
	}
}