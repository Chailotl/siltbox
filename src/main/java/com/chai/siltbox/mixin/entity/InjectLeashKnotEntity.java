package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.decoration.LeashKnotEntity;

@Mixin(LeashKnotEntity.class)
public class InjectLeashKnotEntity// extends AbstractDecorationEntity
{
	/*protected InjectLeashKnotEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world)
	{
		super(entityType, world);
	}*/

	/*@Inject(
		method = "interact",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/entity/Entity;remove()V"))
	private void takeFromFence(PlayerEntity player, Hand hand,
		CallbackInfoReturnable<ActionResult> info)
	{
		LeashKnotEntity knot = (LeashKnotEntity)(Object) this;

		List<MobEntity> list = knot.world.getNonSpectatingEntities(MobEntity.class, new Box(knot.getX() - 7.0D, knot.getY() - 7.0D, knot.getZ() - 7.0D, knot.getX() + 7.0D, knot.getY() + 7.0D, knot.getZ() + 7.0D));
		Iterator var7 = list.iterator();

		MobEntity mobEntity2;
		while(var7.hasNext())
		{
			mobEntity2 = (MobEntity)var7.next();
			if (mobEntity2.getHoldingEntity() == knot)
			{
				mobEntity2.attachLeash(player, true);
			}
		}
	}*/

	/*@Inject(
		method = "canStayAttached",
		at = @At("HEAD"),
		cancellable = true)
	private void renderBig(CallbackInfoReturnable<Boolean> info)
	{
		if (world.getBlockState(attachmentPos).getBlock().isIn(BlockTags.LOGS))
		{
			info.setReturnValue(true);
		}
	}*/
}