package com.chai.siltbox.temperature;

import com.chai.siltbox.item.SiltItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicInteger;

public class SunlightModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		World world = player.world;

		if (world.getRegistryKey() != World.OVERWORLD)
		{
			return 0f;
		}

		int sunlight = world.getLightLevel(LightType.SKY, player.getBlockPos());

		AtomicInteger shade = new AtomicInteger();
		shade.set(world.getAmbientDarkness());
		player.getArmorItems().forEach((stack) ->
		{
			if (stack.getItem() == SiltItems.STRAW_HAT) { shade.set(11); }
		});

		return Math.max(sunlight - shade.get(), 0) / 10f;
	}
}