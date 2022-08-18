package com.chai.siltbox.item;

import com.chai.siltbox.materials.DolphinMaterial;
import com.chai.siltbox.Main;
import com.chai.siltbox.materials.SteelToolMaterial;
import com.chai.siltbox.materials.StrawMaterial;
import com.chai.siltbox.WaterComponent;
import com.chai.siltbox.interfaces.IWaterComponent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SiltItems
{
	private static Item register(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(Main.MOD_ID, name), item);
	}

	// Materials
	public static final Item STEEL_INGOT = register("steel_ingot",
		new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
	public static final Item ROTTEN_LEATHER = register("rotten_leather",
		new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));
	public static final Item PEARLESCENT_BUBBLE = register("pearlescent_bubble",
		new Item(new FabricItemSettings().group(ItemGroup.MATERIALS)));

	// Food
	public static final Item FIELD_SNACK = register("field_snack",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).build()
		)));
	public static final Item RAW_CALAMARI = register("raw_calamari",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(2).saturationModifier(0.1F).build()
		)));
	public static final Item COOKED_CALAMARI = register("cooked_calamari",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(5).saturationModifier(0.6F).build()
		)));
	public static final Item MARSHMALLOW = register("marshmallow",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build()
		)));
	public static final Item COOKED_MARSHMALLOW = register("cooked_marshmallow",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build()
		)));
	public static final Item BURNT_MARSHMALLOW = register("burnt_marshmallow",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build()
		)));
	public static final Item GRAHAM_CRACKER = register("graham_cracker",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(1).saturationModifier(0.1F).snack().build()
		)));
	public static final Item SMORE = register("smore",
		new Item(new FabricItemSettings().group(ItemGroup.FOOD).food(
			(new FoodComponent.Builder()).hunger(4).saturationModifier(0.1F).build()
		)));

	public static final Item PURIFIED_WATER_BOTTLE_WATER = register("purified_water_bottle",
		new PurifiedWaterBottleItem(new FabricItemSettings().group(ItemGroup.FOOD).maxCount(16)));

	// Equipment
	public static final Item DOLPHIN_FINS = register("dolphin_fins",
		new ArmorItem(DolphinMaterial.INSTANCE, EquipmentSlot.FEET, new FabricItemSettings().group(ItemGroup.COMBAT)));
	public static final Item STRAW_HAT = register("straw_hat",
		new ArmorItem(StrawMaterial.INSTANCE, EquipmentSlot.HEAD, new FabricItemSettings().group(ItemGroup.COMBAT)));
	public static final Item NETHERITE_HORSE_ARMOR = register("netherite_horse_armor",
		new HorseArmorItem(13, "netherite", new FabricItemSettings().maxCount(1).group(ItemGroup.MISC)));

	// Tools
	public static final Item PAIL = register("pail",
		new PailItem(Fluids.EMPTY, new FabricItemSettings().maxCount(16).group(ItemGroup.MISC)));
	public static final Item WATER_PAIL = register("water_pail",
		new PailItem(Fluids.WATER, new FabricItemSettings().recipeRemainder(PAIL).maxCount(1).group(ItemGroup.MISC)));
	public static final Item MILK_PAIL = register("milk_pail",
		new MilkPailItem(new FabricItemSettings().recipeRemainder(PAIL).maxCount(16).group(ItemGroup.MISC)));
	public static final Item WRENCH = register("wrench",
		new WrenchItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)));
	public static final Item TROWEL = register("trowel",
		new TrowelItem(new FabricItemSettings().maxCount(1).group(ItemGroup.TOOLS)));
	public static final Item MARSHMALLOW_ON_A_STICK = register("marshmallow_on_a_stick",
		new MarshmallowOnAStickItem(new FabricItemSettings().maxCount(1).group(ItemGroup.FOOD)));
	public static final Item SLIME_GLOVES = register("slime_gloves",
		new Item(new FabricItemSettings().maxCount(1).group(ItemGroup.TRANSPORTATION)));

	public static final Item WOODEN_MATTOCK = register("wooden_mattock",
		new MattockItem(ToolMaterials.WOOD));
	public static final Item STONE_MATTOCK = register("stone_mattock",
		new MattockItem(ToolMaterials.STONE));
	public static final Item IRON_MATTOCK = register("iron_mattock",
		new MattockItem(ToolMaterials.IRON));
	public static final Item STEEL_MATTOCK = register("steel_mattock",
		new MattockItem(SteelToolMaterial.INSTANCE));
	public static final Item GOLDEN_MATTOCK = register("golden_mattock",
		new MattockItem(ToolMaterials.GOLD));
	public static final Item DIAMOND_MATTOCK = register("diamond_mattock",
		new MattockItem(ToolMaterials.DIAMOND));
	public static final Item NETHERITE_MATTOCK = register("netherite_mattock",
		new MattockItem(ToolMaterials.NETHERITE));

	// Weapons
	public static final Item BLOWGUN = register("blowgun",
		new BlowgunItem(new FabricItemSettings().maxCount(1).group(ItemGroup.COMBAT)));

	static {
		((IWaterComponent) PURIFIED_WATER_BOTTLE_WATER).setWaterComponent(
			(new WaterComponent.Builder()).thirst(5).hydrationModifier(0.6F).build()
		);

		FabricModelPredicateProviderRegistry.register(MARSHMALLOW_ON_A_STICK, new Identifier("cooked"), (itemStack, clientWorld, livingEntity) ->
			livingEntity != null ? MarshmallowOnAStickItem.getCookedState(itemStack) : 0);
	}

	public static void init() {}
}
