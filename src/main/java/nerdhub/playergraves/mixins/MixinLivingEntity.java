package nerdhub.playergraves.mixins;

import nerdhub.playergraves.events.EntityDeathDropsCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void drop(DamageSource damageSource_1, CallbackInfo ci) {
        EntityDeathDropsCallback.EVENT.invoker().dropEntityLoot(((LivingEntity) (Object) this).world, (LivingEntity) (Object) this, damageSource_1, ci);
    }
}
