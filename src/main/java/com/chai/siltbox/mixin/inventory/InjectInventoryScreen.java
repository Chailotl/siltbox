package com.chai.siltbox.mixin.inventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.chai.siltbox.InventoryManager;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(InventoryScreen.class)
public abstract class InjectInventoryScreen extends HandledScreen<PlayerScreenHandler>
{
	@Shadow private RecipeBookWidget recipeBook;
	private TexturedButtonWidget sort;

	public InjectInventoryScreen(PlayerScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
	}

	@Inject(
		method = "init",
		at = @At("TAIL"))
	private void addSortButton(CallbackInfo info)
	{
		sort = addButton(new TexturedButtonWidget(x + 160, height / 2 - 11, 9, 9, 0, 0, 9, InventoryManager.SORT_ICONS, 27, 18, (buttonWidget) ->
		{
			ClientPlayNetworking.send(InventoryManager.SORT_INVENTORY, new PacketByteBuf(Unpooled.buffer()));
		}, (buttonWidget, matrices, i, j) ->
		{
			if (buttonWidget.isHovered())
			{
				renderOrderedTooltip(matrices, client.textRenderer.wrapLines(
					new TranslatableText("gui.siltbox.sort.tooltip"), 200), i, j);
			}
		}, new TranslatableText("gui.siltbox.sort.tooltip")));
	}

	@Inject(
		method = "tick",
		at = @At("HEAD"))
	private void moveButton(CallbackInfo info)
	{
		sort.setPos(x + 160, sort.y);
	}
}