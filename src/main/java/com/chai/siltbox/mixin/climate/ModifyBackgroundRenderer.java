package com.chai.siltbox.mixin.climate;

import com.chai.siltbox.climate.FogManager;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class ModifyBackgroundRenderer
{
	@Inject(
		method = "applyFog",
		at = @At("TAIL"))
	private static void clearLavaFog(Camera camera, BackgroundRenderer.FogType fogType,
		float viewDistance, boolean thickFog, CallbackInfo info)
	{
		FluidState fluidState = camera.getSubmergedFluidState();
		Entity entity = camera.getFocusedEntity();

		if (fluidState.isIn(FluidTags.LAVA) && entity instanceof PlayerEntity &&
			((PlayerEntity) entity).isCreative())
		{
			RenderSystem.fogStart(0f);
			RenderSystem.fogEnd(15f);
			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			RenderSystem.setupNvFogDistance();
		}
	}

	@ModifyConstant(
		method = "applyFog",
		constant = @Constant(floatValue = 0.75f))
	private static float modifyFogDistance(float original)
	{
		return original * FogManager.getFogStart();
	}

	@Redirect(
		method = "render",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"))
	private static Vec3d newColor(Vec3d pos, CubicSampler.RgbFetcher rgbFetcher)
	{
		MinecraftClient client = MinecraftClient.getInstance();

		Vec3d fog = CubicSampler.sampleColor(pos, rgbFetcher);

		if (client.world.getDimension().hasSkyLight())
		{
			Vec3d sky = client.world.method_23777(client.gameRenderer.getCamera().getBlockPos(), client.getTickDelta());

			double x = fog.x - sky.x;
			double y = fog.y - sky.y;
			double z = fog.z - sky.z;
			double f = 0.3f + FogManager.getFogLevel() * 0.7f + client.world.getRainGradient(0) * 0.7f;
			f = Math.max(0, Math.min(1, f));
			return new Vec3d(sky.x + x * f, sky.y + y * f, sky.z + z * f);
		}

		return fog;
	}
}