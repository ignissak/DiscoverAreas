package net.ignissak.discoverareas.discover;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverAreasPlugin;
import net.ignissak.discoverareas.events.AreaEnterEvent;
import net.ignissak.discoverareas.events.AreaLeaveEvent;
import net.ignissak.discoverareas.events.PlayerDiscoverEvent;
import net.ignissak.discoverareas.events.worldguard.RegionEnterEvent;
import net.ignissak.discoverareas.events.worldguard.RegionLeaveEvent;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.chatinput.ChatInput;
import net.ignissak.discoverareas.utils.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DiscoverManager implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DiscoverPlayer discoverPlayer = new DiscoverPlayer(player);

        DiscoverAreasPlugin.getInstance().getPlayers().put(player, discoverPlayer);

        if (player.hasPermission("discoverareas.admin")) {
            if (DiscoverAreasPlugin.getInstance().isUpdateAvailable() && DiscoverAreasPlugin.getConfiguration().getBoolean("general.update-notify")) {
                Bukkit.getScheduler().runTaskLater(DiscoverAreasPlugin.getInstance(), () -> {
                    player.sendMessage(ChatColor.RED + "Your servers is running out-of-date version of DiscoverAreas (" + DiscoverAreasPlugin.getInstance().getDescription().getVersion() + ").");
                    player.sendMessage(ChatColor.RED + "Download new version (" + DiscoverAreasPlugin.getInstance().getNewVersion() + ") on spigot page: ");
                    player.sendMessage(ChatColor.RED + "https://www.spigotmc.org/resources/discoverareas-1-14." + DiscoverAreasPlugin.getInstance().getResourceID() + "/");
                    player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, 1, 1);
                }, 40);
            }
        }

        DiscoverAreasPlugin.getMenuManager().updateMenus();
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        DiscoverPlayer discoverPlayer = DiscoverAreasPlugin.getDiscoverPlayer(player);
        if (discoverPlayer == null) return;

        ProtectedRegion region = event.getRegion();
        if (DiscoverAreasPlugin.getInstance().getCache().stream().noneMatch(area -> area.getRegion() == region)) return;
        Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getRegion() == region).findFirst().get();

        if (!discoverPlayer.hasDiscovered(area.getName())) {
            PlayerDiscoverEvent playerDiscoverEvent = new PlayerDiscoverEvent(player, area);
            Bukkit.getPluginManager().callEvent(playerDiscoverEvent);
            if (!playerDiscoverEvent.isCancelled()) {
                area.discover(discoverPlayer);
            }
        }

        Bukkit.getPluginManager().callEvent(new AreaEnterEvent(discoverPlayer, area));
    }

    @EventHandler
    public void onRegionLeave(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        DiscoverPlayer discoverPlayer = DiscoverAreasPlugin.getDiscoverPlayer(player);
        if (discoverPlayer == null) return;

        ProtectedRegion region = event.getRegion();
        if (DiscoverAreasPlugin.getInstance().getCache().stream().noneMatch(area -> area.getRegion() == region)) return;
        Area area = DiscoverAreasPlugin.getInstance().getCache().stream().filter(a -> a.getRegion() == region).findFirst().get();

        Bukkit.getPluginManager().callEvent(new AreaLeaveEvent(discoverPlayer, area));
    }

    @EventHandler
    public void onAreaEnter(AreaEnterEvent event) {
        Area area = event.getArea();
        DiscoverPlayer discoverPlayer = event.getPlayer();

        if (discoverPlayer.hasDiscovered(area.getName())) {
            if (DiscoverAreasPlugin.getConfiguration().getBoolean("title.on_enter.enabled")) {
                String title = ChatColor.translateAlternateColorCodes('&', DiscoverAreasPlugin.getConfiguration().getString("title.on_enter.title").replace("@area", area.getName())).replace("@description", area.getDescription());
                String subtitle = ChatColor.translateAlternateColorCodes('&', DiscoverAreasPlugin.getConfiguration().getString("title.on_enter.subtitle").replace("@area", area.getName())).replace("@description", area.getDescription());
                int fadein = DiscoverAreasPlugin.getConfiguration().getInt("title.on_enter.fadein") * 20;
                int stay = DiscoverAreasPlugin.getConfiguration().getInt("title.on_enter.stay") * 20;
                int fadeout = DiscoverAreasPlugin.getConfiguration().getInt("title.on_enter.fadeout") * 20;
                new Title(title, subtitle, fadein, stay, fadeout).send(discoverPlayer.getPlayer());
            }
            if (DiscoverAreasPlugin.getConfiguration().getBoolean("messages.on_enter.enabled")) {
                for (String s : DiscoverAreasPlugin.getConfiguration().getStringList("messages.on_enter.messages")) {
                    discoverPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("@area", area.getName()).replace("@description", area.getDescription())));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (ChatInput.isInInputMode(player)) {
            ChatInput input = ChatInput.getInput(player);
            if (ChatInput.isStopMessage(message)) {
                input.getExitMethod().run(player);
            } else {
                input.getCompleteMethod().run(player, message);
            }
            input.finish();
            event.setCancelled(true);
            return;
        }
    }
}
