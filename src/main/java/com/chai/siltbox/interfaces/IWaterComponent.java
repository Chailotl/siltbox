package com.chai.siltbox.interfaces;

import com.chai.siltbox.WaterComponent;
import org.jetbrains.annotations.Nullable;

public interface IWaterComponent
{
	boolean isDrink();

	@Nullable
	WaterComponent getWaterComponent();

	void setWaterComponent(WaterComponent waterComponent);
}