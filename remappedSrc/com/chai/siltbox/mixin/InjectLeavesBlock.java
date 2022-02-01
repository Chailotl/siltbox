package com.chai.siltbox.mixin;

import java.awt.Color;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.Main;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.Material;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(LeavesBlock.class)
public class InjectLeavesBlock
{
	private static boolean canFallThrough(BlockState state)
	{
		Material material = state.getMaterial();
		return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
	}

	@Inject(
		method = "randomDisplayTick",
		at = @At("HEAD"))
	private void dropLeaves(BlockState state, World world,
		BlockPos pos, Random random, CallbackInfo info)
	{
		if (random.nextInt(60) == 0)
		{
			BlockPos blockPos = pos.down();
			if (canFallThrough(world.getBlockState(blockPos)))
			{
				double x = pos.getX() + 0.02d + random.nextDouble() * 0.96d;
				double y = pos.getY() - 0.05d;
				double z = pos.getZ() + 0.02d + random.nextDouble() * 0.96d;

				ParticleEffect particle = Main.OAK_LEAF;
				Color color = new Color(BiomeColors.getFoliageColor(world, pos));

				Block block = state.getBlock();
				if (block == Blocks.BIRCH_LEAVES)
				{
					particle = Main.BIRCH_LEAF;
					color = new Color(FoliageColors.getBirchColor());
				}
				else if (block == Blocks.SPRUCE_LEAVES)
				{
					particle = Main.SPRUCE_LEAF;
					color = new Color(FoliageColors.getSpruceColor());
				}
				else if (block == Blocks.JUNGLE_LEAVES)
				{
					particle = Main.JUNGLE_LEAF;
				}
				else if (block == Blocks.ACACIA_LEAVES)
				{
					particle = Main.ACACIA_LEAF;
				}

				world.addParticle(particle, x, y, z, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
			}
		}
	}
}