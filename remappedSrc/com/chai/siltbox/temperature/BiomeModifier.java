package com.chai.siltbox.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class BiomeModifier extends TemperatureModifier
{
	public static float getExternal(PlayerEntity player)
	{
		float temp = 0f;
		RegistryKey<Biome> biome = player.world.method_31081(player.getBlockPos()).get();

		if (biome == BiomeKeys.SNOWY_TUNDRA)
		{
			temp = -7f;
		}
		else if (biome == BiomeKeys.ICE_SPIKES)
		{
			temp = -8f;
		}
		else if (biome == BiomeKeys.SNOWY_TAIGA ||
					biome == BiomeKeys.SNOWY_TAIGA_HILLS)
		{
			temp = -6f;
		}
		else if (biome == BiomeKeys.SNOWY_TAIGA_MOUNTAINS)
		{
			temp = -7f;
		}
		else if (biome == BiomeKeys.SNOWY_BEACH)
		{
			temp = -5f;
		}
		else if (biome == BiomeKeys.FROZEN_RIVER)
		{
			temp = -5.5f;
		}
		else if (biome == BiomeKeys.MOUNTAINS ||
					biome == BiomeKeys.MOUNTAIN_EDGE ||
					biome == BiomeKeys.WOODED_MOUNTAINS ||
					biome == BiomeKeys.GRAVELLY_MOUNTAINS ||
					biome == BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS)
		{
			temp = -4.5f;
		}
		else if (biome == BiomeKeys.TAIGA ||
					biome == BiomeKeys.TAIGA_HILLS ||
					biome == BiomeKeys.GIANT_SPRUCE_TAIGA ||
					biome == BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS ||
					biome == BiomeKeys.GIANT_TREE_TAIGA ||
					biome == BiomeKeys.GIANT_TREE_TAIGA_HILLS)
		{
			temp = -3f;
		}
		else if (biome == BiomeKeys.TAIGA_MOUNTAINS)
		{
			temp = -4f;
		}
		else if (biome == BiomeKeys.STONE_SHORE)
		{
			temp = -3f;
		}
		else if (biome == BiomeKeys.PLAINS)
		{
			temp = -1f;
		}
		else if (biome == BiomeKeys.SUNFLOWER_PLAINS)
		{
			temp = 0f;
		}
		else if (biome == BiomeKeys.FOREST ||
					biome == BiomeKeys.BIRCH_FOREST ||
					biome == BiomeKeys.BIRCH_FOREST_HILLS ||
					biome == BiomeKeys.TALL_BIRCH_FOREST)
		{
			temp = -1.5f;
		}
		else if (biome == BiomeKeys.FLOWER_FOREST)
		{
			temp = -0.5f;
		}
		else if (biome == BiomeKeys.DARK_FOREST ||
					biome == BiomeKeys.DARK_FOREST_HILLS)
		{
			temp = -2f;
		}
		else if (biome == BiomeKeys.SWAMP ||
					biome == BiomeKeys.SWAMP_HILLS)
		{
			temp = 2f;
		}
		else if (biome == BiomeKeys.BEACH)
		{
			temp = 1f;
		}
		else if (biome == BiomeKeys.RIVER)
		{
			temp = -1f;
		}
		else if (biome == BiomeKeys.JUNGLE_EDGE ||
					biome == BiomeKeys.MODIFIED_JUNGLE_EDGE)
		{
			temp = 3f;
		}
		else if (biome == BiomeKeys.JUNGLE ||
					biome == BiomeKeys.JUNGLE_HILLS ||
					biome == BiomeKeys.MODIFIED_JUNGLE ||
					biome == BiomeKeys.BAMBOO_JUNGLE ||
					biome == BiomeKeys.BAMBOO_JUNGLE_HILLS)
		{
			temp = 4f;
		}
		else if (biome == BiomeKeys.MUSHROOM_FIELDS)
		{
			temp = 0f;
		}
		else if (biome == BiomeKeys.MUSHROOM_FIELD_SHORE)
		{
			temp = 1f;
		}
		else if (biome == BiomeKeys.SAVANNA)
		{
			temp = 4.5f;
		}
		else if (biome == BiomeKeys.SAVANNA_PLATEAU ||
					biome == BiomeKeys.SHATTERED_SAVANNA ||
					biome == BiomeKeys.SHATTERED_SAVANNA_PLATEAU)
		{
			temp = 5.5f;
		}
		else if (biome == BiomeKeys.DESERT_LAKES)
		{
			temp = 4f;
		}
		else if (biome == BiomeKeys.DESERT ||
					biome == BiomeKeys.DESERT_HILLS)
		{
			temp = 5f;
		}
		else if (biome == BiomeKeys.BADLANDS ||
					biome == BiomeKeys.ERODED_BADLANDS)
		{
			temp = 6f;
		}
		else if (biome == BiomeKeys.WOODED_BADLANDS_PLATEAU ||
					biome == BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU)
		{
			temp = 6.5f;
		}
		else if (biome == BiomeKeys.BADLANDS_PLATEAU ||
					biome == BiomeKeys.MODIFIED_BADLANDS_PLATEAU)
		{
			temp = 7f;
		}
		else if (biome == BiomeKeys.WARM_OCEAN ||
					biome == BiomeKeys.DEEP_WARM_OCEAN)
		{
			temp = 2f;
		}
		else if (biome == BiomeKeys.LUKEWARM_OCEAN ||
					biome == BiomeKeys.DEEP_LUKEWARM_OCEAN)
		{
			temp = 0f;
		}
		else if (biome == BiomeKeys.OCEAN ||
					biome == BiomeKeys.OCEAN)
		{
			temp = -2f;
		}
		else if (biome == BiomeKeys.COLD_OCEAN ||
					biome == BiomeKeys.DEEP_COLD_OCEAN)
		{
			temp = -4f;
		}
		else if (biome == BiomeKeys.FROZEN_OCEAN ||
					biome == BiomeKeys.DEEP_FROZEN_OCEAN)
		{
			temp = -6f;
		}
		else if (biome == BiomeKeys.NETHER_WASTES)
		{
			temp = 8f;
		}
		else if (biome == BiomeKeys.SOUL_SAND_VALLEY)
		{
			temp = 6f;
		}
		else if (biome == BiomeKeys.CRIMSON_FOREST)
		{
			temp = 7f;
		}
		else if (biome == BiomeKeys.WARPED_FOREST)
		{
			temp = 6f;
		}
		else if (biome == BiomeKeys.BASALT_DELTAS)
		{
			temp = 7f;
		}
		else if (biome == BiomeKeys.THE_END)
		{
			temp = -2f;
		}
		else if (biome == BiomeKeys.SMALL_END_ISLANDS)
		{
			temp = -5f;
		}
		else if (biome == BiomeKeys.END_BARRENS)
		{
			temp = -4f;
		}
		else if (biome == BiomeKeys.END_MIDLANDS)
		{
			temp = -3f;
		}
		else if (biome == BiomeKeys.END_HIGHLANDS)
		{
			temp = -2f;
		}

		return temp;
	}
}