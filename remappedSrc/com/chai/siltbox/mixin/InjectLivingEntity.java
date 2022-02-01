package com.chai.siltbox.mixin;

import com.chai.siltbox.Main;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class InjectLivingEntity extends Entity
{
	private boolean bounce = true;

	@Shadow
	protected float lastDamageTaken;

	@Shadow public abstract Iterable<ItemStack> getArmorItems();

	public InjectLivingEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Inject(
		method = "takeKnockback",
		at = @At("HEAD"))
	private void updateKnockbackYaw(float f, double d, double e, CallbackInfo info)
	{
		Main.setKnockbackYaw((LivingEntity) (Object) this);
	}

	@Inject(
		method = "damage",
		at = @At("HEAD"))
	private void modifyLastDamage(DamageSource source, float amount,
		CallbackInfoReturnable<Boolean> info)
	{
		String name = source.name;
		if (name.equals("player") || name.equals("mob") || name.equals("sting") ||
			name.equals("arrow") || name.equals("trident") || name.equals("fireworks") ||
			name.equals("thrown") || name.equals("thorns") || name.equals("magic") ||
			name.equals("indirectMagic"))
		{
			lastDamageTaken = 0;
		}
	}

	private static boolean hasSkippingEnchant(LivingEntity entity)
	{
		for (ItemStack stack : entity.getArmorItems())
		{
			if (stack.getItem() == Items.ELYTRA &&
				EnchantmentHelper.getLevel(Main.SKIPPING, stack) > 0)
			{
				return true;
			}
		}

		return false;
	}

	@Redirect(
		method = "travel",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V"))
	public void cancelElytraCancel(LivingEntity entity, int index, boolean value)
	{
		if (!hasSkippingEnchant(entity))
		{
			setFlag(index, value);
		}
	}


	@Redirect(
		method = "initAi",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/LivingEntity;setFlag(IZ)V"))
	public void initAi(LivingEntity entity, int index, boolean value)
	{
		if (!hasSkippingEnchant(entity))
		{
			setFlag(index, value);
		}
		else if (entity.getVelocity().y == 0)
		{
			if (!bounce) { setFlag(7, value); }
			bounce = false;
		}
		else
		{
			bounce = true;
		}
	}
}