package com.chai.siltbox.entity;

import com.chai.siltbox.EntitySpawnPacket;
import com.chai.siltbox.Main;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class SeedEntity extends PersistentProjectileEntity
{
	public SeedEntity(EntityType<? extends SeedEntity> type, World world)
	{
		super(type, world);
		this.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
	}

	public SeedEntity(World world, double x, double y, double z)
	{
		this(Main.SEED_ENTITY, world);
		this.updatePosition(x, y, z);
	}

	public SeedEntity(World world, LivingEntity owner)
	{
		this(world, owner.getX(), owner.getEyeY() - 0.10000000149011612D, owner.getZ());
		this.setOwner(owner);
	}

	@Override
	protected ItemStack asItemStack()
	{
		return new ItemStack(Items.WHEAT_SEEDS);
	}

	@Override
	protected void onBlockHit(BlockHitResult hitResult)
	{
		super.onBlockHit(hitResult);
		this.kill();
	}

	@Override
	public Packet<?> createSpawnPacket()
	{
		return EntitySpawnPacket.create(this, Main.PacketID);
	}
}