package com.chai.siltbox.temperature;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class HeatManager
{
	public static final Identifier INTERNAL_SYNC = new Identifier(Main.MOD_ID, "int_sync");
	public static final Identifier EXTERNAL_SYNC = new Identifier(Main.MOD_ID, "ext_sync");

	private static final DamageSource HYPOTHERMIA = InvokerDamageSource.damageSource("hypothermia").setBypassesArmor().setUnblockable();
	private static final DamageSource HYPERTHERMIA = InvokerDamageSource.damageSource("hyperthermia").setBypassesArmor().setUnblockable();

	private boolean updated = false;

	private float internalTemp = 0f;
	private float externalTemp = 0f;
	private int thermiaTimer = 0;

	private float prevInternalTemp = 0f;
	private float prevExternalTemp = 0f;

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

	public void update(PlayerEntity player)
	{
		Difficulty difficulty = player.world.getDifficulty();
		prevInternalTemp = internalTemp;
		prevExternalTemp = externalTemp;

		// Calculate external temp
		World world = player.world;
		BlockPos pos = player.getBlockPos();
		Biome biome = world.getBiome(pos);
		externalTemp = 0f;
		float insulation = 0f;

		externalTemp += BiomeModifier.getExternal(player);
		externalTemp += AltitudeModifier.getExternal(player);
		externalTemp += SunlightModifier.getExternal(player);
		externalTemp += BlockModifier.getExternal(player);

		insulation += WetnessModifier.getInsulation(player);

		System.out.println(externalTemp);

		if (internalTemp >= 5f)
		{
			internalTemp = 5f;

			if (++thermiaTimer >= 80)
			{
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL)
				{
					player.damage(HYPERTHERMIA, 1.0F);
				}
				thermiaTimer = 0;
			}
		}
		else if (internalTemp <= -5f)
		{
			internalTemp = -5f;

			if (++thermiaTimer >= 80)
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