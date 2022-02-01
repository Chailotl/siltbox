package com.chai.siltbox.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.LeashKnotEntityRenderer;

@Mixin(LeashKnotEntityRenderer.class)
public class InjectLeashKnotEntityRenderer
{
	/*@Inject(
		method = "render",
		at = @At("HEAD"))
	private void renderBig(LeashKnotEntity entity, float f, float g, MatrixStack matrices,
		VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info)
	{
		if (entity.world.getBlockState(entity.getBlockPos()).isIn(BlockTags.LOGS))
		{
			matrices.scale(-18f/6f, -0.5f, 18f/6f);
			i = 15728640;
		}
	}*/
}