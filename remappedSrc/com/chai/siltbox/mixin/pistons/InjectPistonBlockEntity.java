package com.chai.siltbox.mixin.pistons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.Main;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PistonBlockEntity.class)
public class InjectPistonBlockEntity
{
	@Inject(
		method = "finish",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;updateNeighbor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onFinish(CallbackInfo info)
	{
		copyBlockEntityTags((PistonBlockEntity) (Object) this);
	}

	@Inject(
		method = "tick",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/World;updateNeighbor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;Lnet/minecraft/util/math/BlockPos;)V"))
	private void onTick(CallbackInfo info)
	{
		copyBlockEntityTags((PistonBlockEntity) (Object) this);
	}

	private void copyBlockEntityTags(PistonBlockEntity piston)
	{
		World world = piston.getWorld();
		if (world == null) { return; }

		BlockPos pos = piston.getPos();
		BlockState state = world.getBlockState(pos);
		if (world.isClient)
		{
			world.updateListeners(pos, state, state, 2);
			return;
		}

		CompoundTag tag = Main.blockEntityTags.get(piston);
		if (tag == null) { return; }
		tag.putInt("x", pos.getX());
		tag.putInt("y", pos.getY());
		tag.putInt("z", pos.getZ());

		BlockEntity entity = world.getBlockEntity(pos);
		if (entity == null) { return; }
		entity.fromTag(state, tag);
		((ServerWorld) world).getChunkManager().markForUpdate(pos);
	}
}