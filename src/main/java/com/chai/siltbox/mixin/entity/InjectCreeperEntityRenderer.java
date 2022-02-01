package com.chai.siltbox.mixin.entity;

import com.chai.siltbox.Main;
import com.chai.siltbox.interfaces.ICreeper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperEntityRenderer.class)
public abstract class InjectCreeperEntityRenderer extends MobEntityRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>>
{
	private static final Identifier NEW_TEXTURE = new Identifier(Main.MOD_ID, "textures/entity/creeper/creeper.png");

	public InjectCreeperEntityRenderer(EntityRenderDispatcher entityRenderDispatcher,
		CreeperEntityModel<CreeperEntity> entityModel, float f)
	{
		super(entityRenderDispatcher, entityModel, f);
	}

	public void render(CreeperEntity creeperEntity, float f, float g,
		MatrixStack matrices, VertexConsumerProvider provider, int i)
	{
		int color = ((ICreeper) creeperEntity).getColor();
		((ICreeper) model).setColor((color >> 16 & 255) / 255f, (color >> 8 & 255) / 255f, (color & 255) / 255f);

		super.render(creeperEntity, f, g, matrices, provider, i);
	}

	@Inject(
		method = "getTexture",
		at = @At("RETURN"),
		cancellable = true)
	private void changeTexture(CreeperEntity creeperEntity,
		CallbackInfoReturnable<Identifier> info)
	{
		if (!((ICreeper) creeperEntity).getCave())
		{
			info.setReturnValue(NEW_TEXTURE);
		}
	}
}