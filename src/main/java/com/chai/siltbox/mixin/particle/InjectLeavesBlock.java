package com.chai.siltbox.mixin.particle;

import com.chai.siltbox.Main;
import net.minecraft.block.*;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(LeavesBlock.class)
public class InjectLeavesBlock
{
	private static List<BlockPos> markedForRemoval = new ArrayList<>();

	@Shadow
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) { }
	@Shadow
	private static int getDistanceFromLog(BlockState state) { throw new AssertionError(); }

	private static boolean canFallThrough(BlockState state)
	{
		Material material = state.getMaterial();
		return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
	}

	@Inject(
		method = "scheduledTick",
		at = @At("TAIL"))
	private void breakLeaves(BlockState state, ServerWorld world,
		BlockPos pos, Random random, CallbackInfo info)
	{
		// Get updated state
		state = world.getBlockState(pos);

		if (!state.get(LeavesBlock.PERSISTENT) && state.get(LeavesBlock.DISTANCE) == 7)
		{
			if (markedForRemoval.contains(pos))
			{
				markedForRemoval.remove(pos);
				randomTick(state, world, pos, random);

				// Check for stragglers
				Iterator<BlockPos> i = markedForRemoval.iterator();
				while (i.hasNext())
				{
					if (!(world.getBlockState(i.next()).getBlock() instanceof LeavesBlock))
					{
						i.remove();
					}
				}
			}
			else
			{
				markedForRemoval.add(pos);
				world.getBlockTickScheduler().schedule(pos, state.getBlock(), world.getRandom().nextInt(40) + 20);
			}
		}
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