package com.chai.siltbox.climate;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ClimateManager
{
	public static final int SEASON_LENGTH = 15;
	private static final int DAY_TICKS = 24000;

	private final ServerWorld world;
	private final List<WeatherEvent> events = new ArrayList<>();

	public ClimateManager(ServerWorld world)
	{
		this.world = world;
	}

	public void registerEvent(WeatherEvent event)
	{
		events.add(event);
	}

	public void tick()
	{
		for (WeatherEvent event : events)
		{
			event.tick();
		}
	}

	public ServerWorld getWorld()
	{
		return world;
	}

	public Collection<ServerPlayerEntity> getPlayers()
	{
		return PlayerLookup.all(world.getServer());
	}

	public Random getRandom()
	{
		return world.random;
	}

	public int getTimeOfDay()
	{
		return (int) (world.getTimeOfDay() % DAY_TICKS);
	}

	public Seasons getSeason()
	{
		int day = (int) (world.getTimeOfDay() / (SEASON_LENGTH * DAY_TICKS)) % 4;

		switch (day)
		{
			case 0:
				return Seasons.SPRING;
			case 1:
				return Seasons.SUMMER;
			case 2:
				return Seasons.FALL;
			case 3:
				return Seasons.WINTER;
		}

		return Seasons.SPRING;
	}

	public enum Seasons {
		SPRING,
		SUMMER,
		FALL,
		WINTER
	}

	public enum Cloud {
		CLEAR,
		MOSTLY_CLEAR,
		PARTLY_CLOUDY,
		MOSTLY_CLOUDY,
		CLOUDY
	}

	public enum Wind {
		CALM,
		LIGHT_BREEZE,
		MODERATE_BREEZE,
		STRONG_BREEZE,
		GALE,
		STORM,
		HURRICANE
	}
}