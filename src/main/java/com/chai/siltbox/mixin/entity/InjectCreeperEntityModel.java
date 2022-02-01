package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.interfaces.ICreeper;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreeperEntityModel.class)
public abstract class InjectCreeperEntityModel<T extends Entity> extends CompositeEntityModel<T> implements ICreeper
{
	private float red = 1f;
	private float blue = 1f;
	private float green = 1f;

	public void setColor(float red, float green, float blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void render(MatrixStack matrices, VertexConsumer vertices, int light,
		int overlay, float red, float green, float blue, float alpha)
	{
		super.render(matrices, vertices, light, overlay, this.red, this.green, this.blue, alpha);
	}
}
