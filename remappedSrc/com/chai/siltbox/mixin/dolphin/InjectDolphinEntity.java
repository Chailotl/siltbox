package com.chai.siltbox.mixin.dolphin;

import com.chai.siltbox.IDolphin;
import com.chai.siltbox.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DolphinEntity.class)
public abstract class InjectDolphinEntity extends WaterCreatureEntity implements IDolphin
{
	private int fishEaten = 0;
	private int itemsBooped = 0;

	@Shadow private void spawnParticlesAround(ParticleEffect parameters)
	{
		return;
	}

	protected InjectDolphinEntity(EntityType<? extends WaterCreatureEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public void eatFish()
	{
		++fishEaten;
		checkBubble();
	}

	@Override
	public void boopItem()
	{
		++itemsBooped;
		checkBubble();
	}

	private void checkBubble()
	{
		if (fishEaten >= 5 && itemsBooped >= 20)
		{
			fishEaten = 0;
			itemsBooped = 0;

			if (!world.isClient)
			{
				double d = getEyeY() - 0.30000001192092896D;
				ItemEntity itemEntity = new ItemEntity(world, getX(), d, getZ(), Main.PEARLESCENT_BUBBLE.getDefaultStack());
				itemEntity.setPickupDelay(20);
				itemEntity.setThrower(getUuid());
				float g = random.nextFloat() * 6.2831855F;
				float h = 0.02F * random.nextFloat();
				itemEntity.setVelocity(0.3F * -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) + MathHelper.cos(g) * h, 0.3F * MathHelper.sin(pitch * 0.017453292F) * 1.5F, 0.3F * MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) + MathHelper.sin(g) * h);
				world.spawnEntity(itemEntity);
				playSound(Main.BUBBLE_POP, 1f, 1f);

				((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityStatusS2CPacket(this, (byte) 39));
			}
		}
	}

	@Inject(
		method = "interactMob",
		at = @At("HEAD"))
	private void givenAFish(PlayerEntity player, Hand hand,
		CallbackInfoReturnable<Boolean> info)
	{
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isEmpty() && itemStack.getItem().isIn(ItemTags.FISHES))
		{
			eatFish();
		}
	}

	@Inject(
		method = "handleStatus",
		at = @At("HEAD"),
		cancellable = true)
	private void beHappy(byte status, CallbackInfo info)
	{
		if (status == 39)
		{
			spawnParticlesAround(ParticleTypes.HEART);
			info.cancel();
		}
	}

	@Inject(
		method = "writeCustomDataToTag",
		at = @At("TAIL"))
	private void writeCustomData(CompoundTag tag, CallbackInfo info)
	{
		tag.putInt("FishEaten", fishEaten);
		tag.putInt("ItemsBooped", itemsBooped);
	}

	@Inject(
		method = "readCustomDataFromTag",
		at = @At("TAIL"))
	private void readCustomData(CompoundTag tag, CallbackInfo info)
	{
		fishEaten = tag.getInt("FishEaten");
		itemsBooped = tag.getInt("ItemsBooped");
	}
}