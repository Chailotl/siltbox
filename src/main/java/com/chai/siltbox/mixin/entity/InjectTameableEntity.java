package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.SiltBlocks;
import com.chai.siltbox.block.PetBedBlock;
import com.chai.siltbox.interfaces.IFollower;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class InjectTameableEntity extends AnimalEntity implements IFollower
{
	@Shadow private boolean sitting;
	@Shadow public abstract boolean isOwner(LivingEntity entity);

	protected InjectTameableEntity(EntityType<? extends AnimalEntity> entityType, World world)
	{
		super(entityType, world);
	}

	private boolean following = true;
	private BlockPos wanderOrigin = null;

	@Inject(
		method = "writeCustomDataToTag",
		at = @At("TAIL"))
	private void writeCustomData(CompoundTag tag, CallbackInfo info)
	{
		tag.putBoolean("Following", following);
		if (wanderOrigin != null)
		{
			tag.putIntArray("WanderOrigin", new int[] {
				wanderOrigin.getX(),
				wanderOrigin.getY(),
				wanderOrigin.getZ()
			});
		}
	}

	@Inject(
		method = "readCustomDataFromTag",
		at = @At("TAIL"))
	private void readCustomData(CompoundTag tag, CallbackInfo info)
	{
		following = tag.getBoolean("Following");
		if (tag.contains("WanderOrigin"))
		{
			int[] arr = tag.getIntArray("WanderOrigin");
			wanderOrigin = new BlockPos(arr[0], arr[1], arr[2]);
		}
	}

	@Override
	public boolean getFollowing()
	{
		return following;
	}

	@Override
	public void setFollowing(boolean bool)
	{
		following = bool;
	}

	@Override
	public BlockPos getWanderOrigin()
	{
		return wanderOrigin;
	}

	@Override
	public void removeWanderOrigin()
	{
		wanderOrigin = null;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand)
	{
		ActionResult actionResult = super.interactMob(player, hand);
		String text = null;

		if ((!actionResult.isAccepted() || isBaby()) && isOwner(player))
		{
			TameableEntity ent = (TameableEntity)(Object) this;
			if (ent instanceof ParrotEntity && ((ParrotEntity) ent).isInAir())
			{
				return actionResult;
			}

			if (following && !sitting)
			{
				following = false;
				text = "gui.siltbox.pet_wander";

				outerLoop:
				for (int x = -5; x <= 5; ++x)
				{
					for (int y = -2; y <= 2; ++y)
					{
						for (int z = -5; z <= 5; ++z)
						{
							// Find unoccupied bed
							BlockPos pos = getBlockPos().add(x, y, z);
							BlockState state = world.getBlockState(pos);

							if (state.getBlock() == SiltBlocks.PET_BED && !state.get(PetBedBlock.OCCUPIED))
							{
								// Claim bed
								world.setBlockState(pos, state.with(PetBedBlock.OCCUPIED, true));
								wanderOrigin = pos;

								// Move towards bed as a visual indicator
								navigation.startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
								text = "gui.siltbox.pet_wander_nearby";
								break outerLoop;
							}
						}
					}
				}

				actionResult = ActionResult.SUCCESS;
			}
			else if (!following && !sitting)
			{
				text = "gui.siltbox.pet_sit";

				forgetBed();
			}
			else if (sitting)
			{
				following = true;
				text = "gui.siltbox.pet_follow";
			}
		}

		if (!world.isClient() && text != null)
		{
			ServerPlayerEntity ply = (ServerPlayerEntity) player;

			ply.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR,
				new TranslatableText(text, getDisplayName())));
		}

		return actionResult;
	}

	@Inject(
			  method = "onDeath",
			  at = @At("HEAD"))
	private void forgetBed(DamageSource source, CallbackInfo ci)
	{
		forgetBed();
	}

	private void forgetBed()
	{
		if (wanderOrigin != null)
		{
			world.setBlockState(wanderOrigin, world.getBlockState(wanderOrigin).with(PetBedBlock.OCCUPIED, false));
			wanderOrigin = null;
		}
	}
}