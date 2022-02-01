package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.chai.siltbox.Main;

import net.minecraft.block.BlockState;
import net.minecraft.entity.projectile.SmallFireballEntity;

@Mixin(SmallFireballEntity.class)
public class ModifySmallFireballEntity
{
	@ModifyArg(method = "onBlockHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"), index = 1)
	private BlockState useSafeFire(BlockState prevState)
	{
		return Main.SAFE_FIRE.getDefaultState();
	}
}