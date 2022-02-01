package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.damage.DamageSource;

@Mixin(DamageSource.class)
public interface InvokerDamageSource
{
	@Invoker("<init>")
	public static DamageSource damageSource(String name)
	{
		throw new AssertionError();
	}
}