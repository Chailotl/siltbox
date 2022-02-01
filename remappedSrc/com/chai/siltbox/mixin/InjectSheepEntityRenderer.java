package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.entity.SheepShearedFeatureRenderer;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.entity.passive.SheepEntity;

@Mixin(SheepEntityRenderer.class)
public abstract class InjectSheepEntityRenderer extends LivingEntityRenderer<SheepEntity, SheepEntityModel<SheepEntity>>
{
	public InjectSheepEntityRenderer(EntityRenderDispatcher dispatcher, SheepEntityModel<SheepEntity> model, float shadowRadius)
	{
		super(dispatcher, model, shadowRadius);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void renderWool(CallbackInfo info)
	{
		addFeature(new SheepShearedFeatureRenderer(this));
	}
}