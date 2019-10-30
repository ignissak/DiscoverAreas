package net.ignissak.discoverareas.events.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class RegionEnteredEvent extends RegionEvent{

    public RegionEnteredEvent(final ProtectedRegion region, final Player player, final MovementWay movement, final Event parent) {
        super(region, player, movement, parent);
    }

}
