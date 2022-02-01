package com.chai.siltbox;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModelProvider implements ModelResourceProvider
{
	@Nullable
	@Override
	public UnbakedModel loadModelResource(Identifier id, ModelProviderContext context)
	{
		String path = id.getPath().replace("block/", "").replace("item/", "");

		if (SiltBlocks.STONE_PATH_TEXTURES.containsKey(path))
		{
			return new StonePathModel(SiltBlocks.STONE_PATH_TEXTURES.get(path));
		}
		else
		{
			return null;
		}
	}
}
