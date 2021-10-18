package me.fluffycq.icehack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class FaceUtil {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static float getFacePitch(Entity entityIn) {
        double d0 = entityIn.posX - FaceUtil.mc.player.posX;
        double d2 = entityIn.posZ - FaceUtil.mc.player.posZ;
        double d1;

        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase d3 = (EntityLivingBase) entityIn;

            d1 = d3.posY + (double) d3.getEyeHeight() - (FaceUtil.mc.player.posY + (double) FaceUtil.mc.player.getEyeHeight());
        } else {
            d1 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (FaceUtil.mc.player.posY + (double) FaceUtil.mc.player.getEyeHeight());
        }

        double d31 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        float f1 = (float) (-(MathHelper.atan2(d1, d31) * 57.29577951308232D));

        return updateRotation(FaceUtil.mc.player.rotationPitch, f1, Float.MAX_VALUE);
    }

    public static float getFaceYaw(Entity entityIn) {
        double d0 = entityIn.posX - FaceUtil.mc.player.posX;
        double d2 = entityIn.posZ - FaceUtil.mc.player.posZ;
        double d1;

        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase f = (EntityLivingBase) entityIn;

            d1 = f.posY + (double) f.getEyeHeight() - (FaceUtil.mc.player.posY + (double) FaceUtil.mc.player.getEyeHeight());
        } else {
            d1 = (entityIn.getEntityBoundingBox().minY + entityIn.getEntityBoundingBox().maxY) / 2.0D - (FaceUtil.mc.player.posY + (double) FaceUtil.mc.player.getEyeHeight());
        }

        float f1 = (float) (MathHelper.atan2(d2, d0) * 57.29577951308232D) - 90.0F;

        return updateRotation(FaceUtil.mc.player.rotationYaw, f1, Float.MAX_VALUE);
    }

    private static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);

        if (f > maxIncrease) {
            f = maxIncrease;
        }

        if (f < -maxIncrease) {
            f = -maxIncrease;
        }

        return angle + f;
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer player) {
        double dirx = player.posX - px;
        double diry = player.posY - py;
        double dirz = player.posZ - pz;
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
