package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.Main;
import com.chai.siltbox.item.SiltItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class InjectLivingEntity extends Entity
{
	private boolean bounce = true;

	@Shadow protected float lastDamageTaken;
	@Shadow private Optional<BlockPos> climbingPos;
	@Shadow public abstract Iterable<ItemStack> getArmorItems();
	@Shadow public abstract ItemStack getOffHandStack();
	@Shadow public abstract ItemStack getMainHandStack();

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

	@Inject(
		method = "isClimbing",
		at = @At("TAIL"),
		cancellable = true)
	private void slimeClimbing(CallbackInfoReturnable<Boolean> info)
	{
		if (getMainHandStack().getItem() == SiltItems.SLIME_GLOVES ||
			getOffHandStack().getItem() == SiltItems.SLIME_GLOVES)
		{
			BlockPos pos = getBlockPos();
			//Vec3d pos = getPos();

			if (world.getBlockState(pos.north()).isSolidBlock(world, pos.north()) ||
				world.getBlockState(pos.south()).isSolidBlock(world, pos.south()) ||
				world.getBlockState(pos.east()).isSolidBlock(world, pos.east()) ||
				world.getBlockState(pos.west()).isSolidBlock(world, pos.west()))
			{
				climbingPos = Optional.of(pos);
				info.setReturnValue(true);
			}
		}
	}

	private static boolean hasSkippingEnchant(LivingEntity entity)
	{
		ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);

		if (stack.getItem() == Items.ELYTRA &&
			EnchantmentHelper.getLevel(Main.SKIPPING, stack) > 0)
		{
			return true;
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