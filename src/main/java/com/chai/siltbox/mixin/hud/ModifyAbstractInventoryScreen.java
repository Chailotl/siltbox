package com.chai.siltbox.mixin.hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;

@Mixin(AbstractInventoryScreen.class)
public class ModifyAbstractInventoryScreen
{
	@ModifyConstant(method = "applyStatusEffectOffset", constant = @Constant(intValue = 160))
	private int modifyOne(int original)
	{
		return 0;
	}

	@ModifyConstant(method = "applyStatusEffectOffset", constant = @Constant(intValue = 200))
	private int modifyTwo(int original)
	{
		return 0;
	}
}