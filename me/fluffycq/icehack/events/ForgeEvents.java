package me.fluffycq.icehack.events;

import java.util.Iterator;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.opengl.GL11;

public class ForgeEvents {

    public ModuleManager moduleManager = new ModuleManager();
    public Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onUpdate(ClientTickEvent event) {
        if (this.mc.player != null) {
            Iterator iterator = this.moduleManager.getEnabledModules().iterator();

            while (iterator.hasNext()) {
                Module m = (Module) iterator.next();

                m.onUpdate();
            }

        }
    }

    @SubscribeEvent
    public void onRender(Post event) {
        ElementType target = ElementType.EXPERIENCE;

        if (!this.mc.player.isCreative() && this.mc.player.getRidingEntity() instanceof AbstractHorse) {
            target = ElementType.HEALTHMOUNT;
        }

        if (event.getType() == target) {
            Iterator iterator = this.moduleManager.getEnabledModules().iterator();

            while (iterator.hasNext()) {
                Module module = (Module) iterator.next();

                module.onRender();
            }

            GL11.glPushMatrix();
            GL11.glPopMatrix();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }

    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!event.isCanceled()) {
            this.moduleManager.onWorldRender(event);
        }
    }
}
