package me.fluffycq.icehack.module.modules.render;

import java.awt.Color;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ICERenderer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockHighlight extends Module {

    Setting width = new Setting("Width", this, 1.0D, 1.0D, 5.0D, false);
    Setting rainbow = new Setting("Rainbow", this, false);
    Setting r = new Setting("R", this, 255.0D, 5.0D, 255.0D, true);
    Setting g = new Setting("G", this, 255.0D, 5.0D, 255.0D, true);
    Setting b = new Setting("B", this, 255.0D, 5.0D, 255.0D, true);
    Setting opacity = new Setting("Opacity", this, 35.0D, 35.0D, 255.0D, true);

    public BlockHighlight() {
        super("BlockHighlight", 0, Category.RENDER);
    }

    public void onWorld(RenderEvent event) {
        float[] hue = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int rgb = Color.HSBtoRGB(hue[0], 1.0F, 1.0F);
        int r = rgb >> 16 & 255;
        int g = rgb >> 8 & 255;
        int b = rgb & 255;
        Minecraft mc = Minecraft.getMinecraft();
        RayTraceResult ray = mc.objectMouseOver;

        if (ray.typeOfHit == Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();
            IBlockState iblockstate = mc.world.getBlockState(blockpos);

            if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                ICERenderer.prepare(7);
                if (this.rainbow.getValBoolean()) {
                    ICERenderer.drawBoundingBoxBlockPos(blockpos, (float) ((int) this.width.getValDouble()), r, g, b, (int) this.opacity.getValDouble());
                } else {
                    ICERenderer.drawBoundingBoxBlockPos(blockpos, (float) ((int) this.width.getValDouble()), (int) this.r.getValDouble(), (int) this.g.getValDouble(), (int) this.b.getValDouble(), (int) this.opacity.getValDouble());
                }

                ICERenderer.release();
            }
        }

    }
}
