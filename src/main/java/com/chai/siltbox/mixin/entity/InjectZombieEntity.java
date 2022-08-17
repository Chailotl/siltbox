package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class InjectZombieEntity extends HostileEntity
{
	protected InjectZombieEntity(EntityType<? extends HostileEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(
		method = "onKilledOther",
		at = @At("HEAD"),
		cancellable = true)
	private void onKilledOtherOverwrite(ServerWorld world, LivingEntity entity,
		CallbackInfo info)
	{
		super.onKilledOther(world, entity);

		if (entity instanceof VillagerEntity)
		{
			VillagerEntity villagerEntity = (VillagerEntity) entity;
			ZombieVillagerEntity zombieVillagerEntity = villagerEntity.method_29243(EntityType.ZOMBIE_VILLAGER, false);
			zombieVillagerEntity.initialize(world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), null);
			zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
			zombieVillagerEntity.setGossipData(villagerEntity.getGossip().serialize(NbtOps.INSTANCE).getValue());
			zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toTag());
			zombieVillagerEntity.setXp(villagerEntity.getExperience());
			if (!this.isSilent()) {
				world.syncWorldEvent(null, 1026, this.getBlockPos(), 0);
			}

			info.cancel();
		}
	}
}