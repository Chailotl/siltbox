package com.chai.siltbox.item;

import org.jetbrains.annotations.Nullable;

import com.chai.siltbox.Main;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PailItem extends BucketItem
{
	private final Fluid fluid;

	public PailItem(Fluid fluid, Settings settings)
	{
		super(fluid, settings);
		this.fluid = fluid;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
	{
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = raycast(world, user, fluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
		if (hitResult.getType() == HitResult.Type.MISS)
		{
			return TypedActionResult.pass(itemStack);
		}
		else if (hitResult.getType() != HitResult.Type.BLOCK)
		{
			return TypedActionResult.pass(itemStack);
		}
		else
		{
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos2 = blockPos.offset(direction);
			if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack))
			{
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (fluid == Fluids.EMPTY)
				{
					// Collect fluids
					if (block instanceof FluidDrainable && block.getFluidState(blockState) != Fluids.LAVA.getStill(false))
					{
						Fluid fluid = ((FluidDrainable)block).tryDrainFluid(world, blockPos, blockState);
						if (fluid != Fluids.EMPTY)
						{
							user.incrementStat(Stats.USED.getOrCreateStat(this));
							user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
							ItemStack itemStack2 = ItemUsage.method_30012(itemStack, user, new ItemStack(SiltItems.WATER_PAIL));
							if (!world.isClient)
							{
								Criteria.FILLED_BUCKET.trigger((ServerPlayerEntity)user, new ItemStack(SiltItems.WATER_PAIL));
							}

							return TypedActionResult.success(itemStack2, world.isClient());
						}
					}

					return TypedActionResult.fail(itemStack);
				}
				else
				{
					// Place fluids
					BlockPos blockPos3 = block instanceof FluidFillable && fluid == Fluids.WATER ? blockPos : blockPos2;
					if (placeFluid(user, world, blockPos3, blockHitResult))
					{
						onEmptied(world, itemStack, blockPos3);
						if (user instanceof ServerPlayerEntity)
						{
							Criteria.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos3, itemStack);
						}

						user.incrementStat(Stats.USED.getOrCreateStat(this));
						return TypedActionResult.success(getEmptiedStack(itemStack, user), world.isClient());
					}
					else
					{
						return TypedActionResult.fail(itemStack);
					}
				}
			}
			else
			{
				return TypedActionResult.fail(itemStack);
			}
		}
	}

	@Override
	protected ItemStack getEmptiedStack(ItemStack stack, PlayerEntity player)
	{
		return !player.abilities.creativeMode ? new ItemStack(SiltItems.PAIL) : stack;
	}

	@Override
	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult blockHitResult)
	{
		if (!(fluid instanceof FlowableFluid))
		{
			return false;
		}
		else
		{
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			Material material = blockState.getMaterial();
			boolean bl = blockState.canBucketPlace(fluid);
			boolean bl2 = blockState.isAir() || bl || block instanceof FluidFillable && ((FluidFillable)block).canFillWithFluid(world, pos, blockState, fluid);
			if (!bl2)
			{
				return blockHitResult != null && placeFluid(player, world, blockHitResult.getBlockPos().offset(blockHitResult.getSide()), (BlockHitResult)null);
			}
			else if (world.getDimension().isUltrawarm())
			{
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

				for(int l = 0; l < 8; ++l)
				{
					world.addParticle(ParticleTypes.LARGE_SMOKE, i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
				}

				return true;
			}
			else if (block instanceof FluidFillable && fluid == Fluids.WATER)
			{
				((FluidFillable)block).tryFillWithFluid(world, pos, blockState, ((FlowableFluid)fluid).getStill(false));
				playEmptyingSound(player, world, pos);
				return true;
			}
			else
			{
				if (!world.isClient && bl && !material.isLiquid())
				{
					world.breakBlock(pos, true);
				}

				if (!world.setBlockState(pos, fluid.getDefaultState().getBlockState(), 11) && !blockState.getFluidState().isStill())
				{
					return false;
				}
				else
				{
					playEmptyingSound(player, world, pos);
					return true;
				}
			}
		}
	}
}