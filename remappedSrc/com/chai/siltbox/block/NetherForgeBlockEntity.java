package com.chai.siltbox.block;

import com.chai.siltbox.Main;
import com.chai.siltbox.mixin.AccessorAbstractFurnaceBlockEntity;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class NetherForgeBlockEntity extends AbstractFurnaceBlockEntity
{
	private boolean stateChanged = false;

	public NetherForgeBlockEntity()
	{
		super(Main.NETHER_FORGE_ENTITY, Main.NETHER_FORGE_RECIPE);
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText("item.siltbox.nether_forge");
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new NetherForgeScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	@Override
	public void tick()
	{
		if (!stateChanged)
		{
			world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, world.getDimension().isUltrawarm()), 3);
			markDirty();
		}

		AccessorAbstractFurnaceBlockEntity forge = (AccessorAbstractFurnaceBlockEntity)this;
		if (world.getDimension().isUltrawarm())
		{
			forge.setBurnTime(1000);
			forge.setFuelTime(1000);
		}
		else
		{
			forge.setBurnTime(0);
			forge.setFuelTime(0);
		}
		super.tick();
	}
}