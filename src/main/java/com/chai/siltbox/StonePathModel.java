package com.chai.siltbox;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class StonePathModel implements UnbakedModel, BakedModel, FabricBakedModel
{
	private static final Direction[] CULL_FACES = ArrayUtils.add(Direction.values(), null);
	private static final ModelRotation[] ROTATIONS = new ModelRotation[] {
		ModelRotation.X0_Y0, ModelRotation.X0_Y90, ModelRotation.X0_Y180, ModelRotation.X0_Y270
	};

	private final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[1];
	private Sprite[] SPRITES = new Sprite[1];
	private List<Mesh> mesh = new ArrayList<>();
	private ModelTransformation transformation;

	public StonePathModel(Identifier id)
	{
		SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, id);
	}

	@Override
	public Collection<Identifier> getModelDependencies()
	{
		return Arrays.asList(
			new Identifier(Main.MOD_ID, "block/path_1"),
			new Identifier(Main.MOD_ID, "block/path_2"),
			new Identifier(Main.MOD_ID, "block/path_3"),
			new Identifier(Main.MOD_ID, "block/path_4"),
			new Identifier(Main.MOD_ID, "block/path_5"),
			new Identifier(Main.MOD_ID, "block/path_6"));
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter,
																				  Set<Pair<String, String>> unresolvedTextureReferences)
	{
		return Arrays.asList(SPRITE_IDS);
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter,
								  ModelBakeSettings rotationContainer, Identifier modelId)
	{
		// Get transformation
		transformation = ((JsonUnbakedModel) loader.getOrLoadModel(new Identifier("minecraft:block/thin_block"))).getTransformations();

		// Get the sprites
		SPRITES[0] = textureGetter.apply(SPRITE_IDS[0]);

		// Build the mesh using the Renderer API
		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();

		for (int i = 0; i < 6; ++i)
		{
			for (ModelRotation rotation : ROTATIONS)
			{
				BakedModel model = loader.bake((Identifier) getModelDependencies().toArray()[i], rotation);

				for (Direction cullFace : CULL_FACES)
				{
					for (BakedQuad quad : model.getQuads(null, cullFace, null))
					{
						emitter.fromVanilla(quad, null, cullFace);
						emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
						emitter.spriteColor(0, -1, -1, -1, -1);
						emitter.emit();
					}
				}
				mesh.add(builder.build());
			}
		}

		return this;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random)
	{
		return null;
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return true;
	}

	@Override
	public boolean hasDepth()
	{
		return false;
	}

	@Override
	public boolean isSideLit()
	{
		return true;
	}

	@Override
	public boolean isBuiltin()
	{
		return false;
	}

	@Override
	public Sprite getSprite()
	{
		return SPRITES[0];
	}

	@Override
	public ModelTransformation getTransformation()
	{
		return transformation;
	}

	@Override
	public ModelOverrideList getOverrides()
	{
		return ModelOverrideList.EMPTY;
	}

	@Override
	public boolean isVanillaAdapter()
	{
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos,
										Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(mesh.get(randomSupplier.get().nextInt(mesh.size())));
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
	{
		context.meshConsumer().accept(mesh.get(mesh.size() - 3));
	}
}