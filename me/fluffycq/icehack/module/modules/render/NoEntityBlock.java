package me.fluffycq.icehack.module.modules.render;

import java.util.ArrayList;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.RayTraceResult.Type;

public class NoEntityBlock extends Module {

    static NoEntityBlock INST;
    public Setting mode;
    public ArrayList modes = new ArrayList();

    public NoEntityBlock() {
        super("NoEntityBlock", 0, Category.RENDER);
        NoEntityBlock.INST = this;
        this.modes.add("Normal");
        this.modes.add("Dev");
        this.mode = new Setting("Mode", this, "Normal", this.modes);
    }

    public static boolean doBlock() {
        return NoEntityBlock.INST.mode.getValString().equalsIgnoreCase("Normal") ? (NoEntityBlock.mc.objectMouseOver == null ? false : NoEntityBlock.INST.isEnabled() && NoEntityBlock.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && NoEntityBlock.mc.objectMouseOver.getBlockPos() != null) : (!NoEntityBlock.INST.mode.getValString().equalsIgnoreCase("Dev") ? false : (NoEntityBlock.mc.objectMouseOver != null ? (NoEntityBlock.mc.objectMouseOver.typeOfHit == null ? false : NoEntityBlock.INST.isEnabled() && NoEntityBlock.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && NoEntityBlock.mc.objectMouseOver.typeOfHit == Type.BLOCK) : false));
    }
}
