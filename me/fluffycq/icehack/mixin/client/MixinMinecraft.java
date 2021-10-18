package me.fluffycq.icehack.mixin.client;

import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.KeyEvent;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = { Minecraft.class},
    priority = Integer.MAX_VALUE
)
public class MixinMinecraft {

    @Inject(
        method = { "runTickKeyboard"},
        at = {             @At(
                value = "INVOKE",
                remap = false,
                target = "Lorg/lwjgl/input/Keyboard;getEventKey()I",
                ordinal = 0,
                shift = At.Shift.BEFORE
            )}
    )
    private void onKeyboard(CallbackInfo callbackInfo) {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

        if (Keyboard.getEventKeyState()) {
            KeyEvent event = new KeyEvent(i);

            ICEHack.EVENT_BUS.post(event);
            if (event.isCancelled()) {
                callbackInfo.cancel();
            }

            if (ICEHack.fevents.moduleManager != null) {
                Iterator iterator = ICEHack.fevents.moduleManager.moduleList.iterator();

                while (iterator.hasNext()) {
                    Module module = (Module) iterator.next();

                    module.onKey(i);
                }
            }
        }

    }

    @Inject(
        method = { "run"},
        at = {             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V",
                shift = At.Shift.BEFORE
            )}
    )
    public void displayCrashReport(CallbackInfo _info) {
        ICEHack.save();
    }

    @Inject(
        method = { "shutdown"},
        at = {             @At("HEAD")}
    )
    public void shutdown(CallbackInfo info) {
        ICEHack.save();
    }
}
