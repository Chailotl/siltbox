package com.chai.siltbox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class InventoryManager
{
	public static final Identifier SORT_ICONS = new Identifier(Main.MOD_ID, "textures/gui/sort_icons.png");
	public static final Identifier SORT_INVENTORY = new Identifier(Main.MOD_ID, "sort_inventory");
	public static final Identifier SORT_CONTAINER = new Identifier(Main.MOD_ID, "sort_container");
	public static final Identifier DEPOSIT_ALL = new Identifier(Main.MOD_ID, "deposit_all");
	public static final Identifier LOOT_ALL = new Identifier(Main.MOD_ID, "loot_all");
	public static final Identifier QUICK_STACK = new Identifier(Main.MOD_ID, "quick_stack");
	public static final Identifier RESTOCK = new Identifier(Main.MOD_ID, "restock");

	public static boolean shift()
	{
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 340) ||
			InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), 344);
	}

	public static void sortInventory(Inventory inv)
	{
		List<ItemStack> list = new ArrayList<>();

		for (int i = 0; i < inv.size(); ++i)
		{
			ItemStack stack = inv.removeStack(i);
			if (!stack.isEmpty())
			{
				list.add(stack);
			}
		}

		list.sort(itemType
			.thenComparing(toolCategory)
			.thenComparing(material)
			.thenComparing(toolType)
			.thenComparing(armorSlot)
			.thenComparing(damage)
			.thenComparing(itemGroup));

		for (int i = 0; i < list.size(); ++i)
		{
			inv.setStack(i, list.get(i));
		}
	}

	public static void sortPlayerInventory(PlayerInventory inv)
	{
		List<ItemStack> list = new ArrayList<>();

		for (int i = 9; i < 36; ++i)
		{
			ItemStack stack = inv.removeStack(i);
			if (!stack.isEmpty())
			{
				list.add(stack);
			}
		}

		list.sort(itemType
			.thenComparing(toolCategory)
			.thenComparing(material)
			.thenComparing(toolType)
			.thenComparing(armorSlot)
			.thenComparing(damage)
			.thenComparing(itemGroup));

		for (int i = 0; i < list.size(); ++i)
		{
			inv.setStack(i + 9, list.get(i));
		}
	}

	// Separates blocks, items, and unstackables
	public static Comparator<ItemStack> itemType = (lhs, rhs) ->
	{
		Item left = lhs.getItem();
		int l = 0;

		if (left instanceof BlockItem) { l = -1; }
		else if (left.getMaxCount() == 1) { l = 1; }

		Item right = rhs.getItem();
		int r = 0;

		if (right instanceof BlockItem) { r = -1; }
		else if (right.getMaxCount() == 1) { r = 1; }

		return l - r;
	};

	// Separates wood, stone, and dirt
	public static Comparator<ItemStack> blockMaterial = (lhs, rhs) -> 0;

	// Separates by creative tab groups
	public static Comparator<ItemStack> itemGroup = (lhs, rhs) ->
	{
		ItemGroup left = lhs.getItem().getGroup();
		ItemGroup right = rhs.getItem().getGroup();
		if (left == null || right == null) { return 0; }

		return left.getIndex() - right.getIndex();
	};

	// Separates weapons, tools, armors
	public static Comparator<ItemStack> toolCategory = (lhs, rhs) ->
	{
		Item left = lhs.getItem();
		Item right = rhs.getItem();
		int r = 0;
		int l = 0;

		if (left instanceof SwordItem) { l = 0; }
		else if (left instanceof BowItem) { l = 1; }
		else if (left instanceof CrossbowItem) { l = 2; }
		else if (left instanceof TridentItem) { l = 3; }
		else if (left instanceof ToolItem) { l = 4; }
		else if (left instanceof ArmorItem) { l = 5; }
		else { l = 6; }

		if (right instanceof SwordItem) { r = 0; }
		else if (right instanceof BowItem) { r = 1; }
		else 	if (right instanceof CrossbowItem) { r = 2; }
		else if (right instanceof TridentItem) { r = 3; }
		else if (right instanceof ToolItem) { r = 4; }
		else if (right instanceof ArmorItem) { r = 5; }
		else { r = 6; }

		return l - r;
	};

	// Separates pickaxes, axes, shovels, hoes
	public static Comparator<ItemStack> toolType = (lhs, rhs) ->
	{
		Item left = lhs.getItem();
		Item right = rhs.getItem();
		int r = 0;
		int l = 0;

		if (left instanceof PickaxeItem) { l = 0; }
		else if (left instanceof AxeItem) { l = 1; }
		else if (left instanceof ShovelItem) { l = 2; }
		else if (left instanceof HoeItem) { l = 3; }
		else { l = 4; }

		if (right instanceof PickaxeItem) { r = 0; }
		else if (right instanceof AxeItem) { r = 1; }
		else 	if (right instanceof ShovelItem) { r = 2; }
		else if (right instanceof HoeItem) { r = 3; }
		else { r = 4; }

		return l - r;
	};

	// Separates helmets, chestplates, leggings, and boots
	public static Comparator<ItemStack> armorSlot = (lhs, rhs) ->
	{
		Item left = lhs.getItem();
		Item right = rhs.getItem();
		int r = 0;
		int l = 0;

		if (left instanceof ArmorItem)
		{
			EquipmentSlot slot = ((ArmorItem) left).getSlotType();

			if (slot == EquipmentSlot.HEAD) { l = 0; }
			else if (slot == EquipmentSlot.CHEST) { l = 1; }
			else if (slot == EquipmentSlot.LEGS) { l = 2; }
			else if (slot == EquipmentSlot.FEET) { l = 3; }
			else if (slot == EquipmentSlot.OFFHAND) { l = 4; }
			else { l = 5; }
		}
		else { l = 6; }

		if (right instanceof ArmorItem)
		{
			EquipmentSlot slot = ((ArmorItem) right).getSlotType();

			if (slot == EquipmentSlot.HEAD) { r = 0; }
			else if (slot == EquipmentSlot.CHEST) { r = 1; }
			else if (slot == EquipmentSlot.LEGS) { r = 2; }
			else if (slot == EquipmentSlot.FEET) { r = 3; }
			else if (slot == EquipmentSlot.OFFHAND) { r = 4; }
			else { r = 5; }
		}
		else { r = 6; }

		return l - r;
	};

	// Separates by materials
	public static Comparator<ItemStack> material = (lhs, rhs) ->
	{
		Item left = lhs.getItem();
		Item right = rhs.getItem();
		int l = 0;
		int r = 0;

		if (left instanceof ToolItem)
		{
			ToolMaterial mat = ((ToolItem) left).getMaterial();

			if (mat == ToolMaterials.NETHERITE) { l = 0; }
			else if (mat == ToolMaterials.DIAMOND) { l = 1; }
			else if (mat == ToolMaterials.GOLD) { l = 2; }
			else if (mat == ToolMaterials.IRON) { l = 3; }
			else if (mat == ToolMaterials.STONE) { l = 4; }
			else if (mat == ToolMaterials.WOOD) { l = 5; }
			else { l = 6; }
		}
		else if (left instanceof ArmorItem)
		{
			ArmorMaterial mat = ((ArmorItem) left).getMaterial();

			if (mat == ArmorMaterials.NETHERITE) { l = 0; }
			else if (mat == ArmorMaterials.DIAMOND) { l = 1; }
			else if (mat == ArmorMaterials.GOLD) { l = 2; }
			else if (mat == ArmorMaterials.IRON) { l = 3; }
			else if (mat == ArmorMaterials.CHAIN) { l = 4; }
			else if (mat == ArmorMaterials.LEATHER) { l = 5; }
			else { l = 6; }
		}
		else { l = 10; }

		if (right instanceof ToolItem)
		{
			ToolMaterial mat = ((ToolItem) right).getMaterial();

			if (mat == ToolMaterials.NETHERITE) { r = 0; }
			else if (mat == ToolMaterials.DIAMOND) { r = 1; }
			else if (mat == ToolMaterials.GOLD) { r = 2; }
			else if (mat == ToolMaterials.IRON) { r = 3; }
			else if (mat == ToolMaterials.STONE) { r = 4; }
			else if (mat == ToolMaterials.WOOD) { r = 5; }
			else { l = 6; }
		}
		else if (right instanceof ArmorItem)
		{
			ArmorMaterial mat = ((ArmorItem) right).getMaterial();

			if (mat == ArmorMaterials.NETHERITE) { r = 0; }
			else if (mat == ArmorMaterials.DIAMOND) { r = 1; }
			else if (mat == ArmorMaterials.GOLD) { r = 2; }
			else if (mat == ArmorMaterials.IRON) { r = 3; }
			else if (mat == ArmorMaterials.CHAIN) { r = 4; }
			else if (mat == ArmorMaterials.LEATHER) { r = 5; }
			else { r = 6; }
		}
		else { r = 10; }

		return l - r;
	};

	public static Comparator<ItemStack> damage = (lhs, rhs) -> lhs.getDamage() - rhs.getDamage();
}