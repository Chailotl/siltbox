package com.chai.siltbox.mixin;

import net.minecraft.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ThornsEnchantment.class)
public class ModifyThornsEnchantment
{
	@ModifyConstant(
		method = "onUserDamaged",
		constant = @Constant(intValue = 2))
	private int modifyArmorDamage(int prevArmorDamage)
	{
		return 0;
	}
}
