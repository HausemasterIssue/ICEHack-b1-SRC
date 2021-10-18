package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerTravel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = { EntityPlayer.class},
    priority = Integer.MAX_VALUE
)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(
        method = { "travel"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void travel(float strafe, float vertical, float forward, CallbackInfo info) {
        EventPlayerTravel l_Event = new EventPlayerTravel();

        ICEHack.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            info.cancel();
        }

    }
}
