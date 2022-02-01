package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chai.siltbox.temperature.HeatManager;
import com.chai.siltbox.IPlayerEntity;
import com.chai.siltbox.Main;
import com.chai.siltbox.ThirstManager;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class InjectPlayerEntity extends LivingEntity implements IPlayerEntity
{
	private ThirstManager thirstManager = new ThirstManager();
	private HeatManager heatManager = new HeatManager();

	@Shadow protected abstract void dropShoulderEntities();
	@Shadow protected abstract void vanishCursedItems();
	@Shadow public abstract void incrementStat(Stat<?> stat);

	protected InjectPlayerEntity(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Override
	public ThirstManager getThirstManager()
	{
		return thirstManager;
	}

	@Override
	public HeatManager getHeatManager()
	{
		return heatManager;
	}

	@Override
	public boolean canDrink(boolean ignoreThirst)
	{
		return ((PlayerEntity)(Object) this).abilities.invulnerable || ignoreThirst || thirstManager.isNotFull();
	}

	@Override
	public ItemStack drinkLiquid(World world, ItemStack stack)
	{
		PlayerEntity player = (PlayerEntity)(Object) this;

		thirstManager.drink(stack.getItem(), stack);
		incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		//world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		if (player instanceof ServerPlayerEntity)
		{
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
		}

		if (!player.abilities.creativeMode)
		{
			stack.decrement(1);
		}
		return stack;
	}

	@Inject(
		method = "tick",
		at = @At("HEAD"))
	private void tickThirst(CallbackInfo info)
	{
		// Update thirst and heat
		PlayerEntity player = (PlayerEntity)(Object) this;

		if (!player.world.isClient)
		{
			thirstManager.update(player);
			heatManager.update(player);
		}

		// Interaction cooldown after eating
		--Main.ticksUntilCanInteract;

		// Dolphin fins
		ItemStack itemStack = getEquippedStack(EquipmentSlot.FEET);
		if (itemStack.getItem() == Main.DOLPHIN_FINS && isSubmergedIn(FluidTags.WATER))
		{
			addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 30, 0, false, false, true));
		}
	}

	@Inject(
		method = "eatFood",
		at = @At("HEAD"))
	private void eatJuicyFood(World world, ItemStack stack,
		CallbackInfoReturnable<ItemStack> info)
	{
		getThirstManager().drink(stack.getItem(), stack);
		Main.ticksUntilCanInteract = 8;
	}

	@Inject(
		method = "dropInventory",
		at = @At("HEAD"))
	private void vanishItems(CallbackInfo info)
	{
		vanishCursedItems();
	}

	@Inject(
		method = "dropShoulderEntities",
		at = @At("HEAD"),
		cancellable = true)
	private void cancelDrop(CallbackInfo info)
	{
		PlayerEntity player = (PlayerEntity)(Object) this;

		if (!player.isSleeping() && !player.isSpectator() &&
			!player.isDead() && !player.isInSneakingPose())
		{
			info.cancel();
		}
	}

	@Inject(
		method = "tickMovement",
		at = @At("TAIL"))
	private void sneakToDropParrots(CallbackInfo info)
	{
		PlayerEntity player = (PlayerEntity)(Object) this;
		if (player.isSneaking()) { dropShoulderEntities(); }

		if (player.world.getDifficulty() == Difficulty.PEACEFUL &&
			player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION))
		{
			if (thirstManager.isNotFull() && player.age % 10 == 0)
			{
				thirstManager.setWaterLevel(thirstManager.getWaterLevel() + 1);
			}
		}
	}

	@Redirect(
		method = "attack",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F",
			ordinal = 0))
	private float modifyDamage(ItemStack stack, EntityGroup group, Entity entity)
	{
		float damage = EnchantmentHelper.getAttackDamage(stack, EntityGroup.AQUATIC);
		if (entity.getType() == EntityType.DROWNED)
		{
			// Keep damage
		}
		else if (group != EntityGroup.AQUATIC && entity.isWet())
		{
			damage /= 2f;
		}
		else
		{
			damage = 0;
		}
		return damage;
	}

	@Inject(
		method = "readCustomDataFromTag",
		at = @At("TAIL"))
	private void readFromTag(CompoundTag tag, CallbackInfo info)
	{
		thirstManager.fromTag(tag);
		heatManager.fromTag(tag);
	}

	@Inject(
		method = "writeCustomDataToTag",
		at = @At("TAIL"))
	private void writeToTag(CompoundTag tag, CallbackInfo info)
	{
		thirstManager.toTag(tag);
		heatManager.toTag(tag);
	}
}
