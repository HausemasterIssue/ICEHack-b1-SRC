package me.fluffycq.icehack.module.modules.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.frame.Frame;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PvPInfo extends Module {

    public Setting totemcount;
    public Setting crystalcount;
    public Setting xpcount;
    public Setting gapcount;
    public Setting obicount;
    public Setting red;
    public Setting green;
    public Setting blue;
    public Setting redinfo;
    public Setting greeninfo;
    public Setting blueinfo;
    public Setting rainbow;
    public Setting pixelwidth;
    int color;
    int infocolor;
    ScaledResolution resolution;
    ArrayList info;
    Frame frame;

    public PvPInfo() {
        super("PvPInfo", 0, Category.HUD);
        this.resolution = new ScaledResolution(PvPInfo.mc);
        this.info = new ArrayList();
        this.rainbow = new Setting("Rainbow", this, false);
        this.totemcount = new Setting("Totems", this, true);
        this.crystalcount = new Setting("Crystals", this, true);
        this.gapcount = new Setting("Gapples", this, true);
        this.xpcount = new Setting("XP", this, true);
        this.obicount = new Setting("Obi", this, true);
        this.pixelwidth = new Setting("PixelWidth", this, 1.0D, 1.0D, 10.0D, true);
        this.red = new Setting("Red", this, 255.0D, 3.0D, 255.0D, true);
        this.green = new Setting("Green", this, 26.0D, 3.0D, 255.0D, true);
        this.blue = new Setting("Blue", this, 42.0D, 3.0D, 255.0D, true);
        this.redinfo = new Setting("Count Red", this, 255.0D, 3.0D, 255.0D, true);
        this.greeninfo = new Setting("Count Green", this, 26.0D, 3.0D, 255.0D, true);
        this.blueinfo = new Setting("Count Blue", this, 42.0D, 3.0D, 255.0D, true);
    }

    public void onRender() {
        this.initSetting(this.totemcount);
        this.initSetting(this.crystalcount);
        this.initSetting(this.gapcount);
        this.initSetting(this.obicount);
        this.initSetting(this.xpcount);
        if (this.rainbow.getValBoolean()) {
            this.color = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
        } else {
            this.color = (new Color((int) this.red.getValDouble(), (int) this.green.getValDouble(), (int) this.blue.getValDouble())).getRGB();
        }

        this.infocolor = (new Color((int) this.redinfo.getValDouble(), (int) this.greeninfo.getValDouble(), (int) this.blueinfo.getValDouble())).getRGB();
        this.frame = (Frame) ICEHack.clickgui.frames.get(1);
        if (this.frame.extended) {
            if (this.rainbow.getValBoolean()) {
                this.color = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
            } else {
                this.color = (new Color((int) this.red.getValDouble(), (int) this.green.getValDouble(), (int) this.blue.getValDouble())).getRGB();
            }

            this.infocolor = (new Color((int) this.redinfo.getValDouble(), (int) this.greeninfo.getValDouble(), (int) this.blueinfo.getValDouble())).getRGB();
            int textY;
            Iterator iterator;
            String s;
            int count;
            int baseX;
            ItemStack itemStack;

            if (this.frame.x + 100 <= this.resolution.getScaledWidth() / 2 + 100) {
                textY = this.frame.y + 15;

                for (iterator = this.info.iterator(); iterator.hasNext(); textY += 10) {
                    s = (String) iterator.next();
                    count = 0;

                    for (baseX = 0; baseX < 45; ++baseX) {
                        itemStack = PvPInfo.mc.player.inventory.getStackInSlot(baseX);
                        if (itemStack.getItem().equals(this.getItem(s)) && s.equalsIgnoreCase("Gapple") && itemStack.getItemDamage() == 1) {
                            count += itemStack.stackSize;
                        }

                        if (itemStack.getItem().equals(this.getItem(s)) && !s.equalsIgnoreCase("Gapple")) {
                            count += itemStack.stackSize;
                        }
                    }

                    PvPInfo.mc.fontRenderer.drawStringWithShadow(s, (float) this.frame.x, (float) textY, this.color);
                    PvPInfo.mc.fontRenderer.drawStringWithShadow(String.valueOf(count), (float) (this.frame.x + PvPInfo.mc.fontRenderer.getStringWidth(s) + (int) this.pixelwidth.getValDouble()), (float) textY, this.infocolor);
                }
            }

            if (this.frame.x + 100 >= this.resolution.getScaledWidth() / 2 + 100) {
                textY = this.frame.y + 15;

                for (iterator = this.info.iterator(); iterator.hasNext(); textY += 10) {
                    s = (String) iterator.next();
                    count = 0;

                    for (baseX = 0; baseX < 45; ++baseX) {
                        itemStack = PvPInfo.mc.player.inventory.getStackInSlot(baseX);
                        if (itemStack.getItem().equals(this.getItem(s)) && s.equalsIgnoreCase("Gapple") && itemStack.getItemDamage() == 1) {
                            count += itemStack.stackSize;
                        }

                        if (itemStack.getItem().equals(this.getItem(s)) && !s.equalsIgnoreCase("Gapple")) {
                            count += itemStack.stackSize;
                        }
                    }

                    baseX = this.frame.x + 100 - PvPInfo.mc.fontRenderer.getStringWidth(s) - (int) this.pixelwidth.getValDouble() - PvPInfo.mc.fontRenderer.getStringWidth(String.valueOf(count));
                    PvPInfo.mc.fontRenderer.drawStringWithShadow(s, (float) baseX, (float) textY, this.color);
                    PvPInfo.mc.fontRenderer.drawStringWithShadow(String.valueOf(count), (float) (baseX + PvPInfo.mc.fontRenderer.getStringWidth(s) + (int) this.pixelwidth.getValDouble()), (float) textY, this.infocolor);
                }
            }
        }

    }

    public void initSetting(Setting set) {
        if (!this.info.contains(set.getName()) && set.getValBoolean()) {
            this.info.add(set.getName());
        }

        if (this.info.contains(set.getName()) && !set.getValBoolean()) {
            this.info.remove(set.getName());
        }

    }

    public Item getItem(String name) {
        Item item = null;
        byte b0 = -1;

        switch (name.hashCode()) {
        case -1820702691:
            if (name.equals("Crystals")) {
                b0 = 1;
            }
            break;

        case -1784051470:
            if (name.equals("Totems")) {
                b0 = 0;
            }
            break;

        case 2808:
            if (name.equals("XP")) {
                b0 = 4;
            }
            break;

        case 79062:
            if (name.equals("Obi")) {
                b0 = 3;
            }
            break;

        case 1472157536:
            if (name.equals("Gapples")) {
                b0 = 2;
            }
        }

        switch (b0) {
        case 0:
            item = Items.TOTEM_OF_UNDYING;
            break;

        case 1:
            item = Items.END_CRYSTAL;
            break;

        case 2:
            item = Items.GOLDEN_APPLE;
            break;

        case 3:
            item = Item.getItemFromBlock(Blocks.OBSIDIAN);
            break;

        case 4:
            item = Items.EXPERIENCE_BOTTLE;
        }

        return item;
    }
}
