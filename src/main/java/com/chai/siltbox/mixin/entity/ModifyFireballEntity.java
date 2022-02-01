package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.world.explosion.Explosion;

@Mixin(FireballEntity.class)
public class ModifyFireballEntity
{
	@ModifyArg(
		method = "onCollision",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"),
		index = 6)
	private Explosion.DestructionType modifyType(Explosion.DestructionType prevType)
	{
		return Explosion.DestructionType.NONE;
	}
}