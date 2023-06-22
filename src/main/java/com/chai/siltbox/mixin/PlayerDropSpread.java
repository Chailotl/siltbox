package com.chai.siltbox.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public abstract class PlayerDropSpread extends Entity {
    public PlayerDropSpread(EntityType<?> type, World world) {
        super(type, world);
    }

    //setVelocity is only called when the player dies to set a random velocity to dropped items
    @ModifyArgs(
            method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At(value = "INVOKE",  target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V", ordinal = 0)
    )
    private void modifySpread(Args args) {
        for (int i = 0; i < args.size(); i++) {
            /*
            TODO: not quite sure yet whether to add a gamerule for this or
            hardcode a smaller drop spread. Let's discuss this later :)
            */
            args.set(i, (double)args.get(i) * 0.2);
        }

    }
}
