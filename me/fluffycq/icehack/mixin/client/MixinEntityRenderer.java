package me.fluffycq.icehack.mixin.client;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import me.fluffycq.icehack.module.modules.render.NoEntityBlock;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = { EntityRenderer.class},
    priority = Integer.MAX_VALUE
)
public class MixinEntityRenderer {

    @Redirect(
        method = { "getMouseOver"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"
            )
    )
    public List getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        return (List) (NoEntityBlock.doBlock() ? new ArrayList() : worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate));
    }
}
