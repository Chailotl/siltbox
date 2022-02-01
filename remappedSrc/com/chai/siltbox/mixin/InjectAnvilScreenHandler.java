package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;

@Mixin(AnvilScreenHandler.class)
public abstract class InjectAnvilScreenHandler extends ForgingScreenHandler
{
	public InjectAnvilScreenHandler(ScreenHandlerType<?> type, int syncId,
		PlayerInventory playerInventory, ScreenHandlerContext context)
	{
		super(type, syncId, playerInventory, context);
	}

	@Shadow
	private Property levelCost;

	@Inject(
		method = "updateResult",
		at = @At("TAIL"))
	private void renameCost(CallbackInfo info)
	{
		if (input.getStack(1).isEmpty())
		{
			levelCost.set(1);
		}
		else if (input.getStack(0).getItem() == Items.ELYTRA &&
			input.getStack(1).getItem() == Items.PHANTOM_MEMBRANE)
		{
			levelCost.set(2 + input.getStack(1).getCount());
			output.getStack(0).setRepairCost(input.getStack(0).getRepairCost());
		}
	}
}