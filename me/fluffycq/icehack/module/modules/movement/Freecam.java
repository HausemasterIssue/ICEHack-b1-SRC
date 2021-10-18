package me.fluffycq.icehack.module.modules.movement;

import java.util.function.Predicate;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import org.lwjgl.input.Keyboard;

public class Freecam extends Module {

    Setting speed = new Setting("Speed", this, 1.0D, 0.1D, 5.0D, false);
    Setting cancelPackets = new Setting("PacketCancel", this, true);
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    @EventHandler
    private Listener moveListener = new Listener((event) -> {
        Freecam.mc.player.noClip = true;
    }, new Predicate[0]);
    @EventHandler
    private Listener pushListener = new Listener((event) -> {
        event.setCanceled(true);
    }, new Predicate[0]);
    @EventHandler
    private Listener sendListener = new Listener((event) -> {
        if (this.cancelPackets.getValBoolean() && (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput)) {
            event.cancel();
        }

    }, new Predicate[0]);

    public Freecam() {
        super("Freecam", 0, Category.MOVEMENT);
    }

    public void onEnable() {
        if (Freecam.mc.player != null) {
            this.isRidingEntity = Freecam.mc.player.getRidingEntity() != null;
            if (Freecam.mc.player.getRidingEntity() == null) {
                this.posX = Freecam.mc.player.posX;
                this.posY = Freecam.mc.player.posY;
                this.posZ = Freecam.mc.player.posZ;
            } else {
                this.ridingEntity = Freecam.mc.player.getRidingEntity();
                Freecam.mc.player.dismountRidingEntity();
            }

            this.pitch = Freecam.mc.player.rotationPitch;
            this.yaw = Freecam.mc.player.rotationYaw;
            this.clonedPlayer = new EntityOtherPlayerMP(Freecam.mc.world, Freecam.mc.getSession().getProfile());
            this.clonedPlayer.copyLocationAndAnglesFrom(Freecam.mc.player);
            this.clonedPlayer.rotationYawHead = Freecam.mc.player.rotationYawHead;
            Freecam.mc.world.addEntityToWorld(-100, this.clonedPlayer);
            Freecam.mc.player.capabilities.isFlying = true;
            Freecam.mc.player.capabilities.setFlySpeed((float) (this.speed.getValDouble() / 100.0D));
            Freecam.mc.player.noClip = true;
        }

    }

    public void onDisable() {
        EntityPlayerSP localPlayer = Freecam.mc.player;

        if (localPlayer != null) {
            Freecam.mc.player.setPositionAndRotation(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            Freecam.mc.world.removeEntityFromWorld(-100);
            this.clonedPlayer = null;
            this.posX = this.posY = this.posZ = 0.0D;
            this.pitch = this.yaw = 0.0F;
            Freecam.mc.player.capabilities.isFlying = false;
            Freecam.mc.player.capabilities.setFlySpeed(0.05F);
            Freecam.mc.player.noClip = false;
            Freecam.mc.player.motionX = Freecam.mc.player.motionY = Freecam.mc.player.motionZ = 0.0D;
            if (this.isRidingEntity) {
                Freecam.mc.player.startRiding(this.ridingEntity, true);
            }
        }

    }

    public void onUpdate() {
        if (!Freecam.mc.player.onGround) {
            Freecam.mc.player.motionY = -0.2D;
        }

        if (Keyboard.isKeyDown(Freecam.mc.gameSettings.keyBindJump.getKeyCode())) {
            Freecam.mc.player.setPosition(Freecam.mc.player.posX, Freecam.mc.player.posY + this.speed.getValDouble(), Freecam.mc.player.posZ);
        }

        if (Freecam.mc.player.isSneaking()) {
            Freecam.mc.player.setPosition(Freecam.mc.player.posX, Freecam.mc.player.posY - this.speed.getValDouble(), Freecam.mc.player.posZ);
        }

        if (!Keyboard.isKeyDown(Freecam.mc.gameSettings.keyBindForward.getKeyCode()) && !Keyboard.isKeyDown(Freecam.mc.gameSettings.keyBindBack.getKeyCode()) && !Keyboard.isKeyDown(Freecam.mc.gameSettings.keyBindLeft.getKeyCode()) && !Keyboard.isKeyDown(Freecam.mc.gameSettings.keyBindRight.getKeyCode())) {
            Freecam.mc.player.motionX = 0.0D;
            Freecam.mc.player.motionZ = 0.0D;
        } else {
            this.playersSpeed(this.speed.getValDouble());
        }

        Freecam.mc.player.onGround = true;
        Freecam.mc.player.motionY = 0.0D;
        Freecam.mc.player.noClip = true;
        Freecam.mc.player.onGround = false;
        Freecam.mc.player.fallDistance = 0.0F;
    }

    private void playersSpeed(double speed) {
        if (Freecam.mc.player != null) {
            MovementInput movementInput = Freecam.mc.player.movementInput;
            double forward = (double) movementInput.moveForward;
            double strafe = (double) movementInput.moveStrafe;
            float yaw = Freecam.mc.player.rotationYaw;

            if (forward == 0.0D && strafe == 0.0D) {
                Freecam.mc.player.motionX = 0.0D;
                Freecam.mc.player.motionZ = 0.0D;
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (float) (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (float) (forward > 0.0D ? 45 : -45);
                    }

                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else if (forward < 0.0D) {
                        forward = -1.0D;
                    }
                }

                Freecam.mc.player.motionX = forward * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F)));
                Freecam.mc.player.motionZ = forward * speed * Math.sin(Math.toRadians((double) (yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double) (yaw + 90.0F)));
            }
        }

    }
}
