package com.chai.siltbox.mixin.pets;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.IFollower;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.World;

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

	@Inject(
		method = "writeCustomDataToTag",
		at = @At("TAIL"))
	private void writeCustomData(CompoundTag tag, CallbackInfo info)
	{
		tag.putBoolean("Following", following);
	}

	@Inject(
		method = "readCustomDataFromTag",
		at = @At("TAIL"))
	private void readCustomData(CompoundTag tag, CallbackInfo info)
	{
		following = tag.getBoolean("Following");
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
				actionResult = ActionResult.SUCCESS;
			}
			else if (!following && !sitting)
			{
				text = "gui.siltbox.pet_sit";
			}
			else if (sitting)
			{
				following = true;
				text = "gui.siltbox.pet_follow";
			}
		}

		if (!player.world.isClient() && text != null)
		{
			ServerPlayerEntity ply = (ServerPlayerEntity) player;

			ply.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR,
				new TranslatableText(text, getDisplayName())));
		}

		return actionResult;
	}
}