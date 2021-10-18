package me.fluffycq.icehack.module.modules.combat;

import java.util.Iterator;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

public class TickAura extends Module {

    public Setting ttkswitch = new Setting("32kSwitch", this, true);
    public Setting range = new Setting("Range", this, 4.5D, 1.0D, 7.0D, false);
    public Setting delay = new Setting("TickDelay", this, 3.0D, 1.0D, 60.0D, true);
    public Setting onlyttk = new Setting("Only32k", this, true);
    boolean foundsword = false;
    int Dticks;

    public TickAura() {
        super("TickAura", 0, Category.COMBAT);
    }

    public void onUpdate() {
        if (!TickAura.mc.player.isDead && TickAura.mc.world != null) {
            ++this.Dticks;
            if (this.ttkswitch.getValBoolean()) {
                boolean foundair = false;
                int entity = -1;

                int i;
                ItemStack itemStack;

                for (i = 0; i < 9; ++i) {
                    itemStack = (ItemStack) TickAura.mc.player.inventory.mainInventory.get(i);
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, itemStack) >= 32767) {
                        entity = i;
                        this.foundsword = true;
                    }

                    if (!this.foundsword) {
                        entity = -1;
                        this.foundsword = false;
                    }
                }

                if (entity != -1 && TickAura.mc.player.inventory.currentItem != entity) {
                    TickAura.mc.player.connection.sendPacket(new CPacketHeldItemChange(entity));
                    TickAura.mc.player.inventory.currentItem = entity;
                    TickAura.mc.playerController.updateController();
                }

                if (entity == -1 && TickAura.mc.player.openContainer != null && TickAura.mc.player.openContainer instanceof ContainerHopper && TickAura.mc.player.openContainer.inventorySlots != null && !TickAura.mc.player.openContainer.inventorySlots.isEmpty()) {
                    for (i = 0; i < 5; ++i) {
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, ((Slot) TickAura.mc.player.openContainer.inventorySlots.get(0)).inventory.getStackInSlot(i)) >= 32767) {
                            entity = i;
                            break;
                        }
                    }

                    if (entity == -1) {
                        return;
                    }

                    if (entity != -1) {
                        for (i = 0; i < 9; ++i) {
                            itemStack = (ItemStack) TickAura.mc.player.inventory.mainInventory.get(i);
                            if (itemStack.getItem() instanceof ItemAir) {
                                if (TickAura.mc.player.inventory.currentItem != i) {
                                    TickAura.mc.player.connection.sendPacket(new CPacketHeldItemChange(i));
                                    TickAura.mc.player.inventory.currentItem = i;
                                    TickAura.mc.playerController.updateController();
                                }

                                foundair = true;
                                break;
                            }
                        }
                    }

                    if (foundair || this.checkEnchants()) {
                        TickAura.mc.playerController.windowClick(TickAura.mc.player.openContainer.windowId, entity, TickAura.mc.player.inventory.currentItem, ClickType.SWAP, TickAura.mc.player);
                    }
                }
            }

            Iterator iterator = TickAura.mc.world.loadedEntityList.iterator();

            Entity entity;

            do {
                if (!iterator.hasNext()) {
                    return;
                }

                entity = (Entity) iterator.next();
            } while (!(entity instanceof EntityLivingBase) || entity == TickAura.mc.player || (double) TickAura.mc.player.getDistance(entity) > this.range.getValDouble() || ((EntityLivingBase) entity).getHealth() <= 0.0F || !isTTK(TickAura.mc.player.inventory.getCurrentItem()) && this.onlyttk.getValBoolean() || !(entity instanceof EntityPlayer) || Friends.isFriend(entity.getName()) || this.Dticks < (int) this.delay.getValDouble());

            TickAura.mc.playerController.attackEntity(TickAura.mc.player, entity);
            TickAura.mc.player.swingArm(EnumHand.MAIN_HAND);
            this.Dticks = 0;
        }
    }

    public void onEnable() {
        this.Dticks = 0;
    }

    public void onDisable() {
        this.Dticks = 0;
    }

    public boolean checkEnchants() {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, TickAura.mc.player.inventory.getCurrentItem()) == Short.valueOf((short) 5).shortValue() || TickAura.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe;
    }

    public static boolean isTTK(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        } else if (itemStack.getTagCompound() == null) {
            return false;
        } else if (itemStack.getEnchantmentTagList().getTagType() == 0) {
            return false;
        } else {
            NBTTagList list = (NBTTagList) itemStack.getTagCompound().getTag("ench");

            for (int i = 0; i < list.tagCount(); ++i) {
                NBTTagCompound compoundTag = list.getCompoundTagAt(i);

                if (compoundTag.getInteger("id") == 16) {
                    if (compoundTag.getInteger("lvl") >= 16) {
                        return true;
                    }
                    break;
                }
            }

            return false;
        }
    }
}
