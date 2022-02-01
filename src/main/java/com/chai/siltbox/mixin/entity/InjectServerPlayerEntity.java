package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.Main;
import com.chai.siltbox.block.SleepingBagBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class InjectServerPlayerEntity
{
	@Shadow public abstract ServerWorld getServerWorld();

	@Shadow @Final public MinecraftServer server;

	@Inject(
		at = @At("HEAD"),
		method = "tick")
	private void updateSaturation(CallbackInfo info)
	{
		Main.onPlayerUpdate((ServerPlayerEntity) (Object) this);
	}

	@Inject(
		at = @At("HEAD"),
		method = "setSpawnPoint",
		cancellable = true)
	private void ignoreSleepingBags(RegistryKey<World> dimension, @Nullable BlockPos pos,
		float angle, boolean spawnPointSet, boolean bl, CallbackInfo info)
	{
		// Sleeping bags should not set spawn points
		if (server.getWorld(dimension).getBlockState(pos).getBlock() instanceof SleepingBagBlock)
		{
			info.cancel();
		}
	}
}