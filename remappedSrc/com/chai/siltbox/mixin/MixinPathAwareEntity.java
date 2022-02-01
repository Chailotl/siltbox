package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;

@Mixin(PathAwareEntity.class)
public class MixinPathAwareEntity
{
	@ModifyConstant(method = "updateLeash", constant = @Constant(floatValue = 10.0f, ordinal = 1))
	private float modifyBreakDistance(float prevBreakDistance)
	{
		return Float.POSITIVE_INFINITY;
	}

	@Inject(at = @At("TAIL"), method = "updateLeash")
	private void teleportMob(CallbackInfo info)
	{
		PathAwareEntity pae = (PathAwareEntity) (Object) this;
		Entity entity = pae.getHoldingEntity();
		if (entity == null) { return; }
		float distance = pae.distanceTo(entity);

		if (distance > 10.0f)
		{
			pae.teleport(entity.getX(), entity.getY(), entity.getZ());
		}
	}
}