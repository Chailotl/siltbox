package com.chai.siltbox.block;

import com.chai.siltbox.Main;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.BlastFurnaceRecipeBookScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NetherForgeScreen extends AbstractFurnaceScreen<NetherForgeScreenHandler>
{
	private static final Identifier BACKGROUND = new Identifier(Main.MOD_ID, "textures/gui/container/nether_forge.png");

	public NetherForgeScreen(NetherForgeScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, new BlastFurnaceRecipeBookScreen(), inventory, title, BACKGROUND);
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
	{
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		client.getTextureManager().bindTexture(BACKGROUND);
		drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
		int level;
		if (handler.isBurning())
		{
			level = handler.getFuelProgress();
			drawTexture(matrices, x + 56, y + 54 + 12 - level, 176, 12 - level, 14, level + 1);
		}

		level = handler.getCookProgress();
		drawTexture(matrices, x + 79, y + 34, 176, 14, level + 1, 16);
	}
}