package com.chai.siltbox.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(NoteBlock.class)
public abstract class InjectNoteBlock
{
	@Shadow
	public static IntProperty NOTE;

	@Shadow
	protected abstract void playNote(World world, BlockPos pos);

	@Inject(
		method = "onUse",
		at = @At("HEAD"),
		cancellable = true)
	private void reverseTune(BlockState state, World world, BlockPos pos, PlayerEntity player,
		Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info)
	{
		if (!world.isClient() && player.isSneaking())
		{
			int note = state.get(NOTE) - 1;
			if (note == -1) { note = 24; }
			state = state.with(NOTE, note);
			world.setBlockState(pos, state, 3);
			playNote(world, pos);
			player.incrementStat(Stats.TUNE_NOTEBLOCK);
			info.setReturnValue(ActionResult.CONSUME);
		}
	}
}