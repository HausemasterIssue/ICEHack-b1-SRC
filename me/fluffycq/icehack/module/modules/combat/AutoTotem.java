package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {

    Setting priority;
    public ArrayList priorities = new ArrayList();
    Setting health = new Setting("Health", this, 11.0D, 5.0D, 36.0D, false);
    Setting messages;
    Setting smart;
    Setting smartdelay;
    Setting allowGap;
    Setting debug;
    int totems = 0;
    int smartTick;
    boolean isSmart;

    public AutoTotem() {
        super("AutoTotem", 0, Category.COMBAT);
        this.priorities.add("Totem");
        this.priorities.add("Crystal");
        this.priorities.add("Gapple");
        this.priority = new Setting("Priority", this, "Totem", this.priorities);
        this.allowGap = new Setting("AllowGap", this, true);
        this.messages = new Setting("Alerts", this, false);
        this.debug = new Setting("Debug", this, false);
    }

    public void switchOffhand(String item) {
        Item prioItem = null;
        byte tslot = -1;

        switch (item.hashCode()) {
        case -1582753002:
            if (item.equals("Crystal")) {
                tslot = 1;
            }
            break;

        case 80997281:
            if (item.equals("Totem")) {
                tslot = 0;
            }
            break;

        case 2125698931:
            if (item.equals("Gapple")) {
                tslot = 2;
            }
        }

        switch (tslot) {
        case 0:
            prioItem = Items.TOTEM_OF_UNDYING;
            break;

        case 1:
            prioItem = Items.END_CRYSTAL;
            break;

        case 2:
            prioItem = Items.GOLDEN_APPLE;
        }

        int slot = InventoryUtil.getItem(prioItem);

        if (slot != -1) {
            if (this.messages.getValBoolean()) {
                Messages.sendChatMessage("AutoTotem equipped you with a " + item);
            }

            AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, AutoTotem.mc.player);
            AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, AutoTotem.mc.player);
            AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, AutoTotem.mc.player);
            AutoTotem.mc.playerController.updateController();
        } else {
            int tslot1 = InventoryUtil.getItem(Items.TOTEM_OF_UNDYING);

            if (tslot1 != -1) {
                if (this.messages.getValBoolean()) {
                    Messages.sendChatMessage("Fallback Emergency Totem equipped!");
                }

                AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, tslot1, 0, ClickType.PICKUP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.windowClick(AutoTotem.mc.player.inventoryContainer.windowId, tslot1, 0, ClickType.PICKUP, AutoTotem.mc.player);
                AutoTotem.mc.playerController.updateController();
            }
        }

    }

    public void onEnable() {
        this.smartTick = 0;
        this.isSmart = false;
    }

    public void onUpdate() {
        if (AutoTotem.mc.currentScreen == null || AutoTotem.mc.currentScreen instanceof GuiInventory) {
            this.totems = AutoTotem.mc.player.inventory.mainInventory.stream().filter((itemStack) -> {
                return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
            }).mapToInt(ItemStack::getCount).sum();
            if (AutoTotem.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
                ++this.totems;
            }

            this.modInfo = String.valueOf(this.totems) != null ? String.valueOf(this.totems) : "0";
            if ((double) (AutoTotem.mc.player.getHealth() + AutoTotem.mc.player.getAbsorptionAmount()) >= this.health.getValDouble() && !AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(this.getItemVal(this.priority.getValString()))) {
                if (this.allowGap.getValBoolean()) {
                    if (!AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE)) {
                        this.switchOffhand(this.priority.getValString());
                    }
                } else {
                    this.switchOffhand(this.priority.getValString());
                }
            }

            if ((double) (AutoTotem.mc.player.getHealth() + AutoTotem.mc.player.getAbsorptionAmount()) <= this.health.getValDouble() && !AutoTotem.mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
                this.switchOffhand("Totem");
            }

        }
    }

    public void onDisable() {
        this.smartTick = 0;
        this.isSmart = false;
    }

    public Item getItemVal(String i) {
        Item prioItem = null;
        byte b0 = -1;

        switch (i.hashCode()) {
        case -1582753002:
            if (i.equals("Crystal")) {
                b0 = 1;
            }
            break;

        case 80997281:
            if (i.equals("Totem")) {
                b0 = 0;
            }
            break;

        case 2125698931:
            if (i.equals("Gapple")) {
                b0 = 2;
            }
        }

        switch (b0) {
        case 0:
            prioItem = Items.TOTEM_OF_UNDYING;
            break;

        case 1:
            prioItem = Items.END_CRYSTAL;
            break;

        case 2:
            prioItem = Items.GOLDEN_APPLE;
        }

        return prioItem;
    }

    public boolean crystalsWillKill() {
        boolean willKill = false;
        Iterator iterator = AutoTotem.mc.world.loadedEntityList.iterator();

        while (iterator.hasNext()) {
            Entity e = (Entity) iterator.next();

            if (e instanceof EntityEnderCrystal) {
                EntityEnderCrystal crystal = (EntityEnderCrystal) e;

                if (crystal != null && AutoCrystal.calculateDamage(crystal.posX + 0.5D, crystal.posY + 1.0D, crystal.posZ + 0.5D, AutoTotem.mc.player) >= AutoTotem.mc.player.getHealth() + AutoTotem.mc.player.getAbsorptionAmount() && !willKill) {
                    willKill = true;
                }
            }
        }

        return willKill;
    }

    public double getCrystalDMG() {
        boolean willKill = false;
        double dmg = 0.0D;
        Iterator iterator = AutoTotem.mc.world.loadedEntityList.iterator();

        while (iterator.hasNext()) {
            Entity e = (Entity) iterator.next();

            if (e instanceof EntityEnderCrystal) {
                EntityEnderCrystal crystal = (EntityEnderCrystal) e;

                if (crystal != null) {
                    dmg = (double) AutoCrystal.calculateDamage(crystal.posX + 0.5D, crystal.posY + 1.0D, crystal.posZ + 0.5D, AutoTotem.mc.player);
                    if (AutoCrystal.calculateDamage(crystal.posX + 0.5D, crystal.posY + 1.0D, crystal.posZ + 0.5D, AutoTotem.mc.player) >= AutoTotem.mc.player.getHealth() + AutoTotem.mc.player.getAbsorptionAmount() && !willKill) {
                        willKill = true;
                    }
                }
            }
        }

        return dmg;
    }
}
