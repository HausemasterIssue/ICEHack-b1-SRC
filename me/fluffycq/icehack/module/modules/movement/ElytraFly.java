package me.fluffycq.icehack.module.modules.movement;

import java.util.ArrayList;
import java.util.function.Predicate;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.EventPlayerTravel;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.MathUtil;
import me.fluffycq.icehack.util.Timer;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.math.MathHelper;

public class ElytraFly extends Module {

    public Setting mode;
    public Setting speed;
    public Setting DownSpeed;
    public Setting GlideSpeed;
    public Setting UpSpeed;
    public Setting Accelerate;
    public Setting vAccelerationTimer;
    public Setting RotationPitch;
    public Setting CancelInWater;
    public Setting CancelAtHeight;
    public Setting InstantFly;
    public Setting EquipElytra;
    public Setting debugmsg;
    public ArrayList elytraMode = new ArrayList();
    private Timer PacketTimer = new Timer();
    private Timer AccelerationTimer = new Timer();
    private Timer AccelerationResetTimer = new Timer();
    private Timer InstantFlyTimer = new Timer();
    private boolean SendMessage = false;
    private int ElytraSlot = -1;
    @EventHandler
    private Listener OnTravel = new Listener((p_Event) -> {
        if (ElytraFly.mc.player != null) {
            this.modInfo = this.mode.getValString().toUpperCase();
            if (this.debugmsg.getValBoolean()) {
                Messages.sendChatMessage("Current Elytra Mode: " + this.mode.getValString());
            }

            if (ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                if (!ICEHack.fevents.moduleManager.getModule("Freecam").isEnabled()) {
                    if (!ElytraFly.mc.player.isElytraFlying()) {
                        if (!ElytraFly.mc.player.onGround && this.InstantFly.getValBoolean()) {
                            if (!this.InstantFlyTimer.passed(1000.0D)) {
                                return;
                            }

                            this.InstantFlyTimer.reset();
                            ElytraFly.mc.player.connection.sendPacket(new CPacketEntityAction(ElytraFly.mc.player, Action.START_FALL_FLYING));
                        }

                    } else {
                        if (this.mode.getValString().equalsIgnoreCase("Packet")) {
                            if (this.debugmsg.getValBoolean()) {
                                Messages.sendChatMessage("Attempting to fly using mode Packet.");
                            }

                            this.HandleNormalModeElytra(p_Event);
                        } else if (this.mode.getValString().equalsIgnoreCase("Superior")) {
                            if (this.debugmsg.getValBoolean()) {
                                Messages.sendChatMessage("Attempting to fly using mode Superior.");
                            }

                            this.HandleImmediateModeElytra(p_Event);
                        } else if (this.mode.getValString().equalsIgnoreCase("Control")) {
                            if (this.debugmsg.getValBoolean()) {
                                Messages.sendChatMessage("Attempting to fly using mode Control.");
                            }

                            this.HandleControlMode(p_Event);
                        }

                    }
                }
            }
        }
    }, new Predicate[0]);

    public ElytraFly() {
        super("ElytraFly", 0, Category.MOVEMENT);
        this.elytraMode.add("Normal");
        this.elytraMode.add("Tarzan");
        this.elytraMode.add("Superior");
        this.elytraMode.add("Packet");
        this.elytraMode.add("Control");
        this.mode = new Setting("Mode", this, "Superior", this.elytraMode);
        this.speed = new Setting("Speed", this, 1.82D, 0.1D, 5.0D, false);
        this.DownSpeed = new Setting("DownSpeed", this, 1.82D, 0.1D, 5.0D, false);
        this.GlideSpeed = new Setting("GlideSpeed", this, 1.0D, 0.0D, 5.0D, false);
        this.UpSpeed = new Setting("UpSpeed", this, 2.0D, 0.0D, 5.0D, false);
        this.Accelerate = new Setting("Accelerate", this, true);
        this.vAccelerationTimer = new Setting("Timer", this, 1000.0D, 0.0D, 10000.0D, true);
        this.RotationPitch = new Setting("Pitch", this, 0.0D, 0.0D, 90.0D, false);
        this.CancelInWater = new Setting("WaterCancel", this, true);
        this.CancelAtHeight = new Setting("HeightCancel", this, 5.0D, 0.0D, 10.0D, true);
        this.InstantFly = new Setting("InstantFly", this, true);
        this.EquipElytra = new Setting("EquipElytra", this, true);
        this.debugmsg = new Setting("DebugMsgs", this, false);
    }

    public void onEnable() {
        this.ElytraSlot = -1;
        if (this.EquipElytra.getValBoolean() && ElytraFly.mc.player != null && ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            for (int l_HasArmorAtChest = 0; l_HasArmorAtChest < 44; ++l_HasArmorAtChest) {
                ItemStack l_Stack = ElytraFly.mc.player.inventory.getStackInSlot(l_HasArmorAtChest);

                if (!l_Stack.isEmpty() && l_Stack.getItem() == Items.ELYTRA) {
                    ItemElytra l_Elytra = (ItemElytra) l_Stack.getItem();

                    this.ElytraSlot = l_HasArmorAtChest;
                    break;
                }
            }

            if (this.ElytraSlot != -1) {
                boolean flag = ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.AIR;

                ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, ElytraFly.mc.player);
                ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, ElytraFly.mc.player);
                if (flag) {
                    ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, ElytraFly.mc.player);
                }
            }
        }

    }

    public void onDisable() {
        if (ElytraFly.mc.player != null) {
            if (this.ElytraSlot != -1) {
                boolean l_HasItem = !ElytraFly.mc.player.inventory.getStackInSlot(this.ElytraSlot).isEmpty() || ElytraFly.mc.player.inventory.getStackInSlot(this.ElytraSlot).getItem() != Items.AIR;

                ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, ElytraFly.mc.player);
                ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, this.ElytraSlot, 0, ClickType.PICKUP, ElytraFly.mc.player);
                if (l_HasItem) {
                    ElytraFly.mc.playerController.windowClick(ElytraFly.mc.player.inventoryContainer.windowId, 6, 0, ClickType.PICKUP, ElytraFly.mc.player);
                }
            }

        }
    }

    public void HandleNormalModeElytra(EventPlayerTravel p_Travel) {
        double l_YHeight = ElytraFly.mc.player.posY;

        if (l_YHeight <= this.CancelAtHeight.getValDouble()) {
            if (!this.SendMessage) {
                Messages.sendChatMessage("&4WARNING, you must scaffold up or use fireworks, as YHeight <= CancelAtHeight!");
                this.SendMessage = true;
            }

        } else {
            boolean l_IsMoveKeyDown = ElytraFly.mc.gameSettings.keyBindForward.isKeyDown() || ElytraFly.mc.gameSettings.keyBindLeft.isKeyDown() || ElytraFly.mc.gameSettings.keyBindRight.isKeyDown() || ElytraFly.mc.gameSettings.keyBindBack.isKeyDown();
            boolean l_CancelInWater = !ElytraFly.mc.player.isInWater() && !ElytraFly.mc.player.isInLava() && this.CancelInWater.getValBoolean();

            if (ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                p_Travel.cancel();
                this.Accelerate();
            } else {
                if (!l_IsMoveKeyDown) {
                    this.AccelerationTimer.resetTimeSkipTo((long) ((int) (-this.vAccelerationTimer.getValDouble())));
                } else if (((double) ElytraFly.mc.player.rotationPitch <= this.RotationPitch.getValDouble() || this.mode.getValString().equalsIgnoreCase("Tarzan")) && l_CancelInWater) {
                    if (this.Accelerate.getValBoolean() && this.AccelerationTimer.passed(this.vAccelerationTimer.getValDouble())) {
                        this.Accelerate();
                        return;
                    }

                    return;
                }

                p_Travel.cancel();
                this.Accelerate();
            }
        }
    }

    public void HandleImmediateModeElytra(EventPlayerTravel p_Travel) {
        p_Travel.cancel();
        boolean moveForward = ElytraFly.mc.gameSettings.keyBindForward.isKeyDown();
        boolean moveBackward = ElytraFly.mc.gameSettings.keyBindBack.isKeyDown();
        boolean moveLeft = ElytraFly.mc.gameSettings.keyBindLeft.isKeyDown();
        boolean moveRight = ElytraFly.mc.gameSettings.keyBindRight.isKeyDown();
        boolean moveUp = ElytraFly.mc.gameSettings.keyBindJump.isKeyDown();
        boolean moveDown = ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown();
        float moveForwardFactor = moveForward ? 1.0F : (float) (moveBackward ? -1 : 0);
        float yawDeg = ElytraFly.mc.player.rotationYaw;

        if (moveLeft && (moveForward || moveBackward)) {
            yawDeg -= 40.0F * moveForwardFactor;
        } else if (moveRight && (moveForward || moveBackward)) {
            yawDeg += 40.0F * moveForwardFactor;
        } else if (moveLeft) {
            yawDeg -= 90.0F;
        } else if (moveRight) {
            yawDeg += 90.0F;
        }

        if (moveBackward) {
            yawDeg -= 180.0F;
        }

        float yaw = (float) Math.toRadians((double) yawDeg);
        double motionAmount = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);

        if (!moveUp && !moveForward && !moveBackward && !moveLeft && !moveRight) {
            ElytraFly.mc.player.motionX = 0.0D;
            ElytraFly.mc.player.motionY = 0.0D;
            ElytraFly.mc.player.motionZ = 0.0D;
        } else if (moveUp && motionAmount > 1.0D) {
            if (ElytraFly.mc.player.motionX == 0.0D && ElytraFly.mc.player.motionZ == 0.0D) {
                ElytraFly.mc.player.motionY = this.UpSpeed.getValDouble();
            } else {
                double calcMotionDiff = motionAmount * 0.008D;

                ElytraFly.mc.player.motionY += calcMotionDiff * 3.2D;
                ElytraFly.mc.player.motionX -= (double) (-MathHelper.sin(yaw)) * calcMotionDiff / 1.0D;
                ElytraFly.mc.player.motionZ -= (double) MathHelper.cos(yaw) * calcMotionDiff / 1.0D;
                ElytraFly.mc.player.motionX *= 0.9900000095367432D;
                ElytraFly.mc.player.motionY *= 0.9800000190734863D;
                ElytraFly.mc.player.motionZ *= 0.9900000095367432D;
            }
        } else {
            ElytraFly.mc.player.motionX = (double) (-MathHelper.sin(yaw)) * 1.7999999523162842D;
            ElytraFly.mc.player.motionY = -(this.GlideSpeed.getValDouble() / 10000.0D);
            ElytraFly.mc.player.motionZ = (double) MathHelper.cos(yaw) * 1.7999999523162842D;
        }

        if (moveDown) {
            ElytraFly.mc.player.motionY = -this.DownSpeed.getValDouble();
        }

        if (!moveUp && moveDown) {
            ;
        }

    }

    public void Accelerate() {
        if (this.AccelerationResetTimer.passed(this.vAccelerationTimer.getValDouble())) {
            this.AccelerationResetTimer.reset();
            this.AccelerationTimer.reset();
            this.SendMessage = false;
        }

        float l_Speed = (float) this.speed.getValDouble();
        double[] dir = MathUtil.directionSpeed((double) l_Speed);

        ElytraFly.mc.player.motionY = -(this.GlideSpeed.getValDouble() / 10000.0D);
        if (ElytraFly.mc.player.movementInput.moveStrafe == 0.0F && ElytraFly.mc.player.movementInput.moveForward == 0.0F) {
            ElytraFly.mc.player.motionX = 0.0D;
            ElytraFly.mc.player.motionZ = 0.0D;
        } else {
            ElytraFly.mc.player.motionX = dir[0];
            ElytraFly.mc.player.motionZ = dir[1];
        }

        if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
            ElytraFly.mc.player.motionY = -this.DownSpeed.getValDouble();
        }

        ElytraFly.mc.player.prevLimbSwingAmount = 0.0F;
        ElytraFly.mc.player.limbSwingAmount = 0.0F;
        ElytraFly.mc.player.limbSwing = 0.0F;
    }

    private void HandleControlMode(EventPlayerTravel p_Event) {
        double[] dir = MathUtil.directionSpeed(this.speed.getValDouble());

        if (ElytraFly.mc.player.movementInput.moveStrafe == 0.0F && ElytraFly.mc.player.movementInput.moveForward == 0.0F) {
            ElytraFly.mc.player.motionX = 0.0D;
            ElytraFly.mc.player.motionZ = 0.0D;
        } else {
            ElytraFly.mc.player.motionX = dir[0];
            ElytraFly.mc.player.motionZ = dir[1];
            ElytraFly.mc.player.motionX -= ElytraFly.mc.player.motionX * (double) (Math.abs(ElytraFly.mc.player.rotationPitch) + 90.0F) / 90.0D - ElytraFly.mc.player.motionX;
            ElytraFly.mc.player.motionZ -= ElytraFly.mc.player.motionZ * (double) (Math.abs(ElytraFly.mc.player.rotationPitch) + 90.0F) / 90.0D - ElytraFly.mc.player.motionZ;
        }

        ElytraFly.mc.player.motionY = -MathUtil.degToRad((double) ElytraFly.mc.player.rotationPitch) * (double) ElytraFly.mc.player.movementInput.moveForward;
        ElytraFly.mc.player.prevLimbSwingAmount = 0.0F;
        ElytraFly.mc.player.limbSwingAmount = 0.0F;
        ElytraFly.mc.player.limbSwing = 0.0F;
        p_Event.cancel();
    }
}
