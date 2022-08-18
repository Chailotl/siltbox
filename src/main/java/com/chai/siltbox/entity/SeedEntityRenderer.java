package com.chai.siltbox.entity;

import com.chai.siltbox.Main;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SeedEntityRenderer extends ProjectileEntityRenderer<SeedEntity>
{
	public static final Identifier TEXTURE = new Identifier(Main.MOD_ID, "textures/entity/projectiles/seed.png");

	public SeedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher)
	{
		super(entityRenderDispatcher);
	}

	@Override
	public Identifier getTexture(SeedEntity entity)
	{
		return TEXTURE;
	}
}