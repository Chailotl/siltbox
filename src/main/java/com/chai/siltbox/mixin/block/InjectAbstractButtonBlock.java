package com.chai.siltbox.mixin.block;

import com.chai.siltbox.SiltBlocks;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractButtonBlock.class)
public class InjectAbstractButtonBlock
{
	@Inject(
		method = "getPressTicks",
		at = @At("HEAD"),
		cancellable = true)
	private void modifyTicks(CallbackInfoReturnable<Integer> info)
	{
		Block block = (AbstractButtonBlock) (Object) this;
		if (block == SiltBlocks.GOLD_BUTTON)
		{
			info.setReturnValue(4);
		}
		else if (block == SiltBlocks.IRON_BUTTON)
		{
			info.setReturnValue(100);
		}
	}
}