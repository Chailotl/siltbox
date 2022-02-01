package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(ComposterBlock.class)
public class ModifyComposterOutput
{
	@Redirect(
		method = "emptyFullComposter",
		at = @At(value = "NEW",
		target = "(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/item/ItemStack;"))
	private static ItemStack modifyOutput(ItemConvertible item)
	{
		return new ItemStack(Items.BONE_MEAL, 3);
	}
}