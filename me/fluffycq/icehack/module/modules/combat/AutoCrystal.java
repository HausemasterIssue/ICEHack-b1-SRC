package me.fluffycq.icehack.module.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ICERenderer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class AutoCrystal extends Module {

    public Setting place = new Setting("Place", this, true);
    public Setting raytrace = new Setting("RayTrace", this, false);
    public Setting autoSwitch = new Setting("AutoSwitch", this, true);
    public Setting antiStuck = new Setting("AntiStuck", this, true);
    public Setting multiPlace = new Setting("MultiPlace", this, true);
    public Setting onlydamage = new Setting("OnlyDMG", this, true);
    public Setting onlydmgval = new Setting("MinBreakDMG", this, 4.0D, 1.0D, 20.0D, true);
    private Setting alert = new Setting("Msgs", this, true);
    private Setting antiSui = new Setting("AntiSui", this, true);
    private Setting attackSpeed = new Setting("BreakSpeed", this, 16.0D, 1.0D, 20.0D, true);
    private Setting placeDelay = new Setting("PlaceDelay", this, 3.0D, 1.0D, 40.0D, true);
    private Setting enemyRange = new Setting("Range", this, 8.0D, 1.0D, 13.0D, true);
    private Setting minDamage = new Setting("MinDmg", this, 6.0D, 1.0D, 16.0D, true);
    private Setting facePlace;
    private Setting multiPlaceSpeed = new Setting("MultiSpeed", this, 3.0D, 1.0D, 10.0D, true);
    private Setting placeRange = new Setting("PlaceRange", this, 6.0D, 1.0D, 6.0D, true);
    private Setting breakRange = new Setting("BreakRange", this, 6.0D, 1.0D, 6.0D, true);
    private Setting opacity = new Setting("Opacity", this, 35.0D, 35.0D, 255.0D, true);
    private Setting rendermode;
    private Setting width;
    ArrayList rendermodes = new ArrayList();
    private BlockPos render;
    public boolean isActive = false;
    private Entity renderEnt;
    private long placeSystemTime;
    private long breakSystemTime;
    private long chatSystemTime;
    private long multiPlaceSystemTime;
    private long antiStuckSystemTime;
    private static boolean togglePitch = false;
    private boolean switchCooldown;
    private int newSlot;
    private int placements;
    private static boolean isSpoofingAngles;
    private static double yaw;
    private static double pitch;
    private Entity target;
    @EventHandler
    private Listener packetListener;

    public AutoCrystal() {
        super("AutoCrystal", 0, Category.COMBAT);
        this.rendermodes.add("Full");
        this.rendermodes.add("Up");
        this.rendermodes.add("Outline");
        this.rendermode = new Setting("Mode", this, "Full", this.rendermodes);
        this.width = new Setting("Width", this, 1.0D, 1.0D, 15.0D, false);
        this.placeSystemTime = -1L;
        this.breakSystemTime = -1L;
        this.chatSystemTime = -1L;
        this.multiPlaceSystemTime = -1L;
        this.antiStuckSystemTime = -1L;
        this.switchCooldown = false;
        this.placements = 0;
        Packet[] packet = new Packet[1];

        this.packetListener = new Listener((event) -> {
            packet[0] = event.getPacket();
            if (packet[0] instanceof CPacketPlayer && AutoCrystal.isSpoofingAngles) {
                ((CPacketPlayer) packet[0]).yaw = (float) AutoCrystal.yaw;
                ((CPacketPlayer) packet[0]).pitch = (float) AutoCrystal.pitch;
            }

        }, new Predicate[0]);
    }

    public void onUpdate() {
        EntityEnderCrystal crystal = (EntityEnderCrystal) AutoCrystal.mc.world.loadedEntityList.stream().filter((entity) -> {
            return entity instanceof EntityEnderCrystal;
        }).map((entity) -> {
            return entity;
        }).min(Comparator.comparing((c) -> {
            return Float.valueOf(AutoCrystal.mc.player.getDistance(c));
        })).orElse((Object) null);

        if (crystal != null && (double) AutoCrystal.mc.player.getDistance(crystal) <= this.breakRange.getValDouble() && this.willDamage(crystal)) {
            if ((double) (System.nanoTime() / 1000000L - this.breakSystemTime) >= 420.0D - this.attackSpeed.getValDouble() * 20.0D) {
                this.lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, AutoCrystal.mc.player);
                AutoCrystal.mc.playerController.attackEntity(AutoCrystal.mc.player, crystal);
                AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
                this.breakSystemTime = System.nanoTime() / 1000000L;
            }

            if (this.multiPlace.getValBoolean()) {
                if ((double) (System.nanoTime() / 1000000L - this.multiPlaceSystemTime) >= 20.0D * this.multiPlaceSpeed.getValDouble() && (double) (System.nanoTime() / 1000000L - this.antiStuckSystemTime) <= 400.0D + (400.0D - this.attackSpeed.getValDouble() * 20.0D)) {
                    this.multiPlaceSystemTime = System.nanoTime() / 1000000L;
                    return;
                }
            } else if ((double) (System.nanoTime() / 1000000L - this.antiStuckSystemTime) <= 400.0D + (400.0D - this.attackSpeed.getValDouble() * 20.0D)) {
                return;
            }
        } else {
            resetRotation();
        }

        int crystalSlot = AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? AutoCrystal.mc.player.inventory.currentItem : -1;

        if (crystalSlot == -1) {
            for (int offhand = 0; offhand < 9; ++offhand) {
                if (AutoCrystal.mc.player.inventory.getStackInSlot(offhand).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = offhand;
                    break;
                }
            }
        }

        boolean flag = false;

        if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            flag = true;
        } else if (crystalSlot == -1) {
            return;
        }

        Entity ent = null;
        Object lastTarget = null;
        BlockPos finalPos = null;
        List blocks = this.findCrystalBlocks();
        ArrayList entities = new ArrayList();

        entities.addAll((Collection) AutoCrystal.mc.world.playerEntities.stream().filter((entityPlayer) -> {
            return !Friends.isFriend(entityPlayer.getName());
        }).collect(Collectors.toList()));
        double damage = 0.5D;
        Iterator player2 = entities.iterator();

        while (player2.hasNext()) {
            Entity f = (Entity) player2.next();

            if (f != AutoCrystal.mc.player && ((EntityLivingBase) f).getHealth() > 0.0F && AutoCrystal.mc.player.getDistanceSq(f) <= this.enemyRange.getValDouble() * this.enemyRange.getValDouble()) {
                Iterator iterator = blocks.iterator();

                while (iterator.hasNext()) {
                    BlockPos blockPos = (BlockPos) iterator.next();

                    if (canBlockBeSeen(blockPos) || AutoCrystal.mc.player.getDistanceSq(blockPos) <= 25.0D || !this.raytrace.getValBoolean()) {
                        double b = f.getDistanceSq(blockPos);

                        if (b <= 56.2D) {
                            double d = (double) calculateDamage((double) blockPos.x + 0.5D, (double) (blockPos.y + 1), (double) blockPos.z + 0.5D, f);

                            if ((d >= this.minDamage.getValDouble() || (double) (((EntityLivingBase) f).getHealth() + ((EntityLivingBase) f).getAbsorptionAmount()) <= 8.0D) && d > damage) {
                                double self = (double) calculateDamage((double) blockPos.x + 0.5D, (double) (blockPos.y + 1), (double) blockPos.z + 0.5D, AutoCrystal.mc.player);

                                if (!this.antiSui.getValBoolean() || (double) (AutoCrystal.mc.player.getHealth() + AutoCrystal.mc.player.getAbsorptionAmount()) - self > 7.0D && self <= d) {
                                    this.target = f;
                                    damage = d;
                                    finalPos = blockPos;
                                    ent = f;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (damage == 0.5D) {
            this.render = null;
            this.renderEnt = null;
            resetRotation();
        } else {
            this.render = finalPos;
            this.renderEnt = ent;
            if (this.place.getValBoolean()) {
                if (!flag && AutoCrystal.mc.player.inventory.currentItem != crystalSlot) {
                    if (this.autoSwitch.getValBoolean()) {
                        AutoCrystal.mc.player.inventory.currentItem = crystalSlot;
                        resetRotation();
                        this.switchCooldown = true;
                    }

                    return;
                }

                this.lookAtPacket((double) finalPos.x + 0.5D, (double) finalPos.y - 0.5D, (double) finalPos.z + 0.5D, AutoCrystal.mc.player);
                RayTraceResult raytraceresult = AutoCrystal.mc.world.rayTraceBlocks(new Vec3d(AutoCrystal.mc.player.posX, AutoCrystal.mc.player.posY + (double) AutoCrystal.mc.player.getEyeHeight(), AutoCrystal.mc.player.posZ), new Vec3d((double) finalPos.x + 0.5D, (double) finalPos.y - 0.5D, (double) finalPos.z + 0.5D));
                EnumFacing enumfacing;

                if (raytraceresult != null && raytraceresult.sideHit != null) {
                    enumfacing = raytraceresult.sideHit;
                } else {
                    enumfacing = EnumFacing.UP;
                }

                if (this.switchCooldown) {
                    this.switchCooldown = false;
                    return;
                }

                if ((double) (System.nanoTime() / 1000000L - this.placeSystemTime) >= this.placeDelay.getValDouble() * 2.0D) {
                    AutoCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(finalPos, enumfacing, flag ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                    ++this.placements;
                    this.antiStuckSystemTime = System.nanoTime() / 1000000L;
                    this.placeSystemTime = System.nanoTime() / 1000000L;
                }
            }

            if (AutoCrystal.isSpoofingAngles) {
                EntityPlayerSP entityplayersp;

                if (AutoCrystal.togglePitch) {
                    entityplayersp = AutoCrystal.mc.player;
                    entityplayersp.rotationPitch += 4.0E-4F;
                    AutoCrystal.togglePitch = false;
                } else {
                    entityplayersp = AutoCrystal.mc.player;
                    entityplayersp.rotationPitch -= 4.0E-4F;
                    AutoCrystal.togglePitch = true;
                }
            }

        }
    }

    public void onWorld(RenderEvent event) {
        if (this.render != null) {
            float[] hue = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
            int rgb = Color.HSBtoRGB(hue[0], 1.0F, 1.0F);
            int r = rgb >> 16 & 255;
            int g = rgb >> 8 & 255;
            int b = rgb & 255;

            if (this.rendermode.getValString().equalsIgnoreCase("Up")) {
                ICERenderer.prepare(7);
                ICERenderer.drawBoxOpacity(this.render, rgb, (int) this.opacity.getValDouble(), 2);
                ICERenderer.release();
            } else if (this.rendermode.getValString().equalsIgnoreCase("Full")) {
                ICERenderer.prepare(7);
                ICERenderer.drawBox(this.render, r, g, b, 77, 63);
                ICERenderer.release();
                ICERenderer.prepare(7);
                ICERenderer.drawBoundingBoxBlockPos(this.render, 1.0F, r, g, b, (int) this.opacity.getValDouble());
                ICERenderer.release();
            } else if (this.rendermode.getValString().equalsIgnoreCase("Outline")) {
                ICERenderer.prepare(7);
                ICERenderer.drawBoundingBoxBlockPos(this.render, (float) this.width.getValDouble(), r, g, b, (int) this.opacity.getValDouble());
                ICERenderer.release();
            }
        }

    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);

        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        return (AutoCrystal.mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || AutoCrystal.mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN) && AutoCrystal.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && AutoCrystal.mc.world.getBlockState(boost2).getBlock() == Blocks.AIR && AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(AutoCrystal.mc.player.posX), Math.floor(AutoCrystal.mc.player.posY), Math.floor(AutoCrystal.mc.player.posZ));
    }

    private List findCrystalBlocks() {
        NonNullList positions = NonNullList.create();

        positions.addAll((Collection) getSphere(getPlayerPos(), (float) this.placeRange.getValDouble(), (int) this.placeRange.getValDouble(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    public boolean willDamage(EntityEnderCrystal crystal) {
        boolean willDamage = false;

        if (this.onlydamage.getValBoolean()) {
            ArrayList entities = new ArrayList();

            entities.addAll((Collection) AutoCrystal.mc.world.playerEntities.stream().filter((entityPlayer) -> {
                return !Friends.isFriend(entityPlayer.getName());
            }).collect(Collectors.toList()));
            Iterator iterator = entities.iterator();

            while (iterator.hasNext()) {
                Entity e = (Entity) iterator.next();

                if (e != AutoCrystal.mc.player && e instanceof EntityLivingBase && ((EntityLivingBase) e).getHealth() > 0.0F && AutoCrystal.mc.player.getDistanceSq(e) <= this.enemyRange.getValDouble() * this.enemyRange.getValDouble() && (double) calculateDamage(crystal.posX, crystal.posY, crystal.posZ, e) >= this.onlydmgval.getValDouble() && !willDamage) {
                    willDamage = true;
                    break;
                }
            }
        } else {
            willDamage = true;
        }

        return willDamage;
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

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D));
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finald = (double) getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(AutoCrystal.mc.world, (Entity) null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);

            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp((float) k, 0.0F, 20.0F);

            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage -= damage / 4.0F;
            }

            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private static float getDamageMultiplied(float damage) {
        int diff = AutoCrystal.mc.world.getDifficulty().getId();

        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static boolean canBlockBeSeen(BlockPos blockPos) {
        return AutoCrystal.mc.world.rayTraceBlocks(new Vec3d(AutoCrystal.mc.player.posX, AutoCrystal.mc.player.posY + (double) AutoCrystal.mc.player.getEyeHeight(), AutoCrystal.mc.player.posZ), new Vec3d((double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ()), false, true, false) == null;
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        AutoCrystal.yaw = (double) yaw1;
        AutoCrystal.pitch = (double) pitch1;
        AutoCrystal.isSpoofingAngles = true;
    }

    private static void resetRotation() {
        if (AutoCrystal.isSpoofingAngles) {
            AutoCrystal.yaw = (double) AutoCrystal.mc.player.rotationYaw;
            AutoCrystal.pitch = (double) AutoCrystal.mc.player.rotationPitch;
            AutoCrystal.isSpoofingAngles = false;
        }

    }

    public void onDisable() {
        this.render = null;
        resetRotation();
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0D / 3.141592653589793D;
        yaw = yaw * 180.0D / 3.141592653589793D;
        yaw += 90.0D;
        return new double[] { yaw, pitch};
    }
}
