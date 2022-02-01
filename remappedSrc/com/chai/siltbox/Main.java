package com.chai.siltbox;

import com.chai.siltbox.block.*;
import com.chai.siltbox.enchantments.SkippingEnchantment;
import com.chai.siltbox.enchantments.TradingCurseEnchantment;
import com.chai.siltbox.entity.SeatEntity;
import com.chai.siltbox.item.*;
import com.chai.siltbox.mixin.*;
import com.chai.siltbox.particles.*;
import com.chai.siltbox.temperature.HeatManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;

import java.util.*;

public class Main implements ModInitializer, ClientModInitializer
{
	public static final String MOD_ID = "siltbox";
	private static final String MOD_NAME = "Siltbox";

	private MinecraftClient client;
	private static final Identifier BAR_ICONS = new Identifier(MOD_ID, "textures/gui/bar_icons.png");
	private static final Identifier TEMP_ICONS = new Identifier(MOD_ID, "textures/gui/temperature_icon.png");
	private static final Identifier STATUS_ICONS = new Identifier(MOD_ID, "textures/gui/status_icon.png");
	private static final Identifier SATURATION_SYNC = new Identifier(MOD_ID, "saturation_sync");
	private static final Identifier KNOCKBACK_YAW = new Identifier(MOD_ID, "knockback_yaw");

	public static int ticksUntilCanInteract = 0;
	public static Map<PistonBlockEntity, CompoundTag> blockEntityTags = new HashMap<>();

	// Items
	public static final Item IRON_NODE;
	public static final Item GOLD_NODE;
	public static final Item STEEL_INGOT;
	public static final Item ROTTEN_LEATHER;
	public static final Item WOODEN_MATTOCK;
	public static final Item STONE_MATTOCK;
	public static final Item IRON_MATTOCK;
	public static final Item STEEL_MATTOCK;
	public static final Item GOLDEN_MATTOCK;
	public static final Item DIAMOND_MATTOCK;
	public static final Item NETHERITE_MATTOCK;
	public static final Item BLOWGUN;
	public static final Item PAIL;
	public static final Item WATER_PAIL;
	public static final Item MILK_PAIL;
	public static final Item WRENCH;
	public static final Item TROWEL;
	public static final Item MARSHMALLOW_ON_A_STICK;
	public static final Item DOLPHIN_FINS;
	public static final Item PEARLESCENT_BUBBLE;

	public static final FoodComponent FIELD_SNACK_FOOD;
	public static final FoodComponent RAW_CALAMARI_FOOD;
	public static final FoodComponent COOKED_CALAMARI_FOOD;
	public static final FoodComponent MARSHMALLOW_FOOD;
	public static final FoodComponent COOKED_MARSHMALLOW_FOOD;
	public static final FoodComponent BURNT_MARSHMALLOW_FOOD;
	public static final FoodComponent GRAHAM_CRACKER_FOOD;
	public static final FoodComponent SMORE_FOOD;
	public static final WaterComponent PURIFIED_WATER_BOTTLE_WATER;

	public static final Item FIELD_SNACK;
	public static final Item RAW_CALAMARI;
	public static final Item COOKED_CALAMARI;
	public static final Item MARSHMALLOW;
	public static final Item COOKED_MARSHMALLOW;
	public static final Item BURNT_MARSHMALLOW;
	public static final Item GRAHAM_CRACKER;
	public static final Item SMORE;
	public static final Item PURIFIED_WATER_BOTTLE;

	// Blocks
	public static final Block IRON_BUTTON;
	public static final Block GOLD_BUTTON;
	public static final Block PLAYER_PRESSURE_PLATE;

	public static final Block NETHER_FORGE;
	public static final BlockEntityType<NetherForgeBlockEntity> NETHER_FORGE_ENTITY;
	public static final RecipeType<NetherForgeRecipe> NETHER_FORGE_RECIPE;
	public static final RecipeSerializer<NetherForgeRecipe> NETHER_FORGE_SERIALIZER;
	public static final ScreenHandlerType<NetherForgeScreenHandler> NETHER_FORGE_SCREEN_HANDLER;

	public static final Block OAK_PANEL;
	public static final Block SPRUCE_PANEL;
	public static final Block BIRCH_PANEL;
	public static final Block JUNGLE_PANEL;
	public static final Block ACACIA_PANEL;
	public static final Block DARK_OAK_PANEL;
	public static final Block CRIMSON_PANEL;
	public static final Block WARPED_PANEL;
	public static final Block IRON_PLATE;
	public static final Block GOLD_PLATE;
	public static final Block NETHERITE_PLATE;

	public static final Block OAK_POST;
	public static final Block SPRUCE_POST;
	public static final Block BIRCH_POST;
	public static final Block JUNGLE_POST;
	public static final Block ACACIA_POST;
	public static final Block DARK_OAK_POST;
	public static final Block CRIMSON_POST;
	public static final Block WARPED_POST;

	public static final Block OAK_LOG_POST;

	public static final Block SAFE_FIRE;

	// Entites
	//public static final EntityType<SeedEntity> SEED_ENTITY;
	public static final EntityType<SeatEntity> SEAT_ENTITY;

	// Materials
	public static final ArmorMaterial DOLPHIN_MATERIAL = new DolphinMaterial();

	// Particles
	public static final DefaultParticleType OAK_LEAF;
	public static final DefaultParticleType BIRCH_LEAF;
	public static final DefaultParticleType SPRUCE_LEAF;
	public static final DefaultParticleType JUNGLE_LEAF;
	public static final DefaultParticleType ACACIA_LEAF;
	public static final DefaultParticleType DARK_OAK_LEAF;
	public static final DefaultParticleType WATER_RIPPLE;
	public static final DefaultParticleType FIREFLY;
	public static final DefaultParticleType BIG_LAVA_EMBER;
	public static final DefaultParticleType STEAM;

	// Status effects
	public static final StatusEffect THIRST;

	// Enchantments
	public static final Enchantment SKIPPING;
	public static final Enchantment TRADING_CURSE;

	// Sound events
	public static final Identifier BUBBLE_POP_ID = new Identifier(MOD_ID, "bubble_pop");
	public static final SoundEvent BUBBLE_POP;

	// Tags
	public static Tag<Item> IGNORE_GRASS = TagRegistry.item(new Identifier(MOD_ID, "ignore_grass"));

	static {
		// Items
		IRON_NODE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_node"),
			new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
		GOLD_NODE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gold_node"),
			new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
		STEEL_INGOT = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "steel_ingot"),
			new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
		ROTTEN_LEATHER = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "rotten_leather"),
			new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
		PEARLESCENT_BUBBLE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pearlescent_bubble"),
			new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

		WOODEN_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "wooden_mattock"),
			new MattockItem(ToolMaterials.WOOD));
		STONE_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "stone_mattock"),
			new MattockItem(ToolMaterials.STONE));
		IRON_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_mattock"),
			new MattockItem(ToolMaterials.IRON));
		STEEL_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "steel_mattock"),
			new MattockItem(SteelToolMaterial.INSTANCE));
		GOLDEN_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "golden_mattock"),
			new MattockItem(ToolMaterials.GOLD));
		DIAMOND_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "diamond_mattock"),
			new MattockItem(ToolMaterials.DIAMOND));
		NETHERITE_MATTOCK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "netherite_mattock"),
			new MattockItem(ToolMaterials.NETHERITE));

		BLOWGUN = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "blowgun"),
			new BlowgunItem(new FabricItemSettings().maxCount(1).group(ItemGroup.COMBAT)));
		DOLPHIN_FINS = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "dolphin_fins"),
			new ArmorItem(DOLPHIN_MATERIAL, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));

		PAIL = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "pail"),
			new PailItem(Fluids.EMPTY, new FabricItemSettings().maxCount(16).group(ItemGroup.MISC)));
		WATER_PAIL = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "water_pail"),
			new PailItem(Fluids.WATER, new FabricItemSettings().recipeRemainder(PAIL).maxCount(1).group(ItemGroup.MISC)));
		MILK_PAIL = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "milk_pail"),
			new MilkPailItem(new FabricItemSettings().recipeRemainder(PAIL).maxCount(16).group(ItemGroup.MISC)));
		WRENCH = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "wrench"),
			new WrenchItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)));
		TROWEL = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "trowel"),
			new TrowelItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)));
		MARSHMALLOW_ON_A_STICK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "marshmallow_on_a_stick"),
			new MarshmallowOnAStickItem(new FabricItemSettings().maxCount(1).group(ItemGroup.FOOD)));
		FabricModelPredicateProviderRegistry.register(MARSHMALLOW_ON_A_STICK, new Identifier("cooked"), (itemStack, clientWorld, livingEntity) ->
		{
			if (livingEntity == null) { return 0f; }
			return MarshmallowOnAStickItem.getCookedState(itemStack);
		});

		FIELD_SNACK_FOOD = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).build();
		RAW_CALAMARI_FOOD = (new FoodComponent.Builder()).hunger(2).saturationModifier(0.1F).build();
		COOKED_CALAMARI_FOOD = (new FoodComponent.Builder()).hunger(5).saturationModifier(0.6F).build();
		MARSHMALLOW_FOOD = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build();
		COOKED_MARSHMALLOW_FOOD = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build();
		BURNT_MARSHMALLOW_FOOD = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build();
		GRAHAM_CRACKER_FOOD = (new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build();
		PURIFIED_WATER_BOTTLE_WATER = (new WaterComponent.Builder()).thirst(5).hydrationModifier(0.6F).build();

		SMORE_FOOD = (new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).build();
		FIELD_SNACK = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "field_snack"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(FIELD_SNACK_FOOD)));
		RAW_CALAMARI = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "raw_calamari"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(RAW_CALAMARI_FOOD)));
		COOKED_CALAMARI = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "cooked_calamari"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(COOKED_CALAMARI_FOOD)));
		MARSHMALLOW = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "marshmallow"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(MARSHMALLOW_FOOD)));
		COOKED_MARSHMALLOW = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "cooked_marshmallow"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(COOKED_MARSHMALLOW_FOOD)));
		BURNT_MARSHMALLOW = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "burnt_marshmallow"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(BURNT_MARSHMALLOW_FOOD)));
		GRAHAM_CRACKER = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "graham_cracker"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(GRAHAM_CRACKER_FOOD)));
		SMORE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "smore"),
			new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(SMORE_FOOD)));

		Item temp = new PurifiedWaterBottleItem(new FabricItemSettings().group(ItemGroup.FOOD).maxCount(16));
		((IWaterComponent) temp).setWaterComponent(PURIFIED_WATER_BOTTLE_WATER);
		PURIFIED_WATER_BOTTLE = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "purified_water_bottle"), temp);

		// Blocks
		IRON_BUTTON = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "iron_button"),
			new GoldButtonBlock(FabricBlockSettings.of(Material.SUPPORTED, MaterialColor.IRON).noCollision()
				.strength(0.5f).breakByTool(FabricToolTags.PICKAXES).requiresTool()));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_button"),
			new BlockItem(IRON_BUTTON, new FabricItemSettings().group(ItemGroup.REDSTONE)));
		GOLD_BUTTON = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "gold_button"),
			new GoldButtonBlock(FabricBlockSettings.of(Material.SUPPORTED, MaterialColor.GOLD).noCollision()
				.strength(0.5f).breakByTool(FabricToolTags.PICKAXES).requiresTool()));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gold_button"),
			new BlockItem(GOLD_BUTTON, new FabricItemSettings().group(ItemGroup.REDSTONE)));
		PLAYER_PRESSURE_PLATE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "player_pressure_plate"),
			new PlayerPressurePlateBlock(FabricBlockSettings.of(Material.STONE)
				.requiresTool().noCollision().strength(0.5F).sounds(BlockSoundGroup.STONE)
				.breakByTool(FabricToolTags.PICKAXES).requiresTool()));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "player_pressure_plate"),
			new BlockItem(PLAYER_PRESSURE_PLATE, new FabricItemSettings().group(ItemGroup.REDSTONE)));

		OAK_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "oak_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.OAK_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "oak_panel"),
			new BlockItem(OAK_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		SPRUCE_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "spruce_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.SPRUCE_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "spruce_panel"),
			new BlockItem(SPRUCE_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		BIRCH_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "birch_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.BIRCH_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "birch_panel"),
			new BlockItem(BIRCH_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		JUNGLE_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "jungle_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.JUNGLE_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "jungle_panel"),
			new BlockItem(JUNGLE_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		ACACIA_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "acacia_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.ACACIA_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "acacia_panel"),
			new BlockItem(ACACIA_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		DARK_OAK_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "dark_oak_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "dark_oak_panel"),
			new BlockItem(DARK_OAK_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		CRIMSON_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "crimson_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.CRIMSON_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crimson_panel"),
			new BlockItem(CRIMSON_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		WARPED_PANEL = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "warped_panel"),
			new PanelBlock(FabricBlockSettings.of(Material.WOOD, Blocks.WARPED_PLANKS.getDefaultMaterialColor())
				.strength(0.5f).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "warped_panel"),
			new BlockItem(WARPED_PANEL, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

		IRON_PLATE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "iron_plate"),
			new PanelBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.IRON).requiresTool()
				.strength(0.5f).sounds(BlockSoundGroup.METAL).breakByTool(FabricToolTags.PICKAXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "iron_plate"),
			new BlockItem(IRON_PLATE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		GOLD_PLATE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "gold_plate"),
			new PanelBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.GOLD).requiresTool()
				.strength(0.5f).sounds(BlockSoundGroup.METAL).breakByTool(FabricToolTags.PICKAXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "gold_plate"),
			new BlockItem(GOLD_PLATE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		NETHERITE_PLATE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "netherite_plate"),
			new PanelBlock(FabricBlockSettings.of(Material.METAL, MaterialColor.BLACK).requiresTool()
				.strength(0.5f).sounds(BlockSoundGroup.NETHERITE).breakByTool(FabricToolTags.PICKAXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "netherite_plate"),
			new BlockItem(NETHERITE_PLATE, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

		OAK_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "oak_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.OAK_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "oak_post"),
			new BlockItem(OAK_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		SPRUCE_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "spruce_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.SPRUCE_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "spruce_post"),
			new BlockItem(SPRUCE_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		BIRCH_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "birch_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.BIRCH_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "birch_post"),
			new BlockItem(BIRCH_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		JUNGLE_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "jungle_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.JUNGLE_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "jungle_post"),
			new BlockItem(JUNGLE_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		ACACIA_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "acacia_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.ACACIA_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "acacia_post"),
			new BlockItem(ACACIA_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		DARK_OAK_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "dark_oak_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.DARK_OAK_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "dark_oak_post"),
			new BlockItem(DARK_OAK_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		CRIMSON_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "crimson_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.CRIMSON_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crimson_post"),
			new BlockItem(CRIMSON_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		WARPED_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "warped_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.WARPED_PLANKS.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "warped_post"),
			new BlockItem(WARPED_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

		OAK_LOG_POST = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "oak_log_post"),
			new PostBlock(FabricBlockSettings.of(Material.WOOD, Blocks.OAK_LOG.getDefaultMaterialColor())
				.strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "oak_log_post"),
			new BlockItem(OAK_LOG_POST, new FabricItemSettings().group(ItemGroup.DECORATIONS)));

		NETHER_FORGE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "nether_forge"),
			new NetherForgeBlock(FabricBlockSettings.of(Material.METAL).requiresTool().strength(3.5F).luminance((state) ->
			state.get(Properties.LIT) ? 13 : 0)));
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, "nether_forge"), new BlockItem(NETHER_FORGE,
			new FabricItemSettings().group(ItemGroup.DECORATIONS)));
		NETHER_FORGE_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "nether_forge"),
			BlockEntityType.Builder.create(NetherForgeBlockEntity::new, NETHER_FORGE).build(null));
		NETHER_FORGE_RECIPE = Registry.register(Registry.RECIPE_TYPE, new Identifier(MOD_ID, "nether_forge"),
			new RecipeType<NetherForgeRecipe>()
		{
			@Override
			public String toString() { return "nether_forge"; }
		});
		NETHER_FORGE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "nether_forge"),
			new CookingRecipeSerializer<>(NetherForgeRecipe::new, 200));
		NETHER_FORGE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "nether_forge"),
			NetherForgeScreenHandler::new);

		SAFE_FIRE = Registry.register(Registry.BLOCK, new Identifier(MOD_ID, "safe_fire"),
			new SafeFireBlock(AbstractBlock.Settings.of(Material.FIRE, MaterialColor.LAVA)
				.noCollision().breakInstantly().luminance((state) -> 15).sounds(BlockSoundGroup.WOOL)));

		// Entities
		/*SEED_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "seed"),
			FabricEntityTypeBuilder.<SeedEntity>create(SpawnGroup.MISC, SeedEntity::new)
			.dimensions(EntityDimensions.fixed(0.25F, 0.25F)).trackRangeBlocks(4).trackedUpdateRate(20).build());*/

		SEAT_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "seat"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, SeatEntity::new)
			.dimensions(EntityDimensions.fixed(0f, 0f)).build());

		// Particles
		OAK_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "oak_leaf"), FabricParticleTypes.simple());
		BIRCH_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "birch_leaf"), FabricParticleTypes.simple());
		SPRUCE_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "spruce_leaf"), FabricParticleTypes.simple());
		JUNGLE_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "jungle_leaf"), FabricParticleTypes.simple());
		ACACIA_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "acacia_leaf"), FabricParticleTypes.simple());
		DARK_OAK_LEAF = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "dark_oak_leaf"), FabricParticleTypes.simple());
		WATER_RIPPLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "water_ripple"), FabricParticleTypes.simple());
		FIREFLY = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "firefly"), FabricParticleTypes.simple());
		BIG_LAVA_EMBER = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "big_lava_ember"), FabricParticleTypes.simple());
		STEAM = Registry.register(Registry.PARTICLE_TYPE, new Identifier(MOD_ID, "steam"), FabricParticleTypes.simple());
		ParticleFactoryRegistry.getInstance().register(OAK_LEAF, OakLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(BIRCH_LEAF, BirchLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(SPRUCE_LEAF, SpruceLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(JUNGLE_LEAF, JungleLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(ACACIA_LEAF, AcaciaLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(DARK_OAK_LEAF, DarkOakLeafParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(WATER_RIPPLE, WaterRippleParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(FIREFLY, FireflyParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(BIG_LAVA_EMBER, BigLavaEmberParticle.DefaultFactory::new);
		ParticleFactoryRegistry.getInstance().register(STEAM, SteamParticle.DefaultFactory::new);

		// Status effects
		THIRST = Registry.register(Registry.STATUS_EFFECT, new Identifier(MOD_ID, "thirst"), new ThirstStatusEffect());

		// Enchantments
		SKIPPING = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "skipping"), new SkippingEnchantment());
		TRADING_CURSE = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "trading_curse"), new TradingCurseEnchantment());

		// Sound events
		BUBBLE_POP = Registry.register(Registry.SOUND_EVENT, BUBBLE_POP_ID, new SoundEvent(BUBBLE_POP_ID));
	}

	@Override
	public void onInitialize()
	{
		// Modify items
		((AccessorFoodComponent) FoodComponents.COOKIE).setSnack(true);
		((AccessorFoodComponent) FoodComponents.SWEET_BERRIES).setSnack(true);
		((AccessorItem) Items.POTION).setMaxCount(16);
		((AccessorItem) Items.SPLASH_POTION).setMaxCount(16);
		((AccessorItem) Items.LINGERING_POTION).setMaxCount(16);
		((AccessorItem) Items.MILK_BUCKET).setMaxCount(16);
		((AccessorItem) Items.BEETROOT_SOUP).setMaxCount(16);
		((AccessorItem) Items.MUSHROOM_STEW).setMaxCount(16);
		((AccessorItem) Items.RABBIT_STEW).setMaxCount(16);
		((AccessorItem) Items.SUSPICIOUS_STEW).setMaxCount(16);
		((AccessorItem) Items.SNOWBALL).setMaxCount(64);
		((AccessorItem) Items.EGG).setMaxCount(64);
		((AccessorItem) Items.ACACIA_SIGN).setMaxCount(64);
		((AccessorItem) Items.BIRCH_SIGN).setMaxCount(64);
		((AccessorItem) Items.CRIMSON_SIGN).setMaxCount(64);
		((AccessorItem) Items.DARK_OAK_SIGN).setMaxCount(64);
		((AccessorItem) Items.JUNGLE_SIGN).setMaxCount(64);
		((AccessorItem) Items.OAK_SIGN).setMaxCount(64);
		((AccessorItem) Items.SPRUCE_SIGN).setMaxCount(64);
		((AccessorItem) Items.WARPED_SIGN).setMaxCount(64);

		((IWaterComponent) Items.ENCHANTED_GOLDEN_APPLE).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.8F).build());
		((IWaterComponent) Items.GOLDEN_APPLE).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.8F).build());
		((IWaterComponent) Items.GOLDEN_CARROT).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.8F).build());
		((IWaterComponent) Items.MELON_SLICE).setWaterComponent(
			(new WaterComponent.Builder()).thirst(4).hydrationModifier(0.6F).build());
		((IWaterComponent) Items.RABBIT_STEW).setWaterComponent(
			(new WaterComponent.Builder()).thirst(4).hydrationModifier(0.6F).build());
		((IWaterComponent) Items.BEETROOT_SOUP).setWaterComponent(
			(new WaterComponent.Builder()).thirst(4).hydrationModifier(0.6F).build());
		((IWaterComponent) Items.MUSHROOM_STEW).setWaterComponent(
			(new WaterComponent.Builder()).thirst(4).hydrationModifier(0.6F).build());
		((IWaterComponent) Items.APPLE).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.3F).build());
		((IWaterComponent) Items.SWEET_BERRIES).setWaterComponent(
			(new WaterComponent.Builder()).thirst(1).hydrationModifier(0.3F).build());
		((IWaterComponent) Items.CARROT).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.1F).build());
		((IWaterComponent) Items.POTATO).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.1F).build());
		((IWaterComponent) Items.BEETROOT).setWaterComponent(
			(new WaterComponent.Builder()).thirst(2).hydrationModifier(0.1F).build());

		InvokerComposterBlock.registerCompost(0.3f, Items.ROTTEN_FLESH);

		AccessorShovelItem.getPathStates().put(Blocks.DIRT, Blocks.GRASS_PATH.getDefaultState());

		AccessorTradeOffers.setWanderingTrades(new Int2ObjectOpenHashMap<>(ImmutableMap.of(
			1, new TradeOffers.Factory[]{
				new TradeOffers.SellItemFactory(Items.ALLIUM, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.AZURE_BLUET, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.CORNFLOWER, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.DANDELION, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.OXEYE_DAISY, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.POPPY, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.RED_TULIP, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.ORANGE_TULIP, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.PINK_TULIP, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.WHITE_TULIP, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.BLUE_ORCHID, 1, 6, 8, 1),
				new TradeOffers.SellItemFactory(Items.LILY_OF_THE_VALLEY, 1, 6, 8, 1),
				new TradeOffers.SellItemFactory(Items.SUNFLOWER, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.ROSE_BUSH, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.LILAC, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.PEONY, 1, 4, 8, 1),

				new TradeOffers.SellItemFactory(Items.LILY_PAD, 1, 6, 8, 1),
				new TradeOffers.SellItemFactory(Items.SEA_PICKLE, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.BAMBOO, 1, 6, 8, 1),

				new TradeOffers.SellItemFactory(Items.CARROT, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.POTATO, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.PUMPKIN_SEEDS, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.MELON_SEEDS, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.BEETROOT_SEEDS, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.COCOA_BEANS, 1, 2, 8, 1),
				new TradeOffers.SellItemFactory(Items.SWEET_BERRIES, 1, 6, 8, 1),

				new TradeOffers.SellItemFactory(Items.SAND, 1, 16, 8, 1),
				new TradeOffers.SellItemFactory(Items.RED_SAND, 1, 16, 8, 1),
				new TradeOffers.SellItemFactory(Items.GLOWSTONE, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.BLUE_ICE, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.BRAIN_CORAL_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.FIRE_CORAL_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.HORN_CORAL_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.TUBE_CORAL_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.BROWN_MUSHROOM_BLOCK, 1, 8, 8, 1),
				new TradeOffers.SellItemFactory(Items.RED_MUSHROOM_BLOCK, 1, 8, 8, 1)
			},
			2, new TradeOffers.Factory[]{
				new TradeOffers.SellItemFactory(Items.ENCHANTED_GOLDEN_APPLE, 64, 1, 1, 1),
				new TradeOffers.SellItemFactory(Items.WITHER_ROSE, 2, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.DRAGON_BREATH, 2, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.NAUTILUS_SHELL, 3, 1, 8, 1),
				new TradeOffers.SellItemFactory(Items.SCUTE, 5, 1, 5, 1),
				new TradeOffers.SellItemFactory(Items.PUFFERFISH, 3, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.RABBIT_FOOT, 2, 1, 4, 1),
				new TradeOffers.SellItemFactory(Items.SLIME_BALL, 1, 4, 8, 1),
				new TradeOffers.SellItemFactory(Items.IRON_HORSE_ARMOR, 8, 1, 1, 1),
				new TradeOffers.SellItemFactory(Items.GOLDEN_HORSE_ARMOR, 16, 1, 1, 1),
				new TradeOffers.SellItemFactory(Items.DIAMOND_HORSE_ARMOR, 32, 1, 1, 1)
			})));

		/*FabricDefaultAttributeRegistry.register(SEAT_ENTITY, LivingEntity.createLivingAttributes());

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) ->
		{
			BlockPos pos = hitResult.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();

			if (player.isInSneakingPose() && block instanceof SlabBlock)
			{
				SeatEntity seat = SEAT_ENTITY.create(world);
				seat.updatePosition(pos.getX() + 0.5d, pos.getY() + 0.25d, pos.getZ() + 0.5d);
				world.spawnEntity(seat);
				player.startRiding(seat);
				return ActionResult.SUCCESS;
			}

			return ActionResult.PASS;
		});*/

		// Register packets
		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.SORT_INVENTORY, (server, player, handler, buf, responseSender) ->
			server.execute(() -> InventoryManager.sortPlayerInventory(player.inventory)));

		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.SORT_CONTAINER, (server, player, handler, buf, responseSender) ->
			server.execute(() -> InventoryManager.sortInventory(player.currentScreenHandler.getSlot(0).inventory)));

		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.DEPOSIT_ALL, (server, player, handler, buf, responseSender) ->
			server.execute(() ->
			{
				int size = player.currentScreenHandler.getSlot(0).inventory.size();
				for (int i = size; i < size + 27; ++i)
				{
					player.currentScreenHandler.transferSlot(null, i);
				}
			}));

		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.LOOT_ALL, (server, player, handler, buf, responseSender) ->
			server.execute(() ->
			{
				int size = player.currentScreenHandler.getSlot(0).inventory.size();
				for (int i = 0; i < size; ++i)
				{
					player.currentScreenHandler.transferSlot(null, i);
				}
			}));

		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.QUICK_STACK, (server, player, handler, buf, responseSender) ->
			server.execute(() ->
			{
				Inventory container = player.currentScreenHandler.getSlot(0).inventory;
				List<Slot> slots = player.currentScreenHandler.slots;
				int size = slots.size();
				for (int i = size - 36; i < size - 9; ++i)
				{
					if (container.containsAny(Collections.singleton(slots.get(i).getStack().getItem())))
					{
						player.currentScreenHandler.transferSlot(null, i);
					}
				}
			}));

		ServerPlayNetworking.registerGlobalReceiver(InventoryManager.RESTOCK, (server, player, handler, buf, responseSender) ->
			server.execute(() ->
			{
				Inventory container = player.currentScreenHandler.getSlot(0).inventory;
				PlayerInventory inventory = player.inventory;

				for (int i = 0; i < 36; ++i)
				{
					ItemStack stackOne = inventory.getStack(i);
					int needed = stackOne.getMaxCount() - stackOne.getCount();
					if (needed > 0)
					{
						for (int j = 0; j < container.size(); ++j)
						{
							ItemStack stackTwo = container.getStack(j);
							if (stackOne.getItem() == stackTwo.getItem())
							{
								int count = stackTwo.getCount();

								if (needed <= count)
								{
									inventory.insertStack(container.removeStack(j, needed));
									needed = 0;
								}
								else
								{
									inventory.insertStack(container.removeStack(j, count));
									needed -= count;
								}
							}
						}
					}
				}
			}));
	}

	@Override
	public void onInitializeClient()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(SAFE_FIRE, RenderLayer.getCutout());

		// Register entities
		/*EntityRendererRegistry.INSTANCE.register(SEED_ENTITY, (dispatcher, context) ->
		{
			receiveEntityPacket();
			return new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer());
		});*/

		EntityRendererRegistry.INSTANCE.register(SEAT_ENTITY, (dispatcher, context) -> new SeatEntity.EmptyRenderer(dispatcher));

		// Register screens
		ScreenRegistry.register(NETHER_FORGE_SCREEN_HANDLER, NetherForgeScreen::new);

		// Register hud
		client = MinecraftClient.getInstance();
		HudRenderCallback.EVENT.register((matrices, val) -> render(matrices));

		// Register packets
		ClientPlayNetworking.registerGlobalReceiver(SATURATION_SYNC, (client, handler, buf, responseSender) ->
		{
			float saturation = buf.readFloat();
			client.execute(() -> client.player.getHungerManager().setSaturationLevelClient(saturation));
		});
		ClientPlayNetworking.registerGlobalReceiver(ThirstManager.THIRST_SYNC, (client, handler, buf, responseSender) ->
		{
			int thirst = buf.readInt();
			client.execute(() -> ((IPlayerEntity) client.player).getThirstManager().setWaterLevel(thirst));
		});
		ClientPlayNetworking.registerGlobalReceiver(ThirstManager.HYDRATION_SYNC, (client, handler, buf, responseSender) ->
		{
			float hydration = buf.readFloat();
			client.execute(() -> ((IPlayerEntity) client.player).getThirstManager().setHydrationLevelClient(hydration));
		});
		ClientPlayNetworking.registerGlobalReceiver(HeatManager.INTERNAL_SYNC, (client, handler, buf, responseSender) ->
		{
			float internalTemp = buf.readFloat();
			client.execute(() -> ((IPlayerEntity) client.player).getHeatManager().setInternalTemp(internalTemp));
		});
		ClientPlayNetworking.registerGlobalReceiver(HeatManager.EXTERNAL_SYNC, (client, handler, buf, responseSender) ->
		{
			float externalTemp = buf.readFloat();
			client.execute(() -> ((IPlayerEntity) client.player).getHeatManager().setExternalTemp(externalTemp));
		});

		ClientPlayNetworking.registerGlobalReceiver(KNOCKBACK_YAW, (client, handler, buf, responseSender) ->
		{
			float f = buf.readFloat();
			client.execute(() -> client.player.knockbackVelocity = f);
		});
	}

	private String ticksToTime(int ticks)
	{
		int seconds = ticks / 20;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		if (minutes == 0)
		{
			return seconds + "";
		}
		return minutes + ":" + String.format("%2s", seconds).replace(' ', '0');
	}

	private void render(MatrixStack matrices)
	{
		PlayerEntity player = client.player;
		if (player == null || player.isCreative() || player.isSpectator()) { return; }

		// Draw armor durability
		int x = client.getWindow().getScaledWidth() / 2 - 108;
		int y = client.getWindow().getScaledHeight() - 51;
		for (ItemStack armor : player.getArmorItems())
		{
			y -= 3;
			if (armor == null || armor.isEmpty() || !armor.isDamageable()) { continue; }

			RenderSystem.disableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.disableAlphaTest();
			RenderSystem.disableBlend();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			float damage = armor.getDamage();
			float maxDamage = armor.getMaxDamage();
			float frac = Math.max(0f, (maxDamage - damage) / maxDamage);
			int width = Math.round(13f - damage * 13f / maxDamage);
			int color = MathHelper.hsvToRgb(frac / 3f, 1f, 1f);
			renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0);
			renderGuiQuad(bufferBuilder, x + 2, y + 13, width, 1, color >> 16 & 255, color >> 8 & 255, color & 255);
			RenderSystem.enableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableTexture();
			RenderSystem.enableDepthTest();
		}

		// Render better status icons
		Collection<StatusEffectInstance> collection = client.player.getStatusEffects();
		if (!collection.isEmpty())
		{
			RenderSystem.enableBlend();
			int good = 0;
			int bad = 0;
			StatusEffectSpriteManager manager = client.getStatusEffectSpriteManager();
			List<Runnable> list = Lists.newArrayListWithExpectedSize(collection.size());
			client.getTextureManager().bindTexture(STATUS_ICONS);
			Iterator iterator = Ordering.natural().reverse().sortedCopy(collection).iterator();

			while (iterator.hasNext())
			{
				StatusEffectInstance inst = (StatusEffectInstance)iterator.next();
				StatusEffect status = inst.getEffectType();
				if (inst.shouldShowIcon())
				{
					x = 1;
					y = 1;
					if (status.isBeneficial())
					{
						x += good++ * 33;
					}
					else
					{
						x += bad++ * 33;
						y += 33;
					}

					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					client.getTextureManager().bindTexture(STATUS_ICONS);
					if (inst.isAmbient())
					{
						DrawableHelper.drawTexture(matrices, x, y, 32, 0, 32, 33, 64, 33);
					}
					else
					{
						DrawableHelper.drawTexture(matrices, x, y, 0, 0, 32, 33, 64, 33);
						DrawableHelper.drawCenteredText(matrices, client.textRenderer,
							new LiteralText(ticksToTime(inst.getDuration())), x + 16, y + 22, 0xFF7D7D7D);
					}

					int a = x;
					int b = y;

					Sprite sprite = manager.getSprite(status);
					list.add(() ->
					{
						client.getTextureManager().bindTexture(sprite.getAtlas().getId());
						DrawableHelper.drawSprite(matrices, a + 7, b + 3, 0, 18, 18, sprite);
					});
				}
			}
			list.forEach(Runnable::run);
		}

		RenderSystem.color4f(1f, 1f, 1f, 1f);
		client.getTextureManager().bindTexture(BAR_ICONS);
		int right = client.getWindow().getScaledWidth() / 2 + 91;
		int top = client.getWindow().getScaledHeight() - 39;

		// Draw saturation
		float saturation = player.getHungerManager().getSaturationLevel();

		for (int i = 0; i < Math.ceil(saturation / 2f); ++i)
		{
			x = right - i * 8 - 9;
			y = top;
			float temp = saturation / 2f - i;

			if (temp >= 1)
			{
				DrawableHelper.drawTexture(matrices, x, y, 27, 0, 9, 9, 54, 27);
			}
			else if (temp > 0.66)
			{
				DrawableHelper.drawTexture(matrices, x, y, 18, 0, 9, 9, 54, 27);
			}
			else if (temp > 0.33)
			{
				DrawableHelper.drawTexture(matrices, x, y, 9, 0, 9, 9, 54, 27);
			}
			else if (temp > 0)
			{
				DrawableHelper.drawTexture(matrices, x, y, 0, 0, 9, 9, 54, 27);
			}
		}

		// Draw thirst
		float thirst = ((IPlayerEntity) player).getThirstManager().getWaterLevel();
		float originalThirst = thirst;
		float hydration = ((IPlayerEntity) player).getThirstManager().getHydrationLevel();
		boolean thirsty = player.getStatusEffect(THIRST) != null;
		Random random = new Random(client.inGameHud.getTicks());

		for (int i = 0; i < 10; ++i)
		{
			x = right - i * 8 - 9;
			y = top - 10;

			if (hydration <= 0.0F && client.inGameHud.getTicks() % (originalThirst * 3 + 1) == 0)
			{
				y += random.nextInt(3) - 1;
			}

			DrawableHelper.drawTexture(matrices, x, y, thirsty ? 9 : 0, 9, 9, 9, 54, 27);

			if (thirst >= 2)
			{
				DrawableHelper.drawTexture(matrices, x, y, 18 + (thirsty ? 18 : 0), 9, 9, 9, 54, 27);
			}
			else if (thirst == 1)
			{
				DrawableHelper.drawTexture(matrices, x, y, 27 + (thirsty ? 18 : 0), 9, 9, 9, 54, 27);
			}
			thirst -= 2;

			float temp = hydration / 2f - i;

			if (temp >= 1)
			{
				DrawableHelper.drawTexture(matrices, x, y, 27, 18, 9, 9, 54, 27);
			}
			else if (temp > 0.66)
			{
				DrawableHelper.drawTexture(matrices, x, y, 18, 18, 9, 9, 54, 27);
			}
			else if (temp > 0.33)
			{
				DrawableHelper.drawTexture(matrices, x, y, 9, 18, 9, 9, 54, 27);
			}
			else if (temp > 0)
			{
				DrawableHelper.drawTexture(matrices, x, y, 0, 18, 9, 9, 54, 27);
			}
		}

		// Draw temperature
		client.getTextureManager().bindTexture(TEMP_ICONS);
		x = client.getWindow().getScaledWidth() / 2 - 7;
		y = client.getWindow().getScaledHeight() - 51;

		float internal = ((IPlayerEntity) player).getHeatManager().getInternalTemp();
		float external = ((IPlayerEntity) player).getHeatManager().getExternalTemp();

		DrawableHelper.drawTexture(matrices, x, y, 0, 0, 14, 14, 56, 28);


		DrawableHelper.drawTexture(matrices, x, y, 28, 0, 14, 14, 56, 28);
		DrawableHelper.drawTexture(matrices, x, y, 28, 14, 14, 14, 56, 28);

		if (internal >= 1f)
		{
			float f = (internal - 1f) / 4f;
			RenderSystem.color4f(1f, 1f, 1f, f);
			DrawableHelper.drawTexture(matrices, x, y, 28 + 14, 0, 14, 14, 56, 28);
		}
		else if (internal <= -1f)
		{
			float f = (-internal - 1f) / 4f;
			RenderSystem.color4f(1f, 1f, 1f, f);
			DrawableHelper.drawTexture(matrices, x, y, 28 - 14, 0, 14, 14, 56, 28);
		}

		if (external >= 1f)
		{
			float f = 1f/3f;
			if (external >= 3) { f = 2f/3f; }
			else if (external == 5) { f = 1f; }
			RenderSystem.color4f(1f, 1f, 1f, f);
			DrawableHelper.drawTexture(matrices, x, y, 28 + 14, 14, 14, 14, 56, 28);
		}
		else if (external <= -1f)
		{
			float f = 1f/3f;
			if (external <= -3) { f = 2f/3f; }
			else if (external == -5) { f = 1f; }
			RenderSystem.color4f(1f, 1f, 1f, f);
			DrawableHelper.drawTexture(matrices, x, y, 28 - 14, 14, 14, 14, 56, 28);
		}
	}

	private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue)
	{
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		buffer.vertex(x, y, 0.0D).color(red, green, blue, 255).next();
		buffer.vertex(x, y + height, 0.0D).color(red, green, blue, 255).next();
		buffer.vertex(x + width, y + height, 0.0D).color(red, green, blue, 255).next();
		buffer.vertex(x + width, y, 0.0D).color(red, green, blue, 255).next();
		Tessellator.getInstance().draw();
	}

	public void receiveEntityPacket()
	{
		/*ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) ->
		{
			EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID uuid = byteBuf.readUuid();
			int entityId = byteBuf.readVarInt();
			Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
			float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			ctx.getTaskQueue().execute(() -> {
				if (MinecraftClient.getInstance().world == null)
					throw new IllegalStateException("Tried to spawn entity in a null world!");
				Entity e = et.create(MinecraftClient.getInstance().world);
				if (e == null)
					throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
				e.updateTrackedPosition(pos);
				e.setPos(pos.x, pos.y, pos.z);
				e.pitch = pitch;
				e.yaw = yaw;
				e.setEntityId(entityId);
				e.setUuid(uuid);
				MinecraftClient.getInstance().world.addEntity(entityId, e);
			});
		});*/
	}

	private static final Map<UUID, Float> prevSaturation = new HashMap<>();

	public static void onPlayerUpdate(ServerPlayerEntity player)
	{
		Float prevSat = prevSaturation.get(player.getUuid());
		float saturation = player.getHungerManager().getSaturationLevel();

		if (prevSat == null || prevSat != saturation)
		{
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeFloat(saturation);
			ServerPlayNetworking.send(player, SATURATION_SYNC, buf);
			prevSaturation.put(player.getUuid(), saturation);
		}
	}

	public static void setKnockbackYaw(LivingEntity entity)
	{
		if (entity instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
			buf.writeFloat(player.knockbackVelocity);
			ServerPlayNetworking.send(player, KNOCKBACK_YAW, buf);
		}
	}
}
