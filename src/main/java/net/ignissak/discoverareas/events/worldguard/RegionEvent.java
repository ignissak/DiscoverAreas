package net.ignissak.discoverareas.events.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class RegionEvent extends PlayerEvent {

    private static final HandlerList handlerList;
    private ProtectedRegion region;
    private MovementWay movement;
    public Event parentEvent;

    public RegionEvent(final ProtectedRegion region, final Player player, final MovementWay movement, final Event parent) {
        super(player);
        this.region = region;
        this.movement = movement;
        this.parentEvent = parent;
    }

    public HandlerList getHandlers() {
        return RegionEvent.handlerList;
    }

    public ProtectedRegion getRegion() {
        return this.region;
    }

    public static HandlerList getHandlerList() {
        return RegionEvent.handlerList;
    }

    public MovementWay getMovementWay() {
        return this.movement;
    }

    public Event getParentEvent() {
        return this.parentEvent;
    }

    static {
        handlerList = new HandlerList();
    }

}
