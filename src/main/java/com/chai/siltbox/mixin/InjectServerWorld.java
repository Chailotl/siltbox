package com.chai.siltbox.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class InjectServerWorld extends World
{
	protected InjectServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef,
		DimensionType dimensionType, Supplier<Profiler> profiler,  boolean isClient, boolean debugWorld, long seed)
	{
		super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
	}

	@Inject(
		method = "tickChunk",
		at = @At("TAIL"))
	private void snowUnderLeaves(WorldChunk chunk,
		int randomTickSpeed, CallbackInfo info)
	{
		if (isRaining() && random.nextInt(16) == 0)
		{
			ChunkPos chunkPos = chunk.getPos();
			int i = chunkPos.getStartX();
			int j = chunkPos.getStartZ();

			BlockPos pos = getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, getRandomPosInChunk(i, 0, j, 15));
			Biome biome = getBiome(pos);

			if (biome.canSetSnow(this, pos))
			{
				setBlockState(pos, Blocks.SNOW.getDefaultState());
			}
		}
	}
}