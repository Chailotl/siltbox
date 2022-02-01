package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.item.SiltItems;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CowEntity.class)
public abstract class InjectCowEntity
{
	@Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
	private void interactWithPail(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info)
	{
		CowEntity cow = (CowEntity) (Object) this;
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == SiltItems.PAIL && !cow.isBaby())
		{
			player.playSound(SoundEvents.ENTITY_COW_MILK, 1.0F, 1.0F);
			ItemStack itemStack2 = ItemUsage.method_30012(itemStack, player, SiltItems.MILK_PAIL.getDefaultStack());
			player.setStackInHand(hand, itemStack2);
			info.setReturnValue(ActionResult.success(cow.world.isClient));
		}
	}
}