package com.chai.siltbox.block;

import com.chai.siltbox.Main;
import net.minecraft.block.*;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.List;
import java.util.Random;

public class StrainerBlock extends Block implements Waterloggable
{
	public static Identifier STRAINER_LOOT = new Identifier(Main.MOD_ID, "gameplay/strainer");
	public static Identifier STRAINER_BADLANDS_LOOT = new Identifier(Main.MOD_ID, "gameplay/strainer_badlands");

	private static final VoxelShape COLLISION_SHAPE;
	public static final BooleanProperty WATERLOGGED;
	public static final IntProperty DURABILITY;

	public StrainerBlock(Settings settings)
	{
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(WATERLOGGED, false).with(DURABILITY, 7));
	}

	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
	{
		if (state.get(WATERLOGGED))
		{
			if (world.getBlockState(pos.down()).getBlock() == Blocks.BARREL)
			{
				Identifier lootID = STRAINER_LOOT;

				// Check if in a badlands biome
				RegistryKey<Biome> biome = world.method_31081(pos).get();
				if (biome == BiomeKeys.BADLANDS ||
						biome == BiomeKeys.BADLANDS_PLATEAU ||
						biome == BiomeKeys.ERODED_BADLANDS ||
						biome == BiomeKeys.WOODED_BADLANDS_PLATEAU ||
						biome == BiomeKeys.MODIFIED_BADLANDS_PLATEAU ||
						biome == BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU)
				{
					lootID = STRAINER_BADLANDS_LOOT;
				}

				// Generate loot
				LootTable lootTable = world.getServer().getLootManager().getTable(lootID);
				LootContext.Builder builder = (new LootContext.Builder(world)).random(random);
				List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.EMPTY));

				// Put into barrel
				Inventory barrel = (Inventory) world.getBlockEntity(pos.down());
				ItemStack remaining = HopperBlockEntity.transfer(null, barrel, list.get(0), null);

				// Deal block damage
				if (remaining.isEmpty() && random.nextInt(8) == 0)
				{
					int i = state.get(DURABILITY);

					if (i-- == 0)
					{
						world.breakBlock(pos, false);
					}
					else
					{
						world.setBlockState(pos, state.with(DURABILITY, i));
					}
				}
			}
		}
	}

	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return COLLISION_SHAPE;
	}

	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return COLLISION_SHAPE;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
															  BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom)
	{
		if (state.get(WATERLOGGED))
		{
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos)
	{
		return !(Boolean)state.get(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return super.getPlacementState(ctx).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(WATERLOGGED);
		builder.add(DURABILITY);
	}

	static
	{
		COLLISION_SHAPE = VoxelShapes.union(Block.createCuboidShape(0, 0, 0, 16, 16, 2),
			Block.createCuboidShape(0, 0, 0, 2, 16, 16),
			Block.createCuboidShape(14, 0, 0, 16, 16, 16),
			Block.createCuboidShape(0, 0, 14, 16, 16, 16));

		WATERLOGGED = Properties.WATERLOGGED;
		DURABILITY = Properties.AGE_7;
	}
}