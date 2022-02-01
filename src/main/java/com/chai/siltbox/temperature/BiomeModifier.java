package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class BiomeModifier extends TemperatureModifier
{
	private static float prevLow = 0f;
	private static float prevHigh = 0f;

	private static float getTemp(float low, float high, int time)
	{
		return high - (high-low) * (time / 11f);
	}

	public static float getExternal(PlayerEntity player)
	{
		RegistryKey<Biome> biome = player.world.method_31081(player.getBlockPos()).get();
		float low = 0f;
		float high = 0f;

		// Temperate/lush biomes
		if (biome == BiomeKeys.PLAINS) { low = -1.5f; high = 0f; }
		else if (biome == BiomeKeys.SUNFLOWER_PLAINS) { low = -1f; high = 1f; }
		else if (biome == BiomeKeys.FOREST ||
					biome == BiomeKeys.WOODED_HILLS) { low = -1.5f; high = 0f; }
		else if (biome == BiomeKeys.FLOWER_FOREST) { low = -1f; high = 1f; }
		else if (biome == BiomeKeys.BIRCH_FOREST ||
					biome == BiomeKeys.BIRCH_FOREST_HILLS ||
					biome == BiomeKeys.TALL_BIRCH_FOREST ||
					biome == BiomeKeys.TALL_BIRCH_HILLS) { low = -2f; high = -0.5f; }
		else if (biome == BiomeKeys.DARK_FOREST ||
					biome == BiomeKeys.DARK_FOREST_HILLS) { low = -2.5f; high = -1f; }
		else if (biome == BiomeKeys.SWAMP ||
					biome == BiomeKeys.SWAMP_HILLS) { low = 0f; high = 2f; }
		else if (biome == BiomeKeys.JUNGLE_EDGE ||
					biome == BiomeKeys.MODIFIED_JUNGLE_EDGE) { low = 1f; high = 3f; }
		else if (biome == BiomeKeys.JUNGLE ||
					biome == BiomeKeys.JUNGLE_HILLS ||
					biome == BiomeKeys.MODIFIED_JUNGLE ||
					biome == BiomeKeys.BAMBOO_JUNGLE ||
					biome == BiomeKeys.BAMBOO_JUNGLE_HILLS) { low = 2.5f; high = 4f; }
		else if (biome == BiomeKeys.RIVER) { low = 0F; high = 0F; }
		else if (biome == BiomeKeys.BEACH) { low = -0.5f; high = 1.5f; }
		else if (biome == BiomeKeys.MUSHROOM_FIELDS) { low = -1f; high = -0.5f; }
		else if (biome == BiomeKeys.MUSHROOM_FIELD_SHORE) { low = -1.5f; high = -1f; }
		// Dry/warm biomes
		else if (biome == BiomeKeys.DESERT ||
					biome == BiomeKeys.DESERT_HILLS) { low = -3f; high = 5f; }
		else if (biome == BiomeKeys.DESERT_LAKES) { low = -1f; high = 4f; }
		else if (biome == BiomeKeys.SAVANNA ||
					biome == BiomeKeys.SHATTERED_SAVANNA) { low = 3f; high = 4f; }
		else if (biome == BiomeKeys.SAVANNA_PLATEAU ||
					biome == BiomeKeys.SHATTERED_SAVANNA_PLATEAU) { low = 3.5f; high = 4.5f; }
		else if (biome == BiomeKeys.BADLANDS ||
					biome == BiomeKeys.ERODED_BADLANDS) { low = 3f; high = 5.5f; }
		else if (biome == BiomeKeys.BADLANDS_PLATEAU ||
					biome == BiomeKeys.MODIFIED_BADLANDS_PLATEAU) { low = 3.5f; high = 6f; }
		else if (biome == BiomeKeys.WOODED_BADLANDS_PLATEAU ||
					biome == BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU) { low = 2.5f; high = 5f; }
		// Oceans
		else if (biome == BiomeKeys.WARM_OCEAN ||
					biome == BiomeKeys.DEEP_WARM_OCEAN) { low = 2f; high = 2f; }
		else if (biome == BiomeKeys.LUKEWARM_OCEAN ||
					biome == BiomeKeys.DEEP_LUKEWARM_OCEAN) { low = 0f; high = 0f; }
		else if (biome == BiomeKeys.OCEAN ||
					biome == BiomeKeys.DEEP_OCEAN) { low = -2f; high = -2f; }
		else if (biome == BiomeKeys.COLD_OCEAN ||
					biome == BiomeKeys.DEEP_COLD_OCEAN) { low = -4f; high = -4f; }
		else if (biome == BiomeKeys.FROZEN_OCEAN ||
					biome == BiomeKeys.DEEP_FROZEN_OCEAN) { low = -6f; high = -6f; }
		// Cold biomes
		else if (biome == BiomeKeys.MOUNTAINS ||
					biome == BiomeKeys.GRAVELLY_MOUNTAINS ||
					biome == BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS ||
					biome == BiomeKeys.WOODED_MOUNTAINS ||
					biome == BiomeKeys.TAIGA_MOUNTAINS) { low = -5f; high = -3.5f; }
		else if (biome == BiomeKeys.TAIGA ||
					biome == BiomeKeys.TAIGA_HILLS ||
					biome == BiomeKeys.GIANT_SPRUCE_TAIGA ||
					biome == BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS ||
					biome == BiomeKeys.GIANT_TREE_TAIGA ||
					biome == BiomeKeys.GIANT_TREE_TAIGA_HILLS)  { low = -5f; high = -3.5f; }
		else if (biome == BiomeKeys.MOUNTAIN_EDGE ||
					biome == BiomeKeys.STONE_SHORE) { low = -4f; high = -2.5f; }
		// Snowy biomes
		else if (biome == BiomeKeys.SNOWY_TAIGA ||
					biome == BiomeKeys.SNOWY_TAIGA_HILLS) { low = -6.5f; high = -5f; }
		else if (biome == BiomeKeys.SNOWY_TAIGA_MOUNTAINS) { low = -7f; high = -5f; }
		else if (biome == BiomeKeys.SNOWY_TUNDRA) { low = -8f; high = -6.5f; }
		else if (biome == BiomeKeys.ICE_SPIKES) { low = -9f; high = -7f; }
		else if (biome == BiomeKeys.SNOWY_BEACH) { low = -6f; high = -5f; }
		else if (biome == BiomeKeys.FROZEN_RIVER) { low = -5f; high = -5f; }
		// Nether
		else if (biome == BiomeKeys.WARPED_FOREST) { low = 5f; high = 5f; }
		else if (biome == BiomeKeys.CRIMSON_FOREST) { low = 5.5f; high = 5.5f; }
		else if (biome == BiomeKeys.SOUL_SAND_VALLEY) { low = 6f; high = 6f; }
		else if (biome == BiomeKeys.BASALT_DELTAS) { low = 6.5f; high = 6.5f; }
		else if (biome == BiomeKeys.NETHER_WASTES) { low = 7f; high = 7f; }
		// End
		else if (biome == BiomeKeys.THE_END) { low = -3f; high = -3f; }
		else if (biome == BiomeKeys.SMALL_END_ISLANDS) { low = -5f; high = -5f; }
		else if (biome == BiomeKeys.END_BARRENS) { low = -5f; high = -5f; }
		else if (biome == BiomeKeys.END_MIDLANDS) { low = -4f; high = -4f; }
		else if (biome == BiomeKeys.END_HIGHLANDS) { low = -3f; high = -3f; }

		prevLow = low;
		prevHigh = high;

		return getTemp(low, high, player.world.getAmbientDarkness());
	}
}