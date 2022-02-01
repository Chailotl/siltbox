package com.chai.siltbox.mixin.entity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(ItemFrameEntity.class)
public abstract class InjectItemFrameEntity
{
	@Shadow
	public abstract ItemStack getHeldItemStack();

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void toggleInvisible(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info)
	{
		if (player.isSneaking() && !player.world.isClient() && !getHeldItemStack().isEmpty())
		{
			ItemFrameEntity frame = (ItemFrameEntity) (Object) this;
			frame.setInvisible(!frame.isInvisible());
			frame.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
			info.setReturnValue(ActionResult.SUCCESS);
		}
	}

	@Inject(method = "dropHeldStack", at = @At("HEAD"))
	private void turnOffInvisible(@Nullable Entity entity, boolean alwaysDrop, CallbackInfo info)
	{
		((ItemFrameEntity) (Object) this).setInvisible(false);
	}
}