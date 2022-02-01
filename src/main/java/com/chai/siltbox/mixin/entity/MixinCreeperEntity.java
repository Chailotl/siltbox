package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.interfaces.ICreeper;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class MixinCreeperEntity extends HostileEntity implements ICreeper
{
	private boolean gotColor = false;
	private static final TrackedData<Integer> COLOR;
	private static final TrackedData<Boolean> CAVE;

	protected MixinCreeperEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
	}

	public int getColor()
	{
		return dataTracker.get(COLOR);
	}

	public boolean getCave()
	{
		return dataTracker.get(CAVE);
	}

	@Inject(
		method = "initDataTracker",
		at = @At("TAIL"))
	private void trackColor(CallbackInfo info)
	{
		dataTracker.startTracking(COLOR, 0xffffff);
		dataTracker.startTracking(CAVE, false);
	}

	@Override
	public void setPos(double x, double y, double z)
	{
		super.setPos(x, y, z);

		if (!world.isClient && !gotColor && isChunkPosUpdateRequested())
		{
			gotColor = true;
			if (getBlockPos().getY() < 60)
			{
				dataTracker.set(CAVE, true);
			}
			else
			{
				dataTracker.set(COLOR, BiomeColors.getGrassColor(world, getBlockPos()));
			}
		}
	}

	@Inject(
		method = "writeCustomDataToTag",
		at = @At("TAIL"))
	private void writeColor(CompoundTag tag, CallbackInfo info)
	{
		tag.putInt("Color", dataTracker.get(COLOR));
		tag.putBoolean("Cave", dataTracker.get(CAVE));
	}

	@Inject(
		method = "readCustomDataFromTag",
		at = @At("TAIL"))
	private void readColor(CompoundTag tag, CallbackInfo info)
	{
		if (tag.contains("Color", 99))
		{
			gotColor = true;
			dataTracker.set(COLOR, tag.getInt("Color"));
			dataTracker.set(CAVE, tag.getBoolean("Cave"));
		}
	}

	@ModifyArg(
		method = "explode",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"),
		index = 5)
	private Explosion.DestructionType modifyType(Explosion.DestructionType prevType)
	{
		return world.getBlockState(getBlockPos()).getBlock() == Blocks.CAVE_AIR ? Explosion.DestructionType.BREAK : Explosion.DestructionType.NONE;
	}

	static {
		COLOR = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
		CAVE = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	}
}