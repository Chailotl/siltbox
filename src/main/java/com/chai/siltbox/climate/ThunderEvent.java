package com.chai.siltbox.climate;

public class ThunderEvent extends WeatherEvent
{
	public ThunderEvent(ClimateManager manager)
	{
		super(manager);
	}

	@Override
	public String getName()
	{
		return "thunder";
	}

	@Override
	public int getRandomDuration()
	{
		return manager.getRandom().nextInt(12000) + 3600;
	}

	@Override
	public int getRandomCooldown()
	{
		return manager.getRandom().nextInt(168000) + 12000;
	}
}