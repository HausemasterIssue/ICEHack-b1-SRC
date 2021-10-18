package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTrap extends Module {

    public Setting blocksper = new Setting("BPT", this, 3.0D, 1.0D, 6.0D, true);
    public Setting delay = new Setting("Delay", this, 2.0D, 1.0D, 16.0D, true);
    public Setting range = new Setting("Range", this, 3.0D, 1.0D, 5.0D, true);
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int delayStep = 0;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;

    public AutoTrap() {
        super("AutoTrap", 0, Category.COMBAT);
    }

    public void onEnable() {
        if (AutoTrap.mc.player == null) {
            this.disable();
        } else {
            this.firstRun = true;
            this.playerHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
            this.lastHotbarSlot = -1;
        }
    }

    public void onDisable() {
        if (AutoTrap.mc.player != null) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                AutoTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
            }

            if (this.isSneaking) {
                AutoTrap.mc.player.connection.sendPacket(new CPacketEntityAction(AutoTrap.mc.player, Action.STOP_SNEAKING));
                this.isSneaking = false;
            }

            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
        }
    }

    public void onUpdate() {
        if (AutoTrap.mc.player != null) {
            if (!this.firstRun) {
                if ((double) this.delayStep < this.delay.getValDouble()) {
                    ++this.delayStep;
                    return;
                }

                this.delayStep = 0;
            }

            this.findClosestTarget();
            if (this.closestTarget == null) {
                if (this.firstRun) {
                    this.firstRun = false;
                }

            } else {
                if (this.firstRun) {
                    this.firstRun = false;
                    this.lastTickTargetName = this.closestTarget.getName();
                } else if (!this.lastTickTargetName.equals(this.closestTarget.getName())) {
                    this.lastTickTargetName = this.closestTarget.getName();
                    this.offsetStep = 0;
                }

                ArrayList placeTargets = new ArrayList();

                Collections.addAll(placeTargets, BlockUtil.TRAP);

                int blocksPlaced;

                for (blocksPlaced = 0; (double) blocksPlaced < this.blocksper.getValDouble(); ++this.offsetStep) {
                    if (this.offsetStep >= placeTargets.size()) {
                        this.offsetStep = 0;
                        break;
                    }

                    BlockPos offsetPos = new BlockPos((Vec3d) placeTargets.get(this.offsetStep));
                    BlockPos targetPos = (new BlockPos(this.closestTarget.getPositionVector())).down().add(offsetPos.x, offsetPos.y, offsetPos.z);

                    if (this.placeBlockInRange(targetPos, this.range.getValDouble())) {
                        ++blocksPlaced;
                    }
                }

                if (blocksPlaced > 0) {
                    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                        AutoTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
                        this.lastHotbarSlot = this.playerHotbarSlot;
                    }

                    if (this.isSneaking) {
                        AutoTrap.mc.player.connection.sendPacket(new CPacketEntityAction(AutoTrap.mc.player, Action.STOP_SNEAKING));
                        this.isSneaking = false;
                    }
                }

                this.modInfo = this.closestTarget != null ? this.closestTarget.getName() : "None";
            }
        }
    }

    private boolean placeBlockInRange(BlockPos pos, double range) {
        Block block = AutoTrap.mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            Iterator side = AutoTrap.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).iterator();

            while (side.hasNext()) {
                Entity neighbour = (Entity) side.next();

                if (!(neighbour instanceof EntityItem) && !(neighbour instanceof EntityXPOrb)) {
                    return false;
                }
            }

            EnumFacing side1 = BlockUtil.getPlaceableSide(pos);

            if (side1 == null) {
                return false;
            } else {
                BlockPos neighbour1 = pos.offset(side1);
                EnumFacing opposite = side1.getOpposite();

                if (!BlockUtil.canBeClicked(neighbour1)) {
                    return false;
                } else {
                    Vec3d hitVec = (new Vec3d(neighbour1)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
                    Block neighbourBlock = AutoTrap.mc.world.getBlockState(neighbour1).getBlock();

                    if (AutoTrap.mc.player.getPositionVector().distanceTo(hitVec) > range) {
                        return false;
                    } else {
                        int obiSlot = this.findObiInHotbar();

                        if (obiSlot == -1) {
                            this.disable();
                        }

                        if (this.lastHotbarSlot != obiSlot) {
                            AutoTrap.mc.player.inventory.currentItem = obiSlot;
                            this.lastHotbarSlot = obiSlot;
                        }

                        if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                            AutoTrap.mc.player.connection.sendPacket(new CPacketEntityAction(AutoTrap.mc.player, Action.START_SNEAKING));
                            this.isSneaking = true;
                        }

                        BlockUtil.faceVectorPacketInstant(hitVec);
                        AutoTrap.mc.playerController.processRightClickBlock(AutoTrap.mc.player, AutoTrap.mc.world, neighbour1, opposite, hitVec, EnumHand.MAIN_HAND);
                        AutoTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
                        AutoTrap.mc.rightClickDelayTimer = 4;
                        return true;
                    }
                }
            }
        }
    }

    private int findObiInHotbar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = AutoTrap.mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();

                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }

    private void findClosestTarget() {
        List playerList = AutoTrap.mc.world.playerEntities;

        this.closestTarget = null;
        Iterator iterator = playerList.iterator();

        while (iterator.hasNext()) {
            EntityPlayer target = (EntityPlayer) iterator.next();

            if (target != AutoTrap.mc.player && !Friends.isFriend(target.getName()) && target instanceof EntityLivingBase && target.getHealth() > 0.0F) {
                if (this.closestTarget == null) {
                    this.closestTarget = target;
                } else if (AutoTrap.mc.player.getDistance(target) < AutoTrap.mc.player.getDistance(this.closestTarget)) {
                    this.closestTarget = target;
                }
            }
        }

    }
}
