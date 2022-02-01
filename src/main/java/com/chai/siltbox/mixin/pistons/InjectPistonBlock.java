package com.chai.siltbox.mixin.pistons;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.chai.siltbox.Main;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(PistonBlock.class)
public class InjectPistonBlock
{
	private CompoundTag blockEntityTag = null;

	@Redirect(
		method = "isMovable",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/Block;hasBlockEntity()Z"))
	private static boolean pushBlockEntites(Block block)
	{
		return false;
	}

	@Inject(
		method = "move",
		at = @At(value = "INVOKE_ASSIGN",
		target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
		ordinal = 3),
		locals = LocalCapture.CAPTURE_FAILHARD)
	private void captureBlockEntityTag(World world, BlockPos pos, Direction dir,
		boolean retract, CallbackInfoReturnable<Boolean> info,
		BlockPos blockPos, PistonHandler pistonHandler, Map map, List list, List list2,
		List list3, BlockState[] blockStates, Direction direction, int j, int l, BlockPos blockPos4)
	{
		BlockEntity entity = world.getBlockEntity(blockPos4);
		world.removeBlockEntity(blockPos4);
		if (entity != null)
		{
			blockEntityTag = entity.toTag(new CompoundTag());
		}
	}

	@Redirect(
		method = "move",
		at = @At(value = "INVOKE",
		target = "Lnet/minecraft/block/PistonExtensionBlock;createBlockEntityPiston(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;ZZ)Lnet/minecraft/block/entity/BlockEntity;",
		ordinal = 0))
	private BlockEntity captureBlockEntityPiston(BlockState pushedBlock, Direction dir, boolean extending, boolean bl)
	{
		PistonBlockEntity entity = (PistonBlockEntity) PistonExtensionBlock.createBlockEntityPiston(pushedBlock, dir, extending, bl);
		Main.blockEntityTags.put(entity, blockEntityTag);
		return entity;
	}
}