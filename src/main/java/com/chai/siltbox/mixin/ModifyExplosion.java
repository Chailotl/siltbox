package com.chai.siltbox.mixin;

import com.chai.siltbox.SiltBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Explosion.class)
public class ModifyExplosion
{
	@ModifyArg(
		method = "affectWorld",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"),
		index = 1)
	private BlockState useSafeFire(BlockState prevState)
	{
		return SiltBlocks.SAFE_FIRE.getDefaultState();
	}
}