package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.LeadItem;

@Mixin(LeadItem.class)
public class InjectLeadItem
{
	/*@Inject(
		method = "useOnBlock",
		at = @At("HEAD"),
		cancellable = true)
	private void useOnLog(ItemUsageContext context,
		CallbackInfoReturnable<ActionResult> info)
	{
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Block block = world.getBlockState(blockPos).getBlock();
		System.out.println(block);
		if (block.isIn(BlockTags.LOGS))
		{
			PlayerEntity playerEntity = context.getPlayer();
			if (!world.isClient && playerEntity != null)
			{
				LeadItem.attachHeldMobsToBlock(playerEntity, world, blockPos);
			}

			info.setReturnValue(ActionResult.success(world.isClient));
		}
	}*/
}