package me.fluffycq.icehack.events;

import net.minecraft.entity.Entity;

public class PopTotemEvent extends ICEEvent {

    private Entity entity;

    public PopTotemEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
