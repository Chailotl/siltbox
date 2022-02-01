package com.chai.siltbox.temperature;

import com.chai.siltbox.interfaces.IPlayerEntity;
import com.chai.siltbox.Main;
import com.chai.siltbox.mixin.InvokerDamageSource;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class HeatManager
{
	public static final Identifier INTERNAL_SYNC = new Identifier(Main.MOD_ID, "int_sync");
	public static final Identifier EXTERNAL_SYNC = new Identifier(Main.MOD_ID, "ext_sync");

	private static final DamageSource HYPOTHERMIA = InvokerDamageSource.damageSource("hypothermia").setBypassesArmor().setUnblockable();
	private static final DamageSource HYPERTHERMIA = InvokerDamageSource.damageSource("hyperthermia").setBypassesArmor().setUnblockable();

	private boolean updated = false;

	private float internalTemp = 0f;
	private float externalTemp = 0f;
	private int timer = 0;
	private int thermiaTimer = 0;

	private float convertTimeToTemp(long time)
	{
		long temp;
		if (time <= 6000)
		{
			temp = time - 6000;
		}
		else if (time <= 18000)
		{
			temp = -time + 6000;
		}
		else
		{
			temp = time - 30000;
		}

		return temp / 12000f;
	}

	public static int getAbove(float temperature, int value)
	{
		return (int) Math.max(0, Math.floor(Math.abs(temperature)) - value);
	}

	private float getExhaustion()
	{
		return 160f / 24000f * 20f * Math.min(5, getAbove(internalTemp, 0)) / 3f;
	}

	public void update(PlayerEntity player)
	{
		Difficulty difficulty = player.world.getDifficulty();
		float prevInternalTemp = internalTemp;
		float prevExternalTemp = externalTemp;

		if (!player.abilities.invulnerable && ++timer >= 20)
		{
			timer = 0;

			// Calculate external temp
			externalTemp = 0f;
			float insulation = 0f;

			externalTemp += BiomeModifier.getExternal(player);
			if (player.world.getRegistryKey() == World.OVERWORLD)
			{
				int y = player.getBlockPos().getY();
				if (y <= 64)
				{
					int d = Math.min(16, 64 - y);

					externalTemp = (externalTemp * (16 - d) - d) / 16f;
				}
				else
				{
					externalTemp += AltitudeModifier.getExternal(player);
				}
			}
			externalTemp += SunlightModifier.getExternal(player);
			externalTemp += BlockModifier.getExternal(player);
			externalTemp += WetnessModifier.getExternal(player);

			insulation += WetnessModifier.getInsulation(player);

			internalTemp += (externalTemp - internalTemp) / (65f + insulation);

			//System.out.println("Ext:" + externalTemp + " Int: " + internalTemp);

			if (internalTemp >= 1f)
			{
				((IPlayerEntity) player).getThirstManager().addExhaustion(getExhaustion());
			}
			else if (internalTemp <= -1f)
			{
				player.getHungerManager().addExhaustion(getExhaustion());
			}
		}

		int thermiaThreshold = Math.max(20, 50 - getAbove(internalTemp, 5) * 10);

		if (internalTemp >= 4.95f)
		{
			if (++thermiaTimer >= thermiaThreshold)
			{
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)
				{
					player.damage(HYPERTHERMIA, 1.0F);
				}
				thermiaTimer = 0;
			}
		}
		else if (internalTemp <= -4.95f)
		{
			if (++thermiaTimer >= thermiaThreshold)
			{
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)
				{
					player.damage(HYPOTHERMIA, 1.0F);
				}
				thermiaTimer = 0;
			}
		}
		else
		{
			thermiaTimer = 0;
		}

		// Send update to client
		if (!player.world.isClient())
		{
			if (internalTemp != prevInternalTemp || !updated)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeFloat(internalTemp);
				ServerPlayNetworking.send((ServerPlayerEntity) player, INTERNAL_SYNC, buf);
			}
			if (externalTemp != prevExternalTemp || !updated)
			{
				PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
				buf.writeFloat(externalTemp);
				ServerPlayNetworking.send((ServerPlayerEntity) player, EXTERNAL_SYNC, buf);
			}

			updated = true;
		}
	}

	public void fromTag(CompoundTag tag)
	{
		if (tag.contains("internalTemp", 99))
		{
			internalTemp = tag.getFloat("internalTemp");
			externalTemp = tag.getFloat("externalTemp");
			thermiaTimer = tag.getInt("thermiaTimer");
		}
	}

	public void toTag(CompoundTag tag)
	{
		tag.putFloat("internalTemp", internalTemp);
		tag.putFloat("externalTemp", externalTemp);
		tag.putInt("thermiaTimer", thermiaTimer);
	}

	public float getInternalTemp()
	{
		return internalTemp;
	}

	public float getExternalTemp()
	{
		return externalTemp;
	}

	public void setInternalTemp(float temp)
	{
		internalTemp = temp;
	}

	public void setExternalTemp(float temp)
	{
		externalTemp = temp;
	}
}