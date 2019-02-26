package nerdhub.playergraves.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface EntityDeathDropsCallback {
    public static final Event<EntityDeathDropsCallback> EVENT = EventFactory.createArrayBacked(EntityDeathDropsCallback.class,
            (listeners) -> (world, livingEntity, damageSource, ci) -> {
                for (EntityDeathDropsCallback event : listeners) {
                    event.dropEntityLoot(world, livingEntity, damageSource, ci);
                }
            });

    void dropEntityLoot(World world, LivingEntity livingEntity, DamageSource damageSource, CallbackInfo ci);
}
