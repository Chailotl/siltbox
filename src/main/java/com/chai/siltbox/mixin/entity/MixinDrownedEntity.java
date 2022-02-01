package com.chai.siltbox.mixin.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedEntity.class)
public abstract class MixinDrownedEntity extends ZombieEntity
{
	public MixinDrownedEntity(World world)
	{
		super(world);
	}

	@ModifyConstant(
		method = "initEquipment",
		constant = @Constant(doubleValue = 0.9d))
	private double moreEquipment(double original)
	{
		return 0.8d;
	}

	@Inject(
		method = "initEquipment",
		at = @At("TAIL"))
	private void increaseTridentChance(LocalDifficulty difficulty, CallbackInfo info)
	{
		if (getEquippedStack(EquipmentSlot.MAINHAND).getItem() == Items.TRIDENT)
		{
			handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 0.25F;
		}
	}
}