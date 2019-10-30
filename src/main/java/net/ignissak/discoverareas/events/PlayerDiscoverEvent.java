package net.ignissak.discoverareas.events;

import net.ignissak.discoverareas.objects.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDiscoverEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private Player player;
    private Area area;
    private boolean isCancelled;

    public PlayerDiscoverEvent(Player player, Area area) {
        this.player = player;
        this.area = area;
    }

    public Player getPlayer() {
        return player;
    }

    public Area getArea() {
        return area;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }
}
