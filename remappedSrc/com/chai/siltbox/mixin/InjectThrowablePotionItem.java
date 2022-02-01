package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(ThrowablePotionItem.class)
public class InjectThrowablePotionItem
{
	@Inject(
		method = "use",
		at = @At("HEAD"))
	private void addCooldown(World world, PlayerEntity user, Hand hand,
		CallbackInfoReturnable<TypedActionResult<ItemStack>> info)
	{
		user.getItemCooldownManager().set((ThrowablePotionItem)(Object)this, 64);
	}
}