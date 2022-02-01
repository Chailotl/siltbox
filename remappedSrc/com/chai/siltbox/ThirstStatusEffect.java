package com.chai.siltbox;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;

public class ThirstStatusEffect extends StatusEffect
{
	protected ThirstStatusEffect()
	{
		super(StatusEffectType.HARMFUL, 0);
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier)
	{
		if (entity instanceof PlayerEntity && !entity.world.isClient)
		{
			((IPlayerEntity) entity).getThirstManager().addExhaustion(0.005F * (amplifier + 1));
		}
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier)
	{
		return this == Main.THIRST;
	}
}