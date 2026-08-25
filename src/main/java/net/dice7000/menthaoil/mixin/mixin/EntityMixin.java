package net.dice7000.menthaoil.mixin.mixin;

import net.dice7000.menthaoil.mixin.IMenthaOilVictim;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements IMenthaOilVictim {
    @Unique private boolean menthaoil$hasAffected;
    @Unique private int menthaoil$count;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if (menthaoil$getAffected()) IMenthaOilVictim.tick((Entity) (Object) this, menthaoil$count++);
    }
    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            shift = At.Shift.AFTER))
    public void saveInject(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        tag.putBoolean("mint_affected", menthaoil$getAffected());
    }
    @Inject(method = "load", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            shift = At.Shift.AFTER))
    public void loadInject(CompoundTag tag, CallbackInfo ci) {
        if (tag.getBoolean("mint_affected")) menthaoil$setAffected();
    }

    @Override public void menthaoil$setAffected() {
        menthaoil$hasAffected = true;
    }
    @Override public boolean menthaoil$getAffected() {
        return menthaoil$hasAffected;
    }
}
