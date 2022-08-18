package com.chai.siltbox;

import com.chai.siltbox.block.*;
import com.chai.siltbox.climate.*;
import com.chai.siltbox.enchantments.LaunchEnchantment;
import com.chai.siltbox.enchantments.SkippingEnchantment;
import com.chai.siltbox.enchantments.TradingCurseEnchantment;
import com.chai.siltbox.entity.SeatEntity;
import com.chai.siltbox.entity.SeedEntity;
import com.chai.siltbox.entity.SeedEntityRenderer;
import com.chai.siltbox.interfaces.IPlayerEntity;
import com.chai.siltbox.interfaces.IWaterComponent;
import com.chai.siltbox.item.SiltItems;
import com.chai.siltbox.mixin.AccessorFoodComponent;
import com.chai.siltbox.mixin.AccessorItem;
import com.chai.siltbox.mixin.AccessorShovelItem;
import com.chai.siltbox.mixin.block.InvokerComposterBlock;
import com.chai.siltbox.mixin.entity.AccessorTradeOffers;
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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
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
import net.minecraft.fluid.Fluid;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;

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

	public static ClimateManager overworldClimate = null;

	public static int ticksUntilCanInteract = 0;
	public static Map<PistonBlockEntity, CompoundTag> blockEntityTags = new HashMap<>();

	// Blocks
	public static final Block NETHER_FORGE;
	public static final BlockEntityType<NetherForgeBlockEntity> NETHER_FORGE_ENTITY;
	public static final RecipeType<NetherForgeRecipe> NETHER_FORGE_RECIPE;
	public static final RecipeSerializer<NetherForgeRecipe> NETHER_FORGE_SERIALIZER;
	public static final ScreenHandlerType<NetherForgeScreenHandler> NETHER_FORGE_SCREEN_HANDLER;

	// Entites
	public static final EntityType<SeedEntity> SEED_ENTITY;
	public static final EntityType<SeatEntity> SEAT_ENTITY;

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
	public static final Enchantment LAUNCH;
	public static final Enchantment TRADING_CURSE;

	// Sound events
	public static final Identifier BUBBLE_POP_ID = new Identifier(MOD_ID, "bubble_pop");
	public static final SoundEvent BUBBLE_POP;

	// Tags
	public static Tag<Item> IGNORE_GRASS = TagRegistry.item(new Identifier(MOD_ID, "ignore_grass"));
	public static Tag<Block> HOT_BLOCKS = TagRegistry.block(new Identifier(MOD_ID, "hot_blocks"));
	public static Tag<Fluid> HOT_FLUIDS = TagRegistry.fluid(new Identifier(MOD_ID, "hot_fluids"));

	static
	{
		// Blocks
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
					  public String toString()
					  {
						  return "nether_forge";
					  }
				  });
		NETHER_FORGE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MOD_ID, "nether_forge"),
				  new CookingRecipeSerializer<>(NetherForgeRecipe::new, 200));
		NETHER_FORGE_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MOD_ID, "nether_forge"),
				  NetherForgeScreenHandler::new);

		// Entities
		SEED_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "seed"),
				  FabricEntityTypeBuilder.<SeedEntity>create(SpawnGroup.MISC, SeedEntity::new)
							 .dimensions(EntityDimensions.fixed(0.25F, 0.25F)).trackRangeBlocks(4).trackedUpdateRate(10).build());

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
		LAUNCH = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "launch"), new LaunchEnchantment());
		TRADING_CURSE = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "trading_curse"), new TradingCurseEnchantment());

		// Sound events
		BUBBLE_POP = Registry.register(Registry.SOUND_EVENT, BUBBLE_POP_ID, new SoundEvent(BUBBLE_POP_ID));
	}

	@Override
	public void onInitialize()
	{
		SiltItems.init();
		SiltBlocks.init();

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

		ServerTickEvents.START_WORLD_TICK.register((world) ->
		{
			RegistryKey<World> key = world.getRegistryKey();

			if (key == World.OVERWORLD)
			{
				if (overworldClimate == null)
				{
					overworldClimate = new ClimateManager(world);
					overworldClimate.registerEvent(new RainEvent(overworldClimate));
					overworldClimate.registerEvent(new ThunderEvent(overworldClimate));
					overworldClimate.registerEvent(new FogEvent(overworldClimate));
					overworldClimate.registerEvent(new TropicalRainEvent(overworldClimate));
				}
				overworldClimate.tick();
			}
		});

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
									  } else
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
		// Register render layers
		BlockRenderLayerMap.INSTANCE.putBlock(SiltBlocks.SAFE_FIRE, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SiltBlocks.BAMBOO_SPIKES, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SiltBlocks.STRAINER, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(SiltBlocks.ROPE, RenderLayer.getCutout());

		// Register colors
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
							 world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(),
				  SiltBlocks.OAK_LEAF_PILE, SiltBlocks.JUNGLE_LEAF_PILE, SiltBlocks.ACACIA_LEAF_PILE, SiltBlocks.DARK_OAK_LEAF_PILE,
				  SiltBlocks.OAK_HEDGE, SiltBlocks.JUNGLE_HEDGE, SiltBlocks.ACACIA_HEDGE, SiltBlocks.DARK_OAK_HEDGE);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> FoliageColors.getSpruceColor(),
				  SiltBlocks.SPRUCE_LEAF_PILE, SiltBlocks.SPRUCE_HEDGE);
		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> FoliageColors.getBirchColor(),
				  SiltBlocks.BIRCH_LEAF_PILE, SiltBlocks.BIRCH_HEDGE);

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getDefaultColor(),
				  SiltBlocks.OAK_LEAF_PILE, SiltBlocks.JUNGLE_LEAF_PILE, SiltBlocks.ACACIA_LEAF_PILE, SiltBlocks.DARK_OAK_LEAF_PILE,
				  SiltBlocks.OAK_HEDGE, SiltBlocks.JUNGLE_HEDGE, SiltBlocks.ACACIA_HEDGE, SiltBlocks.DARK_OAK_HEDGE);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getSpruceColor(),
				  SiltBlocks.SPRUCE_LEAF_PILE, SiltBlocks.SPRUCE_HEDGE);
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> FoliageColors.getBirchColor(),
				  SiltBlocks.BIRCH_LEAF_PILE, SiltBlocks.BIRCH_HEDGE);

		// Register models
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new ModelProvider());

		// Register entities
		EntityRendererRegistry.INSTANCE.register(SEED_ENTITY, (dispatcher, context) ->
		{
			receiveEntityPacket();
			return new SeedEntityRenderer(dispatcher);
			//return new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer());
		});
		receiveEntityPacket();

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


		ClientTickEvents.START_WORLD_TICK.register((world) ->
		{
			FogManager.tick();
		});

		ClientPlayNetworking.registerGlobalReceiver(FogEvent.FOG_EVENT, (client, handler, buf, responseSender) ->
		{
			FogManager.foggy = buf.readBoolean();
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
		if (player == null || player.isCreative() || player.isSpectator())
		{
			return;
		}

		// Draw armor durability
		int x = client.getWindow().getScaledWidth() / 2 - 108;
		int y = client.getWindow().getScaledHeight() - 51;
		for (ItemStack armor : player.getArmorItems())
		{
			y -= 3;
			if (armor == null || armor.isEmpty() || !armor.isDamageable())
			{
				continue;
			}

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
				StatusEffectInstance inst = (StatusEffectInstance) iterator.next();
				StatusEffect status = inst.getEffectType();
				if (inst.shouldShowIcon())
				{
					x = 1;
					y = 1;
					if (status.isBeneficial())
					{
						x += good++ * 33;
					} else
					{
						x += bad++ * 33;
						y += 33;
					}

					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					client.getTextureManager().bindTexture(STATUS_ICONS);
					if (inst.isAmbient())
					{
						DrawableHelper.drawTexture(matrices, x, y, 32, 0, 32, 33, 64, 33);
					} else
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
			} else if (temp > 0.66)
			{
				DrawableHelper.drawTexture(matrices, x, y, 18, 0, 9, 9, 54, 27);
			} else if (temp > 0.33)
			{
				DrawableHelper.drawTexture(matrices, x, y, 9, 0, 9, 9, 54, 27);
			} else if (temp > 0)
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
			} else if (thirst == 1)
			{
				DrawableHelper.drawTexture(matrices, x, y, 27 + (thirsty ? 18 : 0), 9, 9, 9, 54, 27);
			}
			thirst -= 2;

			float temp = hydration / 2f - i;

			if (temp >= 1)
			{
				DrawableHelper.drawTexture(matrices, x, y, 27, 18, 9, 9, 54, 27);
			} else if (temp > 0.66)
			{
				DrawableHelper.drawTexture(matrices, x, y, 18, 18, 9, 9, 54, 27);
			} else if (temp > 0.33)
			{
				DrawableHelper.drawTexture(matrices, x, y, 9, 18, 9, 9, 54, 27);
			} else if (temp > 0)
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

		int num = Math.max(0, 3 - HeatManager.getAbove(internal, 5));
		if ((internal >= 5f || internal <= -5f) && client.inGameHud.getTicks() % (num * 5 + 2) == 0)
		{
			x += client.world.random.nextBoolean() ? 1 : -1;
		}

		DrawableHelper.drawTexture(matrices, x, y, 0, 0, 14, 14, 112, 28);
		DrawableHelper.drawTexture(matrices, x, y, 56 + getSprite(internal) * 14, 0, 14, 14, 112, 28);
		DrawableHelper.drawTexture(matrices, x, y, 56 + getSprite(external) * 14, 14, 14, 14, 112, 28);
	}

	private int getSprite(float temp)
	{
		if (temp >= 4f)
		{
			return 3;
		} else if (temp >= 2.5f)
		{
			return 2;
		} else if (temp >= 1f)
		{
			return 1;
		} else if (temp <= -4f)
		{
			return -3;
		} else if (temp <= -2.5f)
		{
			return -2;
		} else if (temp <= -1f)
		{
			return -1;
		} else
		{
			return 0;
		}
	}

	private float getOpacity(float temp)
	{
		if (temp < 0)
		{
			temp = -temp;
		}

		if (temp >= 5f)
		{
			return 1f;
		} else if (temp >= 3f)
		{
			return 1f / 2f;
		} else if (temp >= 1f)
		{
			return 1f / 4f;
		} else
		{
			return 0f;
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

	public static final Identifier PacketID = new Identifier(MOD_ID, "spawn_packet");

	public void receiveEntityPacket()
	{
		ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) ->
		{
			EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID uuid = byteBuf.readUuid();
			int entityId = byteBuf.readVarInt();
			Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
			float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
			ctx.getTaskQueue().execute(() ->
			{
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
		});
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
