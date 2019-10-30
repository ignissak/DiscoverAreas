package net.ignissak.discoverareas.events;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.events.worldguard.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.*;


public class WGRegionEventsListener implements Listener {

    private DiscoverMain plugin;
    private Map<Player, Set<ProtectedRegion>> playerRegions;
    private final RegionContainer container;
    private static boolean initialized;

    public static void initialize() {
        if (!WGRegionEventsListener.initialized) {
            new WGRegionEventsListener(DiscoverMain.getInstance());
            return;
        }
        throw new UnsupportedOperationException("You are not allowed to instantiate this class!");
    }

    private WGRegionEventsListener(final DiscoverMain plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        this.playerRegions = Maps.newHashMap();
        WGRegionEventsListener.initialized = true;
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent e) {
        final Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (final ProtectedRegion region : regions) {
                final RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                final RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        final Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (final ProtectedRegion region : regions) {
                final RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                final RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                this.plugin.getServer().getPluginManager().callEvent(leaveEvent);
                this.plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent e) {
        e.setCancelled(this.updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerChangeWorlds(final PlayerChangedWorldEvent event) {
        this.clearRegions(event.getPlayer(), MovementWay.WORLD_CHANGE, event);
        this.updateRegions(event.getPlayer(), MovementWay.WORLD_CHANGE, event.getPlayer().getLocation(), event);
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        final PlayerTeleportEvent.TeleportCause cause = event.getCause();
        MovementWay movementType = MovementWay.TELEPORT;
        if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL || cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            this.clearRegions(event.getPlayer(), MovementWay.WORLD_CHANGE, event);
            movementType = MovementWay.WORLD_CHANGE;
        }
        this.updateRegions(event.getPlayer(), movementType, event.getTo(), event);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        this.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }

    @EventHandler
    public void onVehicleMove(final VehicleMoveEvent event) {
        if (event.getVehicle().getPassengers() == null) {
            return;
        }
        final List<Entity> passengers = event.getVehicle().getPassengers();
        passengers.stream().filter(e -> e instanceof Player).forEach(player -> this.updateRegions((Player)player, MovementWay.RIDE, player.getLocation(), (Event)event));
    }

    private void clearRegions(final Player player, final MovementWay movementway, final PlayerEvent event) {
        if (!this.playerRegions.containsKey(player)) {
            return;
        }
        for (final ProtectedRegion region : this.playerRegions.get(player)) {
            final RegionLeaveEvent LeaveEvent = new RegionLeaveEvent(region, player, movementway, event);
            final RegionLeftEvent leftEvent = new RegionLeftEvent(region, player, movementway, event);
            Bukkit.getPluginManager().callEvent(LeaveEvent);
            Bukkit.getPluginManager().callEvent(leftEvent);
        }
        this.playerRegions.put(player, Sets.newHashSet());
    }

    private synchronized boolean updateRegions(final Player player, final MovementWay movement, final Location to, final Event event) {
        final com.sk89q.worldedit.util.Location Wto = BukkitAdapter.adapt(to);
        Set<ProtectedRegion> regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet<>();
        }
        else {
            regions = new HashSet<>(this.playerRegions.get(player));
        }
        final Set<ProtectedRegion> oldRegions = new HashSet<>(regions);
        final RegionManager rm = this.container.get(BukkitAdapter.adapt(to.getWorld()));
        if (rm == null) {
            return false;
        }
        final HashSet<ProtectedRegion> appRegions = new HashSet<>(rm.getApplicableRegions(Wto.toVector().toBlockPoint()).getRegions());
        final ProtectedRegion globalRegion = rm.getRegion("__global__");
        if (globalRegion != null) {
            appRegions.add(globalRegion);
        }
        for (final ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                final RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                this.plugin.getServer().getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getServer().getPluginManager().callEvent(new RegionEnteredEvent(region, player, movement, event)), 1L);
                regions.add(region);
            }
        }
        final Iterator<ProtectedRegion> itr = regions.iterator();
        while (itr.hasNext()) {
            final ProtectedRegion region = itr.next();
            if (!appRegions.contains(region)) {
                if (rm.getRegion(region.getId()) != region) {
                    itr.remove();
                }
                else {
                    final RegionLeaveEvent e2 = new RegionLeaveEvent(region, player, movement, event);
                    this.plugin.getServer().getPluginManager().callEvent(e2);
                    if (e2.isCancelled()) {
                        regions.clear();
                        regions.addAll(oldRegions);
                        return true;
                    }
                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getServer().getPluginManager().callEvent(new RegionLeftEvent(region, player, movement, event)), 1L);
                    itr.remove();
                }
            }
        }
        this.playerRegions.put(player, regions);
        return false;
    }
}

