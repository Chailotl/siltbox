package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SunlightModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		World world = player.world;
		int sunlight = world.getLightLevel(LightType.SKY, player.getBlockPos());
		return Math.max(sunlight - world.getAmbientDarkness(), 0) / 10f;
	}
}