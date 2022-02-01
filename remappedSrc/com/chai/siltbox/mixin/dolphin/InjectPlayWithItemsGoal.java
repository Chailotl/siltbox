package com.chai.siltbox.mixin.dolphin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.IDolphin;

import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DolphinEntity.PlayWithItemsGoal;
import net.minecraft.item.ItemStack;

@Mixin(PlayWithItemsGoal.class)
public class InjectPlayWithItemsGoal
{
	@Shadow DolphinEntity field_6757;

	@Inject(
		method = "spitOutItem",
		at = @At("HEAD"))
	private void boopItem(ItemStack stack, CallbackInfo info)
	{
		if (!stack.isEmpty())
		{
			((IDolphin) field_6757).boopItem();
		}
	}
}