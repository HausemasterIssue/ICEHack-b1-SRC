package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SelfTrap extends Module {

    public Setting blocksper = new Setting("BPT", this, 3.0D, 1.0D, 6.0D, true);
    public Setting delay = new Setting("Delay", this, 2.0D, 1.0D, 16.0D, true);
    public Setting smart = new Setting("Smart", this, true);
    public Setting range = new Setting("Range", this, 3.0D, 1.0D, 5.0D, true);
    public Setting autocenter = new Setting("AutoCenter", this, true);
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int delayStep = 0;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;
    private EntityPlayer closestTarget;

    public SelfTrap() {
        super("SelfTrap", 0, Category.COMBAT);
    }

    public void onEnable() {
        if (SelfTrap.mc.player == null) {
            this.disable();
        } else {
            double y = (double) SelfTrap.mc.player.getPosition().getY();
            double x = (double) SelfTrap.mc.player.getPosition().getX();
            double z = (double) SelfTrap.mc.player.getPosition().getZ();
            Vec3d plusPlus = new Vec3d(x + 0.5D, y, z + 0.5D);
            Vec3d plusMinus = new Vec3d(x + 0.5D, y, z - 0.5D);
            Vec3d minusMinus = new Vec3d(x - 0.5D, y, z - 0.5D);
            Vec3d minusPlus = new Vec3d(x - 0.5D, y, z + 0.5D);

            if (this.autocenter.getValBoolean()) {
                if (this.getDst(plusPlus) < this.getDst(plusMinus) && this.getDst(plusPlus) < this.getDst(minusMinus) && this.getDst(plusPlus) < this.getDst(minusPlus)) {
                    x = (double) SelfTrap.mc.player.getPosition().getX() + 0.5D;
                    z = (double) SelfTrap.mc.player.getPosition().getZ() + 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(plusMinus) < this.getDst(plusPlus) && this.getDst(plusMinus) < this.getDst(minusMinus) && this.getDst(plusMinus) < this.getDst(minusPlus)) {
                    x = (double) SelfTrap.mc.player.getPosition().getX() + 0.5D;
                    z = (double) SelfTrap.mc.player.getPosition().getZ() - 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(minusMinus) < this.getDst(plusPlus) && this.getDst(minusMinus) < this.getDst(plusMinus) && this.getDst(minusMinus) < this.getDst(minusPlus)) {
                    x = (double) SelfTrap.mc.player.getPosition().getX() - 0.5D;
                    z = (double) SelfTrap.mc.player.getPosition().getZ() - 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(minusPlus) < this.getDst(plusPlus) && this.getDst(minusPlus) < this.getDst(plusMinus) && this.getDst(minusPlus) < this.getDst(minusMinus)) {
                    x = (double) SelfTrap.mc.player.getPosition().getX() - 0.5D;
                    z = (double) SelfTrap.mc.player.getPosition().getZ() + 0.5D;
                    this.centerPlayer(x, y, z);
                }
            }

            this.firstRun = true;
            this.playerHotbarSlot = SelfTrap.mc.player.inventory.currentItem;
            this.lastHotbarSlot = -1;
        }
    }

    public void onDisable() {
        this.closestTarget = null;
        if (SelfTrap.mc.player != null) {
            if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                SelfTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
            }

            if (this.isSneaking) {
                SelfTrap.mc.player.connection.sendPacket(new CPacketEntityAction(SelfTrap.mc.player, Action.STOP_SNEAKING));
                this.isSneaking = false;
            }

            this.playerHotbarSlot = -1;
            this.lastHotbarSlot = -1;
        }
    }

    public void onUpdate() {
        if (this.smart.getValBoolean()) {
            this.findClosestTarget();
        }

        if (SelfTrap.mc.player != null) {
            if (!this.firstRun) {
                if (this.delayStep < (int) this.delay.getValDouble()) {
                    ++this.delayStep;
                    return;
                }

                this.delayStep = 0;
            }

            ArrayList placeTargets = new ArrayList();

            if (!this.smart.getValBoolean()) {
                Collections.addAll(placeTargets, BlockUtil.TRAP);
            } else if (this.getViewYaw() <= 315 && this.getViewYaw() >= 225) {
                Collections.addAll(placeTargets, BlockUtil.BLOCKOVERHEADFACINGNEGX);
            } else if ((this.getViewYaw() >= 45 || this.getViewYaw() <= 0) && (this.getViewYaw() <= 315 || this.getViewYaw() >= 360)) {
                if (this.getViewYaw() <= 135 && this.getViewYaw() >= 45) {
                    Collections.addAll(placeTargets, BlockUtil.BLOCKOVERHEADFACINGPOSX);
                } else if (this.getViewYaw() < 225 && this.getViewYaw() > 135) {
                    Collections.addAll(placeTargets, BlockUtil.BLOCKOVERHEADFACINGNEGZ);
                }
            } else {
                Collections.addAll(placeTargets, BlockUtil.BLOCKOVERHEADFACINGPOSZ);
            }

            int blocksPlaced;
            BlockPos blockOverHead;

            for (blocksPlaced = 0; blocksPlaced < (int) this.blocksper.getValDouble(); ++this.offsetStep) {
                if (this.offsetStep >= placeTargets.size()) {
                    this.offsetStep = 0;
                    break;
                }

                BlockPos overHead = new BlockPos((Vec3d) placeTargets.get(this.offsetStep));

                blockOverHead = (new BlockPos(SelfTrap.mc.player.getPositionVector())).down().add(overHead.x, overHead.y, overHead.z);
                if (this.closestTarget != null && this.smart.getValBoolean()) {
                    if (this.isInRange(this.getClosestTargetPos()) && this.placeBlockInRange(blockOverHead)) {
                        ++blocksPlaced;
                    }
                } else if (!this.smart.getValBoolean() && this.placeBlockInRange(blockOverHead)) {
                    ++blocksPlaced;
                }
            }

            if (blocksPlaced > 0) {
                if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
                    SelfTrap.mc.player.inventory.currentItem = this.playerHotbarSlot;
                    this.lastHotbarSlot = this.playerHotbarSlot;
                }

                if (this.isSneaking) {
                    SelfTrap.mc.player.connection.sendPacket(new CPacketEntityAction(SelfTrap.mc.player, Action.STOP_SNEAKING));
                    this.isSneaking = false;
                }
            }

            Vec3d vec3d = new Vec3d(0.0D, 3.0D, 0.0D);

            blockOverHead = (new BlockPos(SelfTrap.mc.player.getPositionVector())).down().add(vec3d.x, vec3d.y, vec3d.z);
            Block block2 = SelfTrap.mc.world.getBlockState(blockOverHead).getBlock();
        }
    }

    private boolean placeBlockInRange(BlockPos pos) {
        Block block = SelfTrap.mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            Iterator side = SelfTrap.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).iterator();

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
                    Block neighbourBlock = SelfTrap.mc.world.getBlockState(neighbour1).getBlock();
                    int obiSlot = this.findObiInHotbar();

                    if (obiSlot == -1) {
                        this.disable();
                    }

                    if (this.lastHotbarSlot != obiSlot) {
                        SelfTrap.mc.player.inventory.currentItem = obiSlot;
                        this.lastHotbarSlot = obiSlot;
                    }

                    if (!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                        SelfTrap.mc.player.connection.sendPacket(new CPacketEntityAction(SelfTrap.mc.player, Action.START_SNEAKING));
                        this.isSneaking = true;
                    }

                    BlockUtil.faceVectorPacketInstant(hitVec);
                    SelfTrap.mc.playerController.processRightClickBlock(SelfTrap.mc.player, SelfTrap.mc.world, neighbour1, opposite, hitVec, EnumHand.MAIN_HAND);
                    SelfTrap.mc.player.swingArm(EnumHand.MAIN_HAND);
                    SelfTrap.mc.rightClickDelayTimer = 0;
                    return true;
                }
            }
        }
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(SelfTrap.mc.player.posX), Math.floor(SelfTrap.mc.player.posY), Math.floor(SelfTrap.mc.player.posZ));
    }

    public BlockPos getClosestTargetPos() {
        return this.closestTarget != null ? new BlockPos(Math.floor(this.closestTarget.posX), Math.floor(this.closestTarget.posY), Math.floor(this.closestTarget.posZ)) : null;
    }

    public int getViewYaw() {
        return (int) Math.abs(Math.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F)));
    }

    private void findClosestTarget() {
        List playerList = SelfTrap.mc.world.playerEntities;

        this.closestTarget = null;
        Iterator iterator = playerList.iterator();

        while (iterator.hasNext()) {
            EntityPlayer target = (EntityPlayer) iterator.next();

            if (target != SelfTrap.mc.player && !Friends.isFriend(target.getName()) && target instanceof EntityLivingBase && target.getHealth() > 0.0F) {
                if (this.closestTarget == null) {
                    this.closestTarget = target;
                } else if (SelfTrap.mc.player.getDistance(target) < SelfTrap.mc.player.getDistance(this.closestTarget)) {
                    this.closestTarget = target;
                }
            }
        }

    }

    private boolean isInRange(BlockPos blockPos) {
        NonNullList positions = NonNullList.create();

        positions.addAll((Collection) this.getSphere(getPlayerPos(), (float) this.range.getValDouble(), (int) this.range.getValDouble(), false, true, 0).stream().collect(Collectors.toList()));
        return positions.contains(blockPos);
    }

    public List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList circleblocks = new ArrayList();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                for (int y = sphere ? cy - (int) r : cy; (float) y < (sphere ? (float) cy + r : (float) (cy + h)); ++y) {
                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);

                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    private int findObiInHotbar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = SelfTrap.mc.player.inventory.getStackInSlot(i);

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

    private void centerPlayer(double x, double y, double z) {
        SelfTrap.mc.player.connection.sendPacket(new Position(x, y, z, true));
        SelfTrap.mc.player.setPosition(x, y, z);
    }

    private double getDst(Vec3d vec) {
        return SelfTrap.mc.player.getPositionVector().distanceTo(vec);
    }
}
