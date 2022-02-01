package com.chai.siltbox.mixin.entity;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

@Mixin(Entity.class)
public abstract class InjectEntity
{
	@Shadow
	private Box entityBounds;

	@Shadow
	public abstract boolean hasPassengers();
	@Shadow
	public abstract List<Entity> getPassengerList();

	@Redirect(
		method = "adjustMovementForCollisions",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
	private Box getBoundingBox(Entity entity)
	{
		Box box = entityBounds;

		if (hasPassengers())
		{
			for(Entity passenger : getPassengerList())
			{
				box = new Box(
					Math.min(box.minX, passenger.getBoundingBox().minX),
					box.minY,
					Math.min(box.minZ, passenger.getBoundingBox().minZ),
					Math.max(box.maxX, passenger.getBoundingBox().maxX),
					Math.max(box.maxY, passenger.getBoundingBox().maxY),
					Math.max(box.maxZ, passenger.getBoundingBox().maxZ));
			}
		}

		return box;
	}
}