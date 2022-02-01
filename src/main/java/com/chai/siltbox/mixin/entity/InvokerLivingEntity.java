package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public interface InvokerLivingEntity
{
	@Invoker("spawnItemParticles")
	public void spawnParticles(ItemStack stack, int count);
}