package com.zen.pdkm.mixin;

import com.zen.pdkm.data.PlayerData;
import com.zen.pdkm.persistent_states.StateSaverAndLoader;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "damage",at = @At("HEAD"), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity thisObject = (PlayerEntity) (Object) this;
        if (!thisObject.getWorld().isClient() && source.getAttacker() instanceof LivingEntity attacker && attacker.isPlayer()) {
            if (!StateSaverAndLoader.getPlayerState(attacker).pvp) {
                System.out.println("Attacker doesn't have pvp on");
                cir.setReturnValue(false);
                return;
            }
            if (!StateSaverAndLoader.getPlayerState(thisObject).pvp) {
                System.out.println("Victim doesn't have pvp on");
                cir.setReturnValue(false);
            }
        }
    }

    /*@Inject(method = "attack",at = @At("HEAD"),cancellable = true)
    private void attackMixin(Entity target, CallbackInfo ci) {
        PlayerEntity thisObject = (PlayerEntity) (Object) this;
        if (!thisObject.getWorld().isClient() && target instanceof ServerPlayerEntity serverTarget) {
            PlayerData playerData = StateSaverAndLoader.getPlayerState((ServerPlayerEntity) thisObject);

            if (!playerData.pvp || !StateSaverAndLoader.getPlayerState(serverTarget).pvp) {
                ci.cancel();
                return;
            }

            System.out.println("hehehehehehehehhehehehehe");

            playerData.pvpTicks = 6000;
        }
    }*/
}
