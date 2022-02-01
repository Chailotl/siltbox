package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.widget.EntryListWidget;

@Mixin(EntryListWidget.class)
public abstract class InjectEntryListWidget
{
	@Shadow
	private double scrollAmount;
	@Shadow
	protected int itemHeight;
	@Shadow
	public abstract void setScrollAmount(double amount);

	private long time;
	private double startScroll;

	private double easeOutQuad (double time, double start, double change, double duration)
	{
		time /= duration;
		return -change * time * (time - 2) + start;
	}

	private double prefab()
	{
		return easeOutQuad(Math.min(System.currentTimeMillis() - time, 150) / 1000d, startScroll, scrollAmount - startScroll, 0.15d);
	}

	@Inject(
		method = "getScrollAmount",
		at = @At("HEAD"),
		cancellable = true)
	private void getScrollAmountI(CallbackInfoReturnable<Double> info)
	{
		info.setReturnValue(prefab());
	}

	@Inject(
		method = "setScrollAmount",
		at = @At("HEAD"))
	private void setScrollAmountI(double amount, CallbackInfo info)
	{
		startScroll = prefab();
		time = System.currentTimeMillis();
	}

	@Inject(
		method = "mouseDragged",
		at = @At("TAIL"))
	private void mouseDraggedI(CallbackInfoReturnable<Boolean> info)
	{
		startScroll = scrollAmount;
	}

	@Inject(
		method = "mouseScrolled",
		at = @At("HEAD"),
		cancellable = true)
	private void mouseScrolledI(double mouseX, double mouseY,
		double amount, CallbackInfoReturnable<Boolean> info)
	{
		setScrollAmount(scrollAmount - amount * itemHeight / 2.0D);
		info.setReturnValue(true);
	}

	@Inject(
		method = "scroll",
		at = @At("HEAD"),
		cancellable = true)
	private void scrollI(int amount, CallbackInfo info)
	{
		setScrollAmount(scrollAmount + amount);
		info.cancel();
	}
}