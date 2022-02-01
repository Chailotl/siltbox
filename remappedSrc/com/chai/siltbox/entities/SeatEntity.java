package com.chai.siltbox.entity;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Collections;

public class SeatEntity extends LivingEntity
{
	//public static final HashMap<Vec3d,BlockPos> OCCUPIED = new HashMap<>();

	public SeatEntity(EntityType<? extends SeatEntity> type, World world)
	{
		super(type, world);
		noClip = true;
	}

	/*@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger)
	{
		if(passenger instanceof PlayerEntity)
		{
			BlockPos pos = OCCUPIED.remove(getPos());

			if(pos != null)
			{
				remove();
				return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
			}
		}

		remove();
		return super.updatePassengerForDismount(passenger);
	}*/

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {}

	@Override
	protected boolean canClimb()
	{
		return false;
	}

	@Override
	public boolean collides()
	{
		return false;
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	public boolean isInvisible()
	{
		return true;
	}

	@Override
	public float getHealth()
	{
		return 100000.0F;
	}

	@Override
	protected boolean canDropLootAndXp()
	{
		return false;
	}

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance statusEffectInstance)
	{
		return false;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource)
	{
		return null;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return null;
	}

	@Override
	public void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

	@Override
	public Iterable<ItemStack> getArmorItems()
	{
		return Collections.emptyList();
	}

	@Override
	public ItemStack getEquippedStack(EquipmentSlot equipmentSlot)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public Arm getMainArm()
	{
		return Arm.RIGHT;
	}

	public static class EmptyRenderer extends EntityRenderer<SeatEntity>
	{
		public EmptyRenderer(EntityRenderDispatcher dispatcher)
		{
			super(dispatcher);
		}

		@Override
		public boolean shouldRender(SeatEntity entity, Frustum frustum, double d, double e, double f)
		{
			return false;
		}

		@Override
		public Identifier getTexture(SeatEntity entity)
		{
			return null;
		}
	}
}