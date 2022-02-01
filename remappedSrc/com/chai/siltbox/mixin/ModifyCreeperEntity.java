package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.world.explosion.Explosion;

@Mixin(CreeperEntity.class)
public class ModifyCreeperEntity
{
	@ModifyArg(
		method = "explode",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;"),
		index = 5)
	private Explosion.DestructionType modifyType(Explosion.DestructionType prevType)
	{
		return Explosion.DestructionType.NONE;
	}
}