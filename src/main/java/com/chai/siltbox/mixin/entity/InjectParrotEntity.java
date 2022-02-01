package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(ParrotEntity.class)
public abstract class InjectParrotEntity extends TameableEntity
{
	@Shadow private static Item COOKIE;
	@Shadow public abstract boolean isInAir();

	protected InjectParrotEntity(EntityType<? extends TameableEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(
		method = "interactMob",
		at = @At("HEAD"),
		cancellable = true)
	private void interactParrot(PlayerEntity player, Hand hand,
		CallbackInfoReturnable<ActionResult> info)
	{
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() != COOKIE && isTamed() && isOwner(player) && !isInAir())
		{
			ActionResult result = super.interactMob(player, hand);

			if (result.isAccepted())
			{
				info.setReturnValue(result);
			}
		}
	}
}