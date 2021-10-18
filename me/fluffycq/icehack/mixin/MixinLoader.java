package me.fluffycq.icehack.mixin;

import java.util.Map;
import me.fluffycq.icehack.ICEHack;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class MixinLoader implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    public MixinLoader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.icehack.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("name");
        ICEHack.logger.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map data) {
        MixinLoader.isObfuscatedEnvironment = ((Boolean) data.get("runtimeDeobfuscationEnabled")).booleanValue();
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
