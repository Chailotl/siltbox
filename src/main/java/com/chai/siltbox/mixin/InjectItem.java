package com.chai.siltbox.mixin;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chai.siltbox.interfaces.IPlayerEntity;
import com.chai.siltbox.WaterComponent;
import com.chai.siltbox.interfaces.IWaterComponent;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

@Mixin(Item.class)
public class InjectItem implements IWaterComponent
{
	@Nullable
	private WaterComponent waterComponent;

	@Override
	public boolean isDrink()
	{
		return waterComponent != null;
	}

	@Override
	@Nullable
	public WaterComponent getWaterComponent()
	{
		return waterComponent;
	}

	@Override
	public void setWaterComponent(WaterComponent waterComponent)
	{
		this.waterComponent = waterComponent;
	}

	@Inject(
		method = "use",
		at = @At("TAIL"),
		cancellable = true)
	private void drink(World world, PlayerEntity user, Hand hand,
		CallbackInfoReturnable<TypedActionResult<ItemStack>> info)
	{
		if (this.isDrink())
		{
			ItemStack itemStack = user.getStackInHand(hand);
			if (((IPlayerEntity) user).canDrink(this.getWaterComponent().isAlwaysDrinkable()))
			{
				user.setCurrentHand(hand);
				info.setReturnValue(TypedActionResult.consume(itemStack));
			}
			else
			{
				info.setReturnValue(TypedActionResult.fail(itemStack));
			}
		}
	}

	@Inject(
		method = "getMaxUseTime",
		at = @At("HEAD"),
		cancellable = true)
	private void getDrinkTime(ItemStack stack, CallbackInfoReturnable<Integer> info)
	{
		if (!stack.getItem().isFood() && ((IWaterComponent) stack.getItem()).isDrink())
		{
			info.setReturnValue(32);
		}
	}

	@Inject(
		method = "getUseAction",
		at = @At("HEAD"),
		cancellable = true)
	private void getUseType(ItemStack stack, CallbackInfoReturnable<UseAction> info)
	{
		if (!stack.getItem().isFood() && ((IWaterComponent) stack.getItem()).isDrink())
		{
			info.setReturnValue(UseAction.DRINK);
		}
	}

	@Inject(
		method = "finishUsing",
		at = @At("HEAD"),
		cancellable = true)
	private void drinkItem(ItemStack stack, World world,
		LivingEntity user, CallbackInfoReturnable<ItemStack> info)
	{
		if (!stack.getItem().isFood() && ((IWaterComponent) stack.getItem()).isDrink())
		{
			info.setReturnValue(((IPlayerEntity) user).drinkLiquid(world, stack));
		}
	}

	@Inject(
		method = "appendTooltip",
		at = @At("TAIL"))
	private void addTooltip(ItemStack stack, World world, List<Text> tooltip,
		TooltipContext context, CallbackInfo info)
	{
		Item item = stack.getItem();
		if (item.isFood() || ((IWaterComponent) item).isDrink())
		{
			tooltip.add(LiteralText.EMPTY);
			tooltip.add(new TranslatableText("item.siltbox.consumed.tooltip").formatted(Formatting.DARK_PURPLE));

			FoodComponent food = item.getFoodComponent();
			WaterComponent water = ((IWaterComponent) item).getWaterComponent();

			if (food != null)
			{
				int hunger = food.getHunger();
				float saturation = hunger * food.getSaturationModifier() * 2.0F;
				tooltip.add(new TranslatableText("item.siltbox.hunger.tooltip", hunger).formatted(Formatting.GREEN));
				tooltip.add(new TranslatableText("item.siltbox.saturation.tooltip", new DecimalFormat("#.#").format(saturation)).formatted(Formatting.YELLOW));
			}
			if (water != null)
			{
				int thirst = water.getThirst();
				float hydration = thirst * water.getHydrationModifier() * 2.0F;
				tooltip.add(new TranslatableText("item.siltbox.thirst.tooltip", thirst).formatted(Formatting.AQUA));
				tooltip.add(new TranslatableText("item.siltbox.hydration.tooltip", new DecimalFormat("#.#").format(hydration)).formatted(Formatting.BLUE));
			}
		}
		else if (item == Items.CAKE)
		{
			tooltip.add(LiteralText.EMPTY);
			tooltip.add(new TranslatableText("item.siltbox.consumed.tooltip").formatted(Formatting.DARK_PURPLE));
			tooltip.add(new TranslatableText("item.siltbox.hunger.tooltip", 14).formatted(Formatting.GREEN));
			tooltip.add(new TranslatableText("item.siltbox.saturation.tooltip", new DecimalFormat("#.#").format(2.8f)).formatted(Formatting.YELLOW));
		}
		else if (item == Items.BEE_NEST || item == Items.BEEHIVE)
		{
			CompoundTag tag = stack.getSubTag("BlockEntityTag");
			int bees = tag != null ? ((ListTag) tag.get("Bees")).size() : 0;

			tag = stack.getSubTag("BlockStateTag");
			int honeyLevel = tag != null ? tag.getInt("honey_level") : 0;

			tooltip.add(LiteralText.EMPTY);
			tooltip.add(new TranslatableText("item.siltbox.inside.tooltip").formatted(Formatting.GRAY));
			tooltip.add(new TranslatableText("item.siltbox.bees.tooltip", bees).formatted(Formatting.YELLOW));
			tooltip.add(new TranslatableText("item.siltbox.honey.tooltip", honeyLevel).formatted(Formatting.GOLD));
		}
	}
}
