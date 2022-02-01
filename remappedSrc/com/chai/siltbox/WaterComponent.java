package com.chai.siltbox;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.effect.StatusEffectInstance;

public class WaterComponent
{
	private final int thirst;
	private final float hydrationModifier;
	private final boolean alwaysDrinkable;
	private final List<Pair<StatusEffectInstance, Float>> statusEffects;

	private WaterComponent(int thirst, float hydrationModifier,
		boolean alwaysDrinkable, List<Pair<StatusEffectInstance, Float>> statusEffects)
	{
		this.thirst = thirst;
		this.hydrationModifier = hydrationModifier;
		this.alwaysDrinkable = alwaysDrinkable;
		this.statusEffects = statusEffects;
	}

	public int getThirst()
	{
		return thirst;
	}

	public float getHydrationModifier()
	{
		return hydrationModifier;
	}

	public boolean isAlwaysDrinkable()
	{
		return alwaysDrinkable;
	}

	public List<Pair<StatusEffectInstance, Float>> getStatusEffects()
	{
		return statusEffects;
	}

	public static class Builder
	{
		private int thirst;
		private float hydrationModifier;
		private boolean alwaysDrinkable;
		private List<Pair<StatusEffectInstance, Float>> statusEffects;

		public WaterComponent.Builder thirst(int thirst)
		{
			this.thirst = thirst;
			return this;
		}

		public WaterComponent.Builder hydrationModifier(float hydrationModifier)
		{
			this.hydrationModifier = hydrationModifier;
			return this;
		}

		public WaterComponent.Builder alwaysDrinkable(boolean alwaysDrinkable)
		{
			this.alwaysDrinkable = alwaysDrinkable;
			return this;
		}

		public WaterComponent.Builder statusEffects(StatusEffectInstance effect, float chance)
		{
			this.statusEffects.add(Pair.of(effect, chance));
			return this;
		}

		public WaterComponent build()
		{
			return new WaterComponent(thirst, hydrationModifier, alwaysDrinkable, statusEffects);
		}
	}
}