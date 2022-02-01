package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(HoeItem.class)
public class InjectHoeItem
{
	@Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
	private void harvestCrop(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info)
	{
		World world = context.getWorld();
		if (!world.isClient())
		{
			BlockPos pos = context.getBlockPos();
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if (block instanceof CropBlock)
			{
				CropBlock crop = (CropBlock) block;
				if (crop.isMature(state))
				{
					((InvokerBlock) block).dropXP((ServerWorld) world, pos, MathHelper.nextInt(world.random, 0, 1));
					world.breakBlock(pos, true);
					world.setBlockState(pos, state.with(crop.getAgeProperty(), 0));
					if (context.getPlayer() != null)
					{
						context.getStack().damage(1, (LivingEntity) context.getPlayer(), (p) ->
						{
							p.sendToolBreakStatus(context.getHand());
						});
					}
					info.setReturnValue(ActionResult.success(world.isClient));
				}
			}
		}
	}
}