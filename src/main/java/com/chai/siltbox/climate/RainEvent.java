package com.chai.siltbox.climate;

public class RainEvent extends WeatherEvent
{
	public RainEvent(ClimateManager manager)
	{
		super(manager);
	}

	public enum Rain {
		CLEAR,
		LIGHT_RAIN,
		MODERATE_RAIN,
		HEAVY_RAIN,
		VIOLENT_RAIN
	}

	@Override
	public String getName()
	{
		return "rain";
	}

	@Override
	public int getRandomDuration()
	{
		return manager.getRandom().nextInt(12000) + 12000;
	}

	@Override
	public int getRandomCooldown()
	{
		return manager.getRandom().nextInt(168000) + 12000;
	}
}