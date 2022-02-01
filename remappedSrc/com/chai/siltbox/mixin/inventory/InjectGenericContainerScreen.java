package com.chai.siltbox.mixin.inventory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.chai.siltbox.InventoryManager;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Mixin(GenericContainerScreen.class)
public abstract class InjectGenericContainerScreen extends HandledScreen<GenericContainerScreenHandler>
{
	@Shadow private int rows;

	public InjectGenericContainerScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title)
	{
		super(handler, inventory, title);
	}

	@Override
	protected void init()
	{
		super.init();

		int y_ = y + 19 + rows * 18;

		addButton(new TexturedButtonWidget(x + 160, y_, 9, 9, 0, 0, 9, InventoryManager.SORT_ICONS, 27, 18, (buttonWidget) ->
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

		addButton(new TexturedButtonWidget(x + 160 - 11, y_, 9, 9, 9, 0, 9, InventoryManager.SORT_ICONS, 27, 18, (buttonWidget) ->
		{
			Identifier id = InventoryManager.QUICK_STACK;
			if (InventoryManager.shift()) { id = InventoryManager.DEPOSIT_ALL; }

			ClientPlayNetworking.send(id, new PacketByteBuf(Unpooled.buffer()));
		}, (buttonWidget, matrices, i, j) ->
		{
			String tooltip = "gui.siltbox.quick_stack.tooltip";
			if (InventoryManager.shift()) { tooltip = "gui.siltbox.deposit.tooltip"; }

			if (buttonWidget.isHovered())
			{
				renderOrderedTooltip(matrices, client.textRenderer.wrapLines(
					new TranslatableText(tooltip), 200), i, j);
			}
		}, new TranslatableText("gui.siltbox.quick_stack.tooltip")));

		addButton(new TexturedButtonWidget(x + 160 - 22, y_, 9, 9, 18, 0, 9, InventoryManager.SORT_ICONS, 27, 18, (buttonWidget) ->
		{
			Identifier id = InventoryManager.RESTOCK;
			if (InventoryManager.shift()) { id = InventoryManager.LOOT_ALL; }

			ClientPlayNetworking.send(id, new PacketByteBuf(Unpooled.buffer()));
		}, (buttonWidget, matrices, i, j) ->
		{
			String tooltip = "gui.siltbox.restock.tooltip";
			if (InventoryManager.shift()) 	{ tooltip = "gui.siltbox.loot.tooltip"; }

			if (buttonWidget.isHovered())
			{
				renderOrderedTooltip(matrices, client.textRenderer.wrapLines(
					new TranslatableText(tooltip), 200), i, j);
			}
		}, new TranslatableText("gui.siltbox.restock.tooltip")));


		addButton(new TexturedButtonWidget(x + 160, y + 6, 9, 9, 0, 0, 9, InventoryManager.SORT_ICONS, 27, 18, (buttonWidget) ->
		{
			ClientPlayNetworking.send(InventoryManager.SORT_CONTAINER, new PacketByteBuf(Unpooled.buffer()));
		}, (buttonWidget, matrices, i, j) ->
		{
			if (buttonWidget.isHovered())
			{
				renderOrderedTooltip(matrices, client.textRenderer.wrapLines(
					new TranslatableText("gui.siltbox.sort_container.tooltip"), 200), i, j);
			}
		}, new TranslatableText("gui.siltbox.sort_container.tooltip")));
	}
}