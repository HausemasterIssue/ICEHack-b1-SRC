package me.fluffycq.icehack.util;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class ICERenderer extends Tessellator {

    public static ICERenderer INSTANCE = new ICERenderer();
    public static final HashMap FACEMAP = new HashMap();

    public ICERenderer() {
        super(2097152);
    }

    public static void prepare(int mode) {
        prepareGL();
        begin(mode);
    }

    public static void prepareGL() {
        GL11.glBlendFunc(770, 771);
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
        GlStateManager.glLineWidth(1.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }

    public static void begin(int mode) {
        ICERenderer.INSTANCE.getBuffer().begin(mode, DefaultVertexFormats.POSITION_COLOR);
    }

    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        ICERenderer.INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
    }

    public static void drawBox(BlockPos blockPos, int argb, int sides) {
        int a = argb >>> 24 & 255;
        int r = argb >>> 16 & 255;
        int g = argb >>> 8 & 255;
        int b = argb & 255;

        drawBox(blockPos, r, g, b, a, sides);
    }

    public static void drawBoxOpacity(BlockPos blockPos, int argb, int opacity, int sides) {
        int r = argb >>> 16 & 255;
        int g = argb >>> 8 & 255;
        int b = argb & 255;

        drawBox(blockPos, r, g, b, opacity, sides);
    }

    public static void drawBox(float x, float y, float z, int argb, int sides) {
        int a = argb >>> 24 & 255;
        int r = argb >>> 16 & 255;
        int g = argb >>> 8 & 255;
        int b = argb & 255;

        drawBox(ICERenderer.INSTANCE.getBuffer(), x, y, z, 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
        drawBox(ICERenderer.INSTANCE.getBuffer(), (float) blockPos.x, (float) blockPos.y, (float) blockPos.z, 1.0F, 1.0F, 1.0F, r, g, b, a, sides);
    }

    public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, float width, int sides) {
        drawBox(ICERenderer.INSTANCE.getBuffer(), (float) blockPos.x, (float) blockPos.y, (float) blockPos.z, width, 1.0F, 1.0F, r, g, b, a, sides);
    }

    public static BufferBuilder getBufferBuilder() {
        return ICERenderer.INSTANCE.getBuffer();
    }

    public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 1) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 2) != 0) {
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 4) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 8) != 0) {
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 16) != 0) {
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 32) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

    }

    public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
        if ((sides & 17) != 0) {
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 18) != 0) {
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 33) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 34) != 0) {
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 5) != 0) {
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 6) != 0) {
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 9) != 0) {
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 10) != 0) {
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 20) != 0) {
            buffer.pos((double) x, (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 36) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) z).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) z).color(r, g, b, a).endVertex();
        }

        if ((sides & 24) != 0) {
            buffer.pos((double) x, (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) x, (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

        if ((sides & 40) != 0) {
            buffer.pos((double) (x + w), (double) y, (double) (z + d)).color(r, g, b, a).endVertex();
            buffer.pos((double) (x + w), (double) (y + h), (double) (z + d)).color(r, g, b, a).endVertex();
        }

    }

    public static void drawBoundingBoxBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double) bp.x - mc.getRenderManager().viewerPosX;
        double y = (double) bp.y - mc.getRenderManager().viewerPosY;
        double z = (double) bp.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBoundingBoxBottomBlockPos(BlockPos bp, float width, int r, int g, int b, int alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(width);
        Minecraft mc = Minecraft.getMinecraft();
        double x = (double) bp.x - mc.getRenderManager().viewerPosX;
        double y = (double) bp.y - mc.getRenderManager().viewerPosY;
        double z = (double) bp.z - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    static {
        ICERenderer.FACEMAP.put(EnumFacing.DOWN, Integer.valueOf(1));
        ICERenderer.FACEMAP.put(EnumFacing.WEST, Integer.valueOf(16));
        ICERenderer.FACEMAP.put(EnumFacing.NORTH, Integer.valueOf(4));
        ICERenderer.FACEMAP.put(EnumFacing.SOUTH, Integer.valueOf(8));
        ICERenderer.FACEMAP.put(EnumFacing.EAST, Integer.valueOf(32));
        ICERenderer.FACEMAP.put(EnumFacing.UP, Integer.valueOf(2));
    }

    public static final class Line {

        public static final int DOWN_WEST = 17;
        public static final int UP_WEST = 18;
        public static final int DOWN_EAST = 33;
        public static final int UP_EAST = 34;
        public static final int DOWN_NORTH = 5;
        public static final int UP_NORTH = 6;
        public static final int DOWN_SOUTH = 9;
        public static final int UP_SOUTH = 10;
        public static final int NORTH_WEST = 20;
        public static final int NORTH_EAST = 36;
        public static final int SOUTH_WEST = 24;
        public static final int SOUTH_EAST = 40;
        public static final int ALL = 63;
    }

    public static final class Quad {

        public static final int DOWN = 1;
        public static final int UP = 2;
        public static final int NORTH = 4;
        public static final int SOUTH = 8;
        public static final int WEST = 16;
        public static final int EAST = 32;
        public static final int ALL = 63;
    }
}
