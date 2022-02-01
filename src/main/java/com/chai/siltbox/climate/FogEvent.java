package com.chai.siltbox.climate;

import com.chai.siltbox.Main;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FogEvent extends WeatherEvent
{
	public static final Identifier FOG_EVENT = new Identifier(Main.MOD_ID, "fog_event");

	public FogEvent(ClimateManager manager)
	{
		super(manager);
	}

	@Override
	public String getName()
	{
		return "fog";
	}

	@Override
	public int getRandomDuration()
	{
		return manager.getRandom().nextInt(3000) + 4000;
	}

	@Override
	public int getRandomCooldown()
	{
		return 0;
	}

	@Override
	public boolean canRun()
	{
		if (manager.getTimeOfDay() == 18000 && manager.getRandom().nextInt(5) == 0)
		{
			return true;
		}

		return false;
	}

	@Override
	protected void onStart()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(true);
		for (ServerPlayerEntity player : manager.getPlayers())
		{
			ServerPlayNetworking.send(player, FOG_EVENT, buf);
		}
	}

	@Override
	protected void onStop()
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBoolean(false);
		for (ServerPlayerEntity player : manager.getPlayers())
		{
			ServerPlayNetworking.send(player, FOG_EVENT, buf);
		}
	}

	public enum Fog {
		CLEAR,
		MIST,
		THIN_FOG,
		FOG,
		THICK_FOG,
	}
}