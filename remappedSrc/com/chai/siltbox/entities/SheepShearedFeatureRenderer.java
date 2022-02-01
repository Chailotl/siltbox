package com.chai.siltbox.entity;

import com.chai.siltbox.Main;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class SheepShearedFeatureRenderer extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>>
{
	private final SheepEntityModel<SheepEntity> model = new SheepEntityModel<>();
	private static final Identifier TEXTURE = new Identifier(Main.MOD_ID, "textures/entity/sheep/sheep_sheared.png");

	public SheepShearedFeatureRenderer(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context)
	{
		super(context);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
			SheepEntity entity, float limbAngle, float limbDistance, float tickDelta,
			float animationProgress, float headYaw, float headPitch)
	{
		float r, g, b;

		if (entity.hasCustomName() && entity.getName().asString().equals("jeb_"))
		{
			int index = entity.age / 25 + entity.getEntityId();
			int len = DyeColor.values().length;
			float tick = ((entity.age % 25) + tickDelta) / 25f;
			float[] color1 = SheepEntity.getRgbColor(DyeColor.byId(index % len));
			float[] color2 = SheepEntity.getRgbColor(DyeColor.byId((index + 1) % len));
			r = color1[0] * (1f - tick) + color2[0] * tick;
			g = color1[1] * (1f - tick) + color2[1] * tick;
			b = color1[2] * (1f - tick) + color2[2] * tick;
		}
		else
		{
			float[] color = SheepEntity.getRgbColor(entity.getColor());
			r = color[0];
			g = color[1];
			b = color[2];
		}

		render(getContextModel(), model, TEXTURE, matrices, vertexConsumers, light, entity,
				limbAngle, limbDistance, tickDelta, headYaw, headPitch, tickDelta, r, g, b);
	}
}