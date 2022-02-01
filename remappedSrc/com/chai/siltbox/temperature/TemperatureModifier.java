package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;

public abstract class TemperatureModifier
{
	public static float getExternal(PlayerEntity player) { return 0f; }
	public static float getInsulation(PlayerEntity player) { return 0f; }
}
