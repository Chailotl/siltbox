package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

@Mixin(PotionItem.class)
public class InjectPotionItem
{
	@Inject(
		method = "hasGlint",
		at = @At("HEAD"),
		cancellable = true)
	private void removeGlint(ItemStack stack, CallbackInfoReturnable<Boolean> info)
	{
		info.setReturnValue(stack.hasEnchantments());
	}
}