package com.chai.siltbox.mixin.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@Mixin(Block.class)
public interface InvokerBlock
{
	@Invoker("dropExperience")
	public void dropXP(ServerWorld world, BlockPos pos, int size);
}
