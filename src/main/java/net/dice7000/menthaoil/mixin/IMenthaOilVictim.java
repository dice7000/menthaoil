package net.dice7000.menthaoil.mixin;

import net.dice7000.menthaoil.MORegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface IMenthaOilVictim {
    void menthaoil$setAffected();
    boolean menthaoil$getAffected();

    static void tick(Entity entity, int tickCount) {
        if (entity.onGround()) entity.setDeltaMovement(new Vec3((Math.random() * 2 - 1), Math.random() * 0.5 + 0.5, (Math.random() * 2 - 1)));
        if (tickCount % 10 == 0) {
            if (entity instanceof LivingEntity l) l.hurt(MORegistry.causeDeathMintDamage(l.level()), l.getMaxHealth() * 0.1F);
            if (!(entity instanceof Player)) {
                entity.setXRot((float) (Math.random() * 180 - 90));
                entity.setYRot((float) (Math.random() * 360 - 180));
            }
        }
    }
}
