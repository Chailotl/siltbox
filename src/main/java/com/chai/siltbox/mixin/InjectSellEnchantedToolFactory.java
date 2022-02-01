package com.chai.siltbox.mixin;

import com.chai.siltbox.Main;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public class InjectSellEnchantedToolFactory
{
	@Inject(
		method = "create",
		at = @At("TAIL"),
		locals = LocalCapture.CAPTURE_FAILHARD)
	private void addCurse(Entity entity, Random random,
		CallbackInfoReturnable<TradeOffer> info,
		int i, ItemStack itemStack)
	{
		itemStack.addEnchantment(Main.TRADING_CURSE, 1);
	}
}
