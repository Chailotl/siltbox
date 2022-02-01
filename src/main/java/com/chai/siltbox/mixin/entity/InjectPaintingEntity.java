package com.chai.siltbox.mixin.entity;

import com.google.common.collect.Lists;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Iterator;
import java.util.List;

@Mixin(PaintingEntity.class)
public class InjectPaintingEntity
{
	public ActionResult interact(PlayerEntity player, Hand hand)
	{
		if (player.isSneaking())
		{
			PaintingEntity painting = (PaintingEntity) (Object) this;
			List<PaintingMotive> motives = Lists.newArrayList();

			Iterator<PaintingMotive> iterator = Registry.PAINTING_MOTIVE.iterator();
			while (iterator.hasNext())
			{
				PaintingMotive motive = iterator.next();
				if (motive.getWidth() == painting.motive.getWidth() &&
						motive.getHeight() == painting.motive.getHeight())
				{
					motives.add(motive);
				}
			}

			int index = (motives.indexOf(painting.motive) + 1) % motives.size();
			painting.motive = motives.get(index);

			painting.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}
}