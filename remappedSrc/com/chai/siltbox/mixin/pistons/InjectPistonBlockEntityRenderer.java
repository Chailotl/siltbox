package com.chai.siltbox.mixin.pistons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.PistonBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.World;

@Mixin(PistonBlockEntityRenderer.class)
public class InjectPistonBlockEntityRenderer
{
	@Inject(
		method = "render",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/block/entity/PistonBlockEntityRenderer;method_3575(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;ZI)V"))
	private void renderBlockEntity(PistonBlockEntity piston, float f, MatrixStack matrices, VertexConsumerProvider provider, int i, int j, CallbackInfo info)
	{
		BlockState state = piston.getPushedBlock();
		if (state.getBlock().hasBlockEntity())
		{
			World world = piston.getWorld();
			BlockEntity entity = ((BlockEntityProvider) state.getBlock()).createBlockEntity(world);
			if (entity != null && BlockEntityRenderDispatcher.INSTANCE.get(entity) != null)
			{
				entity.setLocation(world, piston.getPos());
				((AccessorBlockEntity) entity).setCachedState(state);
				BlockEntityRenderDispatcher.INSTANCE.render(entity, f, matrices, provider);
				entity.markRemoved();
			}
		}
	}
}