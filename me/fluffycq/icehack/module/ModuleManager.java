package me.fluffycq.icehack.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.module.modules.combat.AutoCrystal;
import me.fluffycq.icehack.module.modules.combat.AutoTotem;
import me.fluffycq.icehack.module.modules.combat.AutoTrap;
import me.fluffycq.icehack.module.modules.combat.SelfTrap;
import me.fluffycq.icehack.module.modules.combat.StrDetect;
import me.fluffycq.icehack.module.modules.combat.TickAura;
import me.fluffycq.icehack.module.modules.exploit.AutoKickBow;
import me.fluffycq.icehack.module.modules.exploit.MountBypass;
import me.fluffycq.icehack.module.modules.exploit.Vanish;
import me.fluffycq.icehack.module.modules.hud.Console;
import me.fluffycq.icehack.module.modules.hud.GUI;
import me.fluffycq.icehack.module.modules.hud.Inventory;
import me.fluffycq.icehack.module.modules.hud.ModuleArray;
import me.fluffycq.icehack.module.modules.hud.PvPInfo;
import me.fluffycq.icehack.module.modules.misc.AntiRodarg;
import me.fluffycq.icehack.module.modules.misc.ChatSuffix;
import me.fluffycq.icehack.module.modules.misc.FastItem;
import me.fluffycq.icehack.module.modules.misc.TotemPopAlert;
import me.fluffycq.icehack.module.modules.movement.ElytraFly;
import me.fluffycq.icehack.module.modules.movement.Freecam;
import me.fluffycq.icehack.module.modules.movement.Sprint;
import me.fluffycq.icehack.module.modules.render.BlockHighlight;
import me.fluffycq.icehack.module.modules.render.HoleESP;
import me.fluffycq.icehack.module.modules.render.NoEntityBlock;
import me.fluffycq.icehack.module.modules.render.ShulkerViewer;
import me.fluffycq.icehack.module.modules.render.Tablist;
import me.fluffycq.icehack.util.ICERenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ModuleManager {

    public ArrayList moduleList = new ArrayList();

    public ModuleManager() {
        this.moduleList.add(new Sprint());
        this.moduleList.add(new GUI());
        this.moduleList.add(new AutoTotem());
        this.moduleList.add(new ModuleArray());
        this.moduleList.add(new Vanish());
        this.moduleList.add(new Freecam());
        this.moduleList.add(new PvPInfo());
        this.moduleList.add(new Inventory());
        this.moduleList.add(new MountBypass());
        this.moduleList.add(new AutoKickBow());
        this.moduleList.add(new AntiRodarg());
        this.moduleList.add(new ElytraFly());
        this.moduleList.add(new StrDetect());
        this.moduleList.add(new AutoCrystal());
        this.moduleList.add(new Console());
        this.moduleList.add(new ShulkerViewer());
        this.moduleList.add(new NoEntityBlock());
        this.moduleList.add(new FastItem());
        this.moduleList.add(new SelfTrap());
        this.moduleList.add(new AutoTrap());
        this.moduleList.add(new HoleESP());
        this.moduleList.add(new TotemPopAlert());
        this.moduleList.add(new Tablist());
        this.moduleList.add(new BlockHighlight());
        this.moduleList.add(new ChatSuffix());
        this.moduleList.add(new TickAura());
        Comparator lengthComp = new Comparator() {
            public int compare(Module o1, Module o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        };

        Collections.sort(this.moduleList, lengthComp);
    }

    public void onWorldRender(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().profiler.startSection("kami");
        Minecraft.getMinecraft().profiler.startSection("setup");
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GlStateManager.disableDepth();
        GlStateManager.glLineWidth(1.0F);
        Vec3d renderPos = getInterpolatedPos(Minecraft.getMinecraft().player, event.getPartialTicks());
        RenderEvent e = new RenderEvent(ICERenderer.INSTANCE, renderPos);

        e.resetTranslation();
        Minecraft.getMinecraft().profiler.endSection();
        this.getEnabledModules().stream().filter(test<invokedynamic>()).forEach(accept<invokedynamic>(e));
        Minecraft.getMinecraft().profiler.startSection("release");
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        ICERenderer.releaseGL();
        Minecraft.getMinecraft().profiler.endSection();
        Minecraft.getMinecraft().profiler.endSection();
    }

    public static Vec3d getInterpolatedPos(Entity entity, float ticks) {
        return (new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ)).add(getInterpolatedAmount(entity, (double) ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
        return getInterpolatedAmount(entity, vec.x, vec.y, vec.z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public ArrayList getEnabledModules() {
        ArrayList enabledModules = new ArrayList();
        Iterator iterator = this.moduleList.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.getState()) {
                enabledModules.add(module);
            }
        }

        return enabledModules;
    }

    public Module getModule(String name) {
        Module the_module = null;
        Iterator iterator = this.moduleList.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.name.equalsIgnoreCase(name)) {
                the_module = module;
                break;
            }
        }

        return the_module;
    }

    public boolean hasModules(Category category) {
        ArrayList modules = new ArrayList();
        Iterator iterator = this.moduleList.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.getCategory().equals(category)) {
                modules.add(module);
            }
        }

        if (modules.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList getModulesByCategory(Category category) {
        ArrayList modules = new ArrayList();
        Iterator iterator = this.moduleList.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.getCategory().equals(category)) {
                modules.add(module);
            }
        }

        return modules;
    }

    private static void lambda$onWorldRender$1(RenderEvent e, Module module) {
        Minecraft.getMinecraft().profiler.startSection(module.getName());
        module.onWorld(e);
        Minecraft.getMinecraft().profiler.endSection();
    }

    private static boolean lambda$onWorldRender$0(Module module) {
        return module.isEnabled();
    }
}
