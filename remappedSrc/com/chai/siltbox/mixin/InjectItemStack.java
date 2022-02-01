package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chai.siltbox.Main;
import com.chai.siltbox.item.MarshmallowOnAStickItem;

import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class InjectItemStack
{
	@Inject(at = @At("HEAD"),
		method = "areEqual",
		cancellable = true)
	private static void removeBob(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> info)
	{
		if (left.getItem() == Main.MARSHMALLOW_ON_A_STICK &&
			right.getItem() == Main.MARSHMALLOW_ON_A_STICK &&
			MarshmallowOnAStickItem.getCookedState(left) ==
			MarshmallowOnAStickItem.getCookedState(right))
		{
			info.setReturnValue(true);
		}
	}
}