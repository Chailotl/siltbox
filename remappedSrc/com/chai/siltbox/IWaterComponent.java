package com.chai.siltbox;

import org.jetbrains.annotations.Nullable;

public interface IWaterComponent
{
	boolean isDrink();

	@Nullable
	WaterComponent getWaterComponent();

	void setWaterComponent(WaterComponent waterComponent);
}