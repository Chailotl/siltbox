package com.chai.siltbox.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.chai.siltbox.Main;

import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

@Mixin(TallPlantBlock.class)
public class InjectTallPlantBlock extends PlantBlock
{
	protected InjectTallPlantBlock(Settings settings)
	{
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state,
		BlockView view, BlockPos pos, ShapeContext context)
	{
		if (((AccessorEntityShapeContext) context).getHeldItem().isIn(Main.IGNORE_GRASS))
		{
			return VoxelShapes.empty();
		}
		return super.getOutlineShape(state, view, pos, context);
	}
}