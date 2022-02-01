package com.chai.siltbox.climate;

public abstract class WeatherEvent
{
	private boolean active = false;
	protected int duration = 0;
	protected int cooldown = 0;
	protected int lastRun = 0;
	protected ClimateManager manager;

	public WeatherEvent(ClimateManager manager)
	{
		this.manager = manager;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public int getCooldown()
	{
		return cooldown;
	}

	public void setCooldown(int cooldown)
	{
		this.cooldown = cooldown;
	}

	public int getLastRun()
	{
		return lastRun;
	}

	public boolean getActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		if (active && !this.active)
		{
			this.active = true;
			duration = getRandomDuration();
			cooldown = 0;
			lastRun = 0;
			onStart();
		}
		else if (!active && this.active)
		{
			this.active = false;
			duration = 0;
			cooldown = getRandomCooldown();
			lastRun = 0;
			onStop();
		}
	}

	public abstract String getName();

	public abstract int getRandomDuration();

	public abstract int getRandomCooldown();

	public boolean canRun()
	{
		return --cooldown <= 0;
	}

	protected void onStart() {}

	protected void onActive() {}

	protected void onStop() {}

	public void tick()
	{
		if (active)
		{
			onActive();

			if (--duration <= 0)
			{
				setActive(false);
			}
		}
		else
		{
			++lastRun;

			if (canRun())
			{
				setActive(true);
			}
		}
	}
}