package com.chai.siltbox.item;

import java.util.Set;

import com.chai.siltbox.mixin.AccessorMiningToolItem;
import com.google.common.collect.Sets;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;

public class MattockItem extends MiningToolItem
{
	private static final Set<Material> MATERIALS;
	private static final Set<Block> EFFECTIVE_BLOCKS;

	public MattockItem(ToolMaterial material)
	{
		super(1f, -3f, material, EFFECTIVE_BLOCKS, new FabricItemSettings().group(ItemGroup.TOOLS));
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		if (Items.IRON_SHOVEL.isEffectiveOn(state))
		{
			return true;
		}

		int i = this.getMaterial().getMiningLevel();
		if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.CRYING_OBSIDIAN) && !state.isOf(Blocks.NETHERITE_BLOCK) && !state.isOf(Blocks.RESPAWN_ANCHOR) && !state.isOf(Blocks.ANCIENT_DEBRIS))
		{
			if (!state.isOf(Blocks.DIAMOND_BLOCK) && !state.isOf(Blocks.DIAMOND_ORE) && !state.isOf(Blocks.EMERALD_ORE) && !state.isOf(Blocks.EMERALD_BLOCK) && !state.isOf(Blocks.GOLD_BLOCK) && !state.isOf(Blocks.GOLD_ORE) && !state.isOf(Blocks.REDSTONE_ORE))
			{
				if (!state.isOf(Blocks.IRON_BLOCK) && !state.isOf(Blocks.IRON_ORE) && !state.isOf(Blocks.LAPIS_BLOCK) && !state.isOf(Blocks.LAPIS_ORE))
				{
					Material material = state.getMaterial();
					return material == Material.STONE || material == Material.METAL || material == Material.REPAIR_STATION || state.isOf(Blocks.NETHER_GOLD_ORE);
				} else { return i >= 1; }
			} else { return i >= 2; }
		} else { return i >= 3; }
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return MATERIALS.contains(material) ? this.miningSpeed : super.getMiningSpeedMultiplier(stack, state);
	}

	static {
		MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT,
			Material.BAMBOO, Material.GOURD, Material.METAL, Material.REPAIR_STATION, Material.STONE);

		EFFECTIVE_BLOCKS = Sets.newHashSet();
		EFFECTIVE_BLOCKS.addAll(((AccessorMiningToolItem) Items.IRON_PICKAXE).getEffectiveBlocks());
		EFFECTIVE_BLOCKS.addAll(((AccessorMiningToolItem) Items.IRON_AXE).getEffectiveBlocks());
		EFFECTIVE_BLOCKS.addAll(((AccessorMiningToolItem) Items.IRON_SHOVEL).getEffectiveBlocks());
	}
}
