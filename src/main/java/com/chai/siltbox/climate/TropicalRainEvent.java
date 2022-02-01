package com.chai.siltbox.climate;

import com.chai.siltbox.Main;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TropicalRainEvent extends WeatherEvent
{
	public static final Identifier TROPICAL_RAIN_EVENT = new Identifier(Main.MOD_ID, "tropical_rain_event");

	public TropicalRainEvent(ClimateManager manager)
	{
		super(manager);
	}

	@Override
	public String getName()
	{
		return "tropical rain";
	}

	@Override
	public int getRandomDuration()
	{
		return manager.getRandom().nextInt(24000) + 24000;
	}

	@Override
	public int getRandomCooldown()
	{
		return manager.getRandom().nextInt(48000) + 12000;
	}

	@Override
	protected void onStart()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(true);
		for (ServerPlayerEntity player : manager.getPlayers())
		{
			ServerPlayNetworking.send(player, TROPICAL_RAIN_EVENT, buf);
		}
	}

	@Override
	protected void onStop()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(false);
		for (ServerPlayerEntity player : manager.getPlayers())
		{
			ServerPlayNetworking.send(player, TROPICAL_RAIN_EVENT, buf);
		}
	}
}