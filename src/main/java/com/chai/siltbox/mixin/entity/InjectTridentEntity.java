package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

@Mixin(TridentEntity.class)
public abstract class InjectTridentEntity extends Entity
{
	@Shadow
	private static TrackedData<Byte> LOYALTY;

	@Shadow
	private boolean dealtDamage;

	public InjectTridentEntity(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Override
	protected void destroy()
	{
		if (((TridentEntity)(Object)this).getOwner() != null && dataTracker.get(LOYALTY) > 0)
		{
			dealtDamage = true;
			return;
		}
		super.destroy();
	}

	@Redirect(
		method = "onEntityHit",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"))
	private float modifyDamage(ItemStack stack, EntityGroup group, EntityHitResult entityHitResult)
	{
		float damage = EnchantmentHelper.getAttackDamage(stack, EntityGroup.AQUATIC);
		if (entityHitResult.getEntity().getType() == EntityType.DROWNED)
		{
			// Keep damage
		}
		else if (group != EntityGroup.AQUATIC && entityHitResult.getEntity().isWet())
		{
			damage /= 2f;
		}
		else
		{
			damage = 0;
		}
		return damage;
	}
}