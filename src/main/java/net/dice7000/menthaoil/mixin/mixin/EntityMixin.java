package net.dice7000.menthaoil.mixin.mixin;

import net.dice7000.menthaoil.MORegistry;
import net.dice7000.menthaoil.mixin.IMenthaOilVictim;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements IMenthaOilVictim {
    @Unique private final Entity menthaoil$this = (Entity) (Object) this;
    @Shadow private boolean onGround;
    @Shadow private Vec3 deltaMovement;
    @Shadow private Level level;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Unique private boolean menthaoil$hasAffected;
    @Unique private int menthaoil$count;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if (menthaoil$getAffected()) {
            if (onGround) deltaMovement = new Vec3((Math.random() * 2 - 1), Math.random() * 0.5 + 0.5, (Math.random() * 2 - 1));
            if (menthaoil$count % 10 == 0) {
                xRot = (float) (Math.random() * 360 - 180);
                yRot = (float) (Math.random() * 180 - 90);
                if (menthaoil$this instanceof LivingEntity l) l.hurt(MORegistry.causeDeathMintDamage(level), l.getMaxHealth() * 0.1F);
            }
            menthaoil$count++;

        }
    }
    @Inject(method = "isNoGravity", at = @At("HEAD"), cancellable = true)
    public void isNoGravityInject(CallbackInfoReturnable<Boolean> cir) {
        if (menthaoil$hasAffected) cir.setReturnValue(false);
    }

    @Override public void menthaoil$setAffected() {
        menthaoil$hasAffected = true;
    }
    @Override public boolean menthaoil$getAffected() {
        return menthaoil$hasAffected;
    }
}
