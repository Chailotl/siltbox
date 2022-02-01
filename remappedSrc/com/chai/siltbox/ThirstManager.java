package com.chai.siltbox;

import com.chai.siltbox.mixin.InvokerDamageSource;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;

public class ThirstManager
{
	public static final Identifier THIRST_SYNC = new Identifier(Main.MOD_ID, "thirst_sync");
	public static final Identifier HYDRATION_SYNC = new Identifier(Main.MOD_ID, "hydration_sync");

	private static final DamageSource DEHYDRATE = InvokerDamageSource.damageSource("dehydrate").setBypassesArmor().setUnblockable();

	private boolean updated = false;

	private int waterLevel = 20;
	private float hydrationLevel = 5.0f;
	private float exhaustion;
	private int dehydrationTimer;

	private int prevWaterLevel = 20;
	private float prevHydrationLevel = 5.0f;

	public void add(int water, float w)
	{
		waterLevel = Math.min(water + waterLevel, 20);
		hydrationLevel = Math.min(hydrationLevel + water * w * 2f, waterLevel);
	}

	public void drink(Item item, ItemStack stack)
	{
		if (((IWaterComponent) item).isDrink())
		{
			WaterComponent component = ((IWaterComponent) item).getWaterComponent();
			add(component.getThirst(), component.getHydrationModifier());
		}
	}

	public void update(PlayerEntity player)
	{
		Difficulty difficulty = player.world.getDifficulty();
		prevWaterLevel = waterLevel;
		prevHydrationLevel = hydrationLevel;

		if (!player.abilities.invulnerable)
		{
			exhaustion += 160f/24000f;
		}
		if (exhaustion > 4f)
		{
			exhaustion -= 4f;
			if (hydrationLevel > 0f)
			{
				hydrationLevel = Math.max(hydrationLevel - 1f, 0f);
			}
			else if (difficulty != Difficulty.PEACEFUL)
			{
				waterLevel = Math.max(waterLevel - 1, 0);
			}
		}

		if (this.waterLevel <= 0)
		{
			++dehydrationTimer;
			if (dehydrationTimer >= 80)
			{
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)
				{
					player.damage(DEHYDRATE, 1.0F);
				}

				dehydrationTimer = 0;
			}
		}
		else
		{
			dehydrationTimer = 0;
		}

		// Send update to client
		if (!player.world.isClient())
		{
			if (waterLevel != prevWaterLevel || !updated)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeInt(waterLevel);
				ServerPlayNetworking.send((ServerPlayerEntity) player, THIRST_SYNC, buf);
			}
			if (hydrationLevel != prevHydrationLevel || !updated)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeFloat(hydrationLevel);
				ServerPlayNetworking.send((ServerPlayerEntity) player, HYDRATION_SYNC, buf);
			}

			updated = true;
		}
	}

	public void fromTag(CompoundTag tag)
	{
		if (tag.contains("waterLevel", 99))
		{
			waterLevel = tag.getInt("waterLevel");
			hydrationLevel = tag.getFloat("hydrationLevel");
			exhaustion = tag.getFloat("waterExhaustionLevel");
			dehydrationTimer = tag.getInt("waterTickTimer");
		}
	}

	public void toTag(CompoundTag tag)
	{
		tag.putInt("waterLevel", waterLevel);
		tag.putFloat("hydrationLevel", hydrationLevel);
		tag.putFloat("waterExhaustionLevel", exhaustion);
		tag.putInt("waterTickTimer", dehydrationTimer);
	}

	public int getWaterLevel()
	{
		return waterLevel;
	}

	public boolean isNotFull()
	{
		return waterLevel < 20;
	}

	public void addExhaustion(float exhaustion)
	{
		this.exhaustion = Math.min(this.exhaustion + exhaustion, 40f);
	}

	public float getHydrationLevel()
	{
		return hydrationLevel;
	}

	public void setWaterLevel(int waterLevel)
	{
		this.waterLevel = waterLevel;
	}

	@Environment(EnvType.CLIENT)
	public void setHydrationLevelClient(float hydrationLevel)
	{
		this.hydrationLevel = hydrationLevel;
	}
}