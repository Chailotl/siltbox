package com.chai.siltbox;

import com.chai.siltbox.block.*;
import com.chai.siltbox.item.RopeItem;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class SiltBlocks
{
	public static final Map<String, Identifier> STONE_PATH_TEXTURES = new HashMap<>();

	private static Block register(String name, Block block)
	{
		return Registry.register(Registry.BLOCK, new Identifier(Main.MOD_ID, name), block);
	}

	private static Block register(String name, Block block, FabricItemSettings settings)
	{
		Identifier id = new Identifier(Main.MOD_ID, name);

		Registry.register(Registry.ITEM, id, new BlockItem(block, settings));
		return Registry.register(Registry.BLOCK, id, block);
	}

	private static Block registerWoodPanel(String name, Block block)
	{
		return register(name, new PanelBlock(FabricBlockSettings
				.of(Material.WOOD, block.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));
	}
	private static Block registerWoolSlab(String name, Block block)
	{
		return register(name, new SlabBlock(FabricBlockSettings
				.of(Material.WOOL, block.getDefaultMaterialColor())
				.strength(0.8f).sounds(BlockSoundGroup.WOOL).breakByTool(FabricToolTags.SHEARS)),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));
	}

	private static Block registerStonePath(String name, Block block, String texture)
	{
		Identifier id = Registry.BLOCK.getId(block);
		STONE_PATH_TEXTURES.put(name, new Identifier(texture));

		return register(name, new PanelBlock(FabricBlockSettings
				.of(Material.STONE, block.getDefaultMaterialColor())
				.strength(0.5f).sounds(block.getSoundGroup(block.getDefaultState()))
				.breakByTool(FabricToolTags.PICKAXES).nonOpaque()),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));
	}

	private static Block registerMetalPlate(String name, Block block)
	{
		return register(name, new PanelBlock(FabricBlockSettings
				.of(Material.METAL, block.getDefaultMaterialColor())
				.strength(0.5f).sounds(block.getSoundGroup(block.getDefaultState()))
				.breakByTool(FabricToolTags.PICKAXES).requiresTool()),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));
	}

	private static Block registerLeafPile(String name)
	{
		Block newBlock = register(name, new PanelBlock(FabricBlockSettings
				.of(Material.LEAVES).strength(0.1f).sounds(BlockSoundGroup.GRASS)
				.nonOpaque().suffocates((s, w, p) -> false).blockVision((s, w, p) -> false)),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));

		BlockRenderLayerMap.INSTANCE.putBlock(newBlock, RenderLayer.getCutout());
		return newBlock;
	}

	private static Block registerPost(String name, Block block)
	{
		return register(name, new PostBlock(FabricBlockSettings
				.of(Material.WOOD, block.getDefaultMaterialColor())
				.strength(2f, 3f).sounds(BlockSoundGroup.WOOD)
				.breakByTool(FabricToolTags.AXES)),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));
	}

	private static Block registerHedge(String name, Block block)
	{
		Block newBlock = register(name, new WallBlock(AbstractBlock.Settings.copy(block)),
			new FabricItemSettings().group(ItemGroup.DECORATIONS));

		BlockRenderLayerMap.INSTANCE.putBlock(newBlock, RenderLayer.getCutout());
		return newBlock;
	}

	// Redstone
	public static final Block IRON_BUTTON = register("iron_button",
		new GoldButtonBlock(FabricBlockSettings.of(Material.SUPPORTED, MaterialColor.IRON)
			.strength(0.5f).breakByTool(FabricToolTags.PICKAXES).requiresTool().noCollision()),
		new FabricItemSettings().group(ItemGroup.REDSTONE));
	public static final Block GOLD_BUTTON = register("gold_button",
		new GoldButtonBlock(FabricBlockSettings.of(Material.SUPPORTED, MaterialColor.GOLD)
			.strength(0.5f).breakByTool(FabricToolTags.PICKAXES).requiresTool().noCollision()),
		new FabricItemSettings().group(ItemGroup.REDSTONE));
	public static final Block PLAYER_PRESSURE_PLATE = register("player_pressure_plate",
		new PlayerPressurePlateBlock(FabricBlockSettings.of(Material.STONE)
			.strength(0.5f).breakByTool(FabricToolTags.PICKAXES).requiresTool()
			.sounds(BlockSoundGroup.STONE).noCollision()),
		new FabricItemSettings().group(ItemGroup.REDSTONE));

	// Functional
	public static final Block BAMBOO_SPIKES = register("bamboo_spikes",
		new BambooSpikesBlock(FabricBlockSettings.of(Material.SUPPORTED, MaterialColor.FOLIAGE)
			.strength(1f).breakByTool(FabricToolTags.AXES).sounds(BlockSoundGroup.BAMBOO)),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	public static final Block STRAINER = register("strainer",
		new StrainerBlock(FabricBlockSettings.of(Material.BAMBOO, MaterialColor.SAND)
			.strength(0.5f).breakByTool(FabricToolTags.AXES).sounds(BlockSoundGroup.BAMBOO)
			.nonOpaque().ticksRandomly()),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	public static final Block ROPE = register("rope",
		new RopeBlock(FabricBlockSettings.of(Material.WOOL, MaterialColor.BROWN)
			.noCollision().breakInstantly()));

	public static final Block PET_BED = register("pet_bed",
		new PetBedBlock(DyeColor.RED, FabricBlockSettings.of(Material.WOOL, MaterialColor.RED)),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	public static final Block SLEEPING_BAG = register("sleeping_bag_red",
		new SleepingBagBlock(DyeColor.RED, FabricBlockSettings.of(Material.WOOL, MaterialColor.RED)
			.sounds(BlockSoundGroup.WOOL).strength(0.2f).nonOpaque()),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	// Decorative
	public static final Block OAK_PANEL = registerWoodPanel("oak_panel", Blocks.OAK_PLANKS);
	public static final Block SPRUCE_PANEL = registerWoodPanel("spruce_panel", Blocks.SPRUCE_PLANKS);
	public static final Block BIRCH_PANEL = registerWoodPanel("birch_panel", Blocks.BIRCH_PLANKS);
	public static final Block JUNGLE_PANEL = registerWoodPanel("jungle_panel", Blocks.JUNGLE_PLANKS);
	public static final Block ACACIA_PANEL = registerWoodPanel("acacia_panel", Blocks.ACACIA_PLANKS);
	public static final Block DARK_OAK_PANEL = registerWoodPanel("dark_oak_panel", Blocks.DARK_OAK_PLANKS);
	public static final Block CRIMSON_PANEL = registerWoodPanel("crimson_panel", Blocks.CRIMSON_PLANKS);
	public static final Block WARPED_PANEL = registerWoodPanel("warped_panel", Blocks.WARPED_PLANKS);

	public static final Block IRON_PLATE = registerMetalPlate("iron_plate", Blocks.IRON_BLOCK);
	public static final Block GOLD_PLATE = registerMetalPlate("gold_plate", Blocks.GOLD_BLOCK);
	public static final Block NETHERITE_PLATE = registerMetalPlate("netherite_plate", Blocks.NETHERITE_BLOCK);

	public static final Block OAK_LEAF_PILE = registerLeafPile("oak_leaf_pile");
	public static final Block SPRUCE_LEAF_PILE = registerLeafPile("spruce_leaf_pile");
	public static final Block BIRCH_LEAF_PILE = registerLeafPile("birch_leaf_pile");
	public static final Block JUNGLE_LEAF_PILE = registerLeafPile("jungle_leaf_pile");
	public static final Block ACACIA_LEAF_PILE = registerLeafPile("acacia_leaf_pile");
	public static final Block DARK_OAK_LEAF_PILE = registerLeafPile("dark_oak_leaf_pile");

	public static final Block OAK_POST = registerPost("oak_post", Blocks.OAK_PLANKS);
	public static final Block SPRUCE_POST = registerPost("spruce_post", Blocks.SPRUCE_PLANKS);
	public static final Block BIRCH_POST = registerPost("birch_post", Blocks.BIRCH_PLANKS);
	public static final Block JUNGLE_POST = registerPost("jungle_post", Blocks.JUNGLE_PLANKS);
	public static final Block ACACIA_POST = registerPost("acacia_post", Blocks.ACACIA_PLANKS);
	public static final Block DARK_OAK_POST = registerPost("dark_oak_post", Blocks.DARK_OAK_PLANKS);
	public static final Block CRIMSON_POST = registerPost("crimson_post", Blocks.CRIMSON_PLANKS);
	public static final Block WARPED_POST = registerPost("warped_post", Blocks.WARPED_PLANKS);

	public static final Block OAK_LOG_POST = registerPost("oak_log_post", Blocks.OAK_LOG);

	public static final Block OAK_HEDGE = registerHedge("oak_hedge", Blocks.OAK_LEAVES);
	public static final Block SPRUCE_HEDGE = registerHedge("spruce_hedge", Blocks.SPRUCE_LEAVES);
	public static final Block BIRCH_HEDGE = registerHedge("birch_hedge", Blocks.BIRCH_LEAVES);
	public static final Block JUNGLE_HEDGE = registerHedge("jungle_hedge", Blocks.JUNGLE_LEAVES);
	public static final Block ACACIA_HEDGE = registerHedge("acacia_hedge", Blocks.ACACIA_LEAVES);
	public static final Block DARK_OAK_HEDGE = registerHedge("dark_oak_hedge", Blocks.DARK_OAK_LEAVES);

	public static final Block PAPER_SCREEN = register("paper_screen",
		new PaneBlock(FabricBlockSettings.of(Material.BAMBOO, MaterialColor.WHITE)
			.strength(0.5f).breakByTool(FabricToolTags.AXES)),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	public static final Block SMALL_OAK_LOG = register("small_oak_log",
		new SmallLogBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WOOD)
			.strength(2f).breakByTool(FabricToolTags.AXES).sounds(BlockSoundGroup.WOOD).nonOpaque()),
		new FabricItemSettings().group(ItemGroup.DECORATIONS));

	public static final Block STONE_PATH = registerStonePath("stone_path", Blocks.STONE, "minecraft:block/stone");
	public static final Block SMOOTH_STONE_PATH = registerStonePath("smooth_stone_path", Blocks.SMOOTH_STONE, "minecraft:block/smooth_stone");
	public static final Block COBBLESTONE_PATH = registerStonePath("cobblestone_path", Blocks.COBBLESTONE, "minecraft:block/cobblestone");
	public static final Block MOSSY_COBBLESTONE_PATH = registerStonePath("mossy_cobblestone_path", Blocks.MOSSY_COBBLESTONE, "minecraft:block/mossy_cobblestone");
	public static final Block GRANITE_PATH = registerStonePath("granite_path", Blocks.GRANITE, "minecraft:block/granite");
	public static final Block DIORITE_PATH = registerStonePath("diorite_path", Blocks.DIORITE, "minecraft:block/diorite");
	public static final Block ANDESITE_PATH = registerStonePath("andesite_path", Blocks.ANDESITE, "minecraft:block/andesite");
	public static final Block POLISHED_GRANITE_PATH = registerStonePath("polished_granite_path", Blocks.POLISHED_GRANITE, "minecraft:block/polished_granite");
	public static final Block POLISHED_DIORITE_PATH = registerStonePath("polished_diorite_path", Blocks.POLISHED_DIORITE, "minecraft:block/polished_diorite");
	public static final Block POLISHED_ANDESITE_PATH = registerStonePath("polished_andesite_path", Blocks.POLISHED_ANDESITE, "minecraft:block/polished_andesite");
	public static final Block SANDSTONE_PATH = registerStonePath("sandstone_path", Blocks.SANDSTONE, "minecraft:block/sandstone_bottom");
	public static final Block RED_SANDSTONE_PATH = registerStonePath("red_sandstone_path", Blocks.RED_SANDSTONE, "minecraft:block/red_sandstone_bottom");
	public static final Block SMOOTH_SANDSTONE_PATH = registerStonePath("smooth_sandstone_path", Blocks.SANDSTONE, "minecraft:block/sandstone_top");
	public static final Block SMOOTH_RED_SANDSTONE_PATH = registerStonePath("smooth_red_sandstone_path", Blocks.RED_SANDSTONE, "minecraft:block/red_sandstone_top");
	public static final Block PRISMARINE_PATH = registerStonePath("prismarine_path", Blocks.PRISMARINE, "minecraft:block/prismarine");
	public static final Block BLACKSTONE_PATH = registerStonePath("blackstone_path", Blocks.BLACKSTONE, "minecraft:block/blackstone");
	public static final Block POLISHED_BLACKSTONE_PATH = registerStonePath("polished_blackstone_path", Blocks.POLISHED_BLACKSTONE, "minecraft:block/polished_blackstone");
	public static final Block END_STONE_PATH = registerStonePath("end_stone_path", Blocks.END_STONE, "minecraft:block/end_stone");


	public static final Block WHITE_WOOL_SLAB = registerWoolSlab("white_wool_slab", Blocks.WHITE_WOOL);
	public static final Block ORANGE_WOOL_SLAB = registerWoolSlab("orange_wool_slab", Blocks.ORANGE_WOOL);
	public static final Block MAGENTA_WOOL_SLAB = registerWoolSlab("magenta_wool_slab", Blocks.MAGENTA_WOOL);
	public static final Block YELLOW_WOOL_SLAB = registerWoolSlab("yellow_wool_slab", Blocks.YELLOW_WOOL);
	public static final Block LIGHT_BLUE_WOOL_SLAB = registerWoolSlab("light_blue_wool_slab", Blocks.LIGHT_BLUE_WOOL);
	public static final Block LIME_WOOL_SLAB = registerWoolSlab("lime_wool_slab", Blocks.LIME_WOOL);
	public static final Block PINK_WOOL_SLAB = registerWoolSlab("pink_wool_slab", Blocks.PINK_WOOL);
	public static final Block GRAY_WOOL_SLAB = registerWoolSlab("gray_wool_slab", Blocks.GRAY_WOOL);
	public static final Block LIGHT_GRAY_WOOL_SLAB = registerWoolSlab("light_gray_wool_slab", Blocks.LIGHT_GRAY_WOOL);
	public static final Block CYAN_WOOL_SLAB = registerWoolSlab("cyan_wool_slab", Blocks.CYAN_WOOL);
	public static final Block PURPLE_WOOL_SLAB = registerWoolSlab("purple_wool_slab", Blocks.PURPLE_WOOL);
	public static final Block BLUE_WOOL_SLAB = registerWoolSlab("blue_wool_slab", Blocks.BLUE_WOOL);
	public static final Block BROWN_WOOL_SLAB = registerWoolSlab("brown_wool_slab", Blocks.BROWN_WOOL);
	public static final Block GREEN_WOOL_SLAB = registerWoolSlab("green_wool_slab", Blocks.GREEN_WOOL);
	public static final Block RED_WOOL_SLAB = registerWoolSlab("red_wool_slab", Blocks.RED_WOOL);
	public static final Block BLACK_WOOL_SLAB = registerWoolSlab("black_wool_slab", Blocks.BLACK_WOOL);

	// Technical
	public static final Block SAFE_FIRE = register("safe_fire",
		new SafeFireBlock(FabricBlockSettings.of(Material.FIRE, MaterialColor.LAVA)
			.noCollision().breakInstantly().luminance((state) -> 15).sounds(BlockSoundGroup.WOOL)));

	static {
		Registry.register(Registry.ITEM, new Identifier(Main.MOD_ID, "rope"),
			new RopeItem(ROPE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
	}
	public static void init() {}
}
