package me.fluffycq.icehack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockUtil {

    public static final List blackList = Arrays.asList(new Block[] { Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR});
    public static final List shulkerList = Arrays.asList(new Block[] { Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    static Minecraft mc = Minecraft.getMinecraft();
    public static final Vec3d[] TRAP = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D)};
    public static final Vec3d[] BLOCKOVERHEADFACINGPOSX = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D)};
    public static final Vec3d[] BLOCKOVERHEADFACINGPOSZ = new Vec3d[] { new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(0.0D, 3.0D, 0.0D)};
    public static final Vec3d[] BLOCKOVERHEADFACINGNEGX = new Vec3d[] { new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D)};
    public static final Vec3d[] BLOCKOVERHEADFACINGNEGZ = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D)};

    public static int findObiInHotbar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BlockUtil.mc.player.inventory.getStackInSlot(i);

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

    public static List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
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

    public static void findClosestTarget() {
        List playerList = BlockUtil.mc.world.playerEntities;
        EntityPlayer closestTarget = null;
        Iterator iterator = playerList.iterator();

        while (iterator.hasNext()) {
            EntityPlayer target = (EntityPlayer) iterator.next();

            if (target != BlockUtil.mc.player && !Friends.isFriend(target.getName()) && target instanceof EntityLivingBase && target.getHealth() > 0.0F) {
                if (closestTarget == null) {
                    closestTarget = target;
                } else if (BlockUtil.mc.player.getDistance(target) < BlockUtil.mc.player.getDistance(closestTarget)) {
                    closestTarget = target;
                }
            }
        }

    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = pos.offset(side);

            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);

                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }

        return null;
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static boolean placeBlockInRange(BlockPos pos, int lastHotbarSlot, Module m, boolean isSneaking) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();

        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        } else {
            Iterator side = BlockUtil.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).iterator();

            while (side.hasNext()) {
                Entity neighbour = (Entity) side.next();

                if (!(neighbour instanceof EntityItem) && !(neighbour instanceof EntityXPOrb)) {
                    return false;
                }
            }

            EnumFacing side1 = getPlaceableSide(pos);

            if (side1 == null) {
                return false;
            } else {
                BlockPos neighbour1 = pos.offset(side1);
                EnumFacing opposite = side1.getOpposite();

                if (!canBeClicked(neighbour1)) {
                    return false;
                } else {
                    Vec3d hitVec = (new Vec3d(neighbour1)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
                    Block neighbourBlock = BlockUtil.mc.world.getBlockState(neighbour1).getBlock();
                    int obiSlot = findObiInHotbar();

                    if (obiSlot == -1) {
                        m.disable();
                    }

                    if (lastHotbarSlot != obiSlot) {
                        BlockUtil.mc.player.inventory.currentItem = obiSlot;
                    }

                    if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                        BlockUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockUtil.mc.player, Action.START_SNEAKING));
                        isSneaking = true;
                    }

                    faceVectorPacketInstant(hitVec);
                    BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, neighbour1, opposite, hitVec, EnumHand.MAIN_HAND);
                    BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                    BlockUtil.mc.rightClickDelayTimer = 0;
                    return true;
                }
            }
        }
    }

    private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { BlockUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockUtil.mc.player.rotationYaw), BlockUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockUtil.mc.player.rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.player.posX, BlockUtil.mc.player.posY + (double) BlockUtil.mc.player.getEyeHeight(), BlockUtil.mc.player.posZ);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);

        BlockUtil.mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], BlockUtil.mc.player.onGround));
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(BlockUtil.mc.player.posX), Math.floor(BlockUtil.mc.player.posY), Math.floor(BlockUtil.mc.player.posZ));
    }

    public BlockPos getClosestTargetPos(Entity closestTarget) {
        return closestTarget != null ? new BlockPos(Math.floor(closestTarget.posX), Math.floor(closestTarget.posY), Math.floor(closestTarget.posZ)) : null;
    }

    public int getViewYaw() {
        return (int) Math.abs(Math.floor((double) (Minecraft.getMinecraft().player.rotationYaw * 8.0F / 360.0F)));
    }

    public boolean isApplicable(BlockPos blockPos, float range) {
        NonNullList positions = NonNullList.create();

        positions.addAll((Collection) getSphere(getPlayerPos(), range, (int) range, false, true, 0).stream().collect(Collectors.toList()));
        return positions.contains(blockPos);
    }
}
