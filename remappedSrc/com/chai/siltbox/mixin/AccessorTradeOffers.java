package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.village.TradeOffers;

@Mixin(TradeOffers.class)
public interface AccessorTradeOffers
{
	@Accessor("WANDERING_TRADER_TRADES")
	public static void setWanderingTrades(Int2ObjectMap<TradeOffers.Factory[]> newTrades)
	{
		throw new AssertionError();
	}
}