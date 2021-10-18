package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.frame.Frame;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class Inventory extends Module {

    Frame frame;

    public Inventory() {
        super("Inventory", 0, Category.HUD);
    }

    public void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void onRender() {
        this.frame = (Frame) ICEHack.clickgui.frames.get(2);
        if (this.frame.extended) {
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GuiIngame guiingame = Inventory.mc.ingameGUI;

            GuiIngame.drawRect(this.frame.x, this.frame.y, this.frame.x + 162, this.frame.y + 54, 1963986960);
            GlStateManager.enableDepth();
            NonNullList items = Inventory.mc.player.inventory.mainInventory;

            GlStateManager.clear(256);
            int size = items.size();

            for (int item = 9; item < size; ++item) {
                int slotX = this.frame.x + 1 + item % 9 * 18;
                int slotY = this.frame.y + 1 + (item / 9 - 1) * 18;

                GlStateManager.pushMatrix();
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                RenderHelper.enableGUIStandardItemLighting();
                Inventory.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack) items.get(item), slotX, slotY);
                Inventory.mc.getRenderItem().renderItemOverlays(Inventory.mc.fontRenderer, (ItemStack) items.get(item), slotX, slotY);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.popMatrix();
            }
        }

    }
}
