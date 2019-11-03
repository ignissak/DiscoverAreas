package net.ignissak.discoverareas.events;

import net.ignissak.discoverareas.discover.DiscoverPlayer;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AreaLeaveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private Area area;
    private DiscoverPlayer player;

    public AreaLeaveEvent(DiscoverPlayer player, Area area) {
        this.player = player;
        this.area = area;
    }

    public DiscoverPlayer getPlayer() {
        return player;
    }

    public Area getArea() {
        return area;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
