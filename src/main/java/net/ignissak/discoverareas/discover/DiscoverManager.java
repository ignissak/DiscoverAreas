package net.ignissak.discoverareas.discover;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverMain;
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

        DiscoverMain.getInstance().getPlayers().put(player, discoverPlayer);

        if (player.hasPermission("discoverareas.admin")) {
            if (DiscoverMain.getInstance().isUpdateAvailable() && DiscoverMain.getConfiguration().getBoolean("general.update-notify")) {
                player.sendMessage(ChatColor.RED + "Your servers is running out-of-date version of DiscoverAreas (" + DiscoverMain.getInstance().getDescription().getVersion() + ").");
                player.sendMessage(ChatColor.RED + "Download new version (" + DiscoverMain.getInstance().getNewVersion() + ") on spigot page: ");
                player.sendMessage(ChatColor.RED + "https://www.spigotmc.org/resources/discoverareas-1-13." + DiscoverMain.getInstance().getResourceID() + "/");
            }
        }
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        DiscoverPlayer discoverPlayer = DiscoverMain.getDiscoverPlayer(player);
        if (discoverPlayer == null) return;

        ProtectedRegion region = event.getRegion();
        if (DiscoverMain.getInstance().getCache().stream().noneMatch(area -> area.getRegion() == region)) return;
        Area area = DiscoverMain.getInstance().getCache().stream().filter(a -> a.getRegion() == region).findFirst().get();

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
        DiscoverPlayer discoverPlayer = DiscoverMain.getDiscoverPlayer(player);
        if (discoverPlayer == null) return;

        ProtectedRegion region = event.getRegion();
        if (DiscoverMain.getInstance().getCache().stream().noneMatch(area -> area.getRegion() == region)) return;
        Area area = DiscoverMain.getInstance().getCache().stream().filter(a -> a.getRegion() == region).findFirst().get();

        Bukkit.getPluginManager().callEvent(new AreaLeaveEvent(discoverPlayer, area));
    }

    @EventHandler
    public void onAreaEnter(AreaEnterEvent event) {
        Area area = event.getArea();
        DiscoverPlayer discoverPlayer = event.getPlayer();

        if (discoverPlayer.hasDiscovered(area.getName())) {
            if (DiscoverMain.getConfiguration().getBoolean("title.on_enter.enabled")) {
                String title = ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("title.on_enter.title").replace("@area", area.getName())).replace("@description", area.getDescription());
                String subtitle = ChatColor.translateAlternateColorCodes('&', DiscoverMain.getConfiguration().getString("title.on_enter.subtitle").replace("@area", area.getName())).replace("@description", area.getDescription());
                int fadein = DiscoverMain.getConfiguration().getInt("title.on_enter.fadein") * 20;
                int stay = DiscoverMain.getConfiguration().getInt("title.on_enter.stay") * 20;
                int fadeout = DiscoverMain.getConfiguration().getInt("title.on_enter.fadeout") * 20;
                new Title(title, subtitle, fadein, stay, fadeout).send(discoverPlayer.getPlayer());
            }
            if (DiscoverMain.getConfiguration().getBoolean("messages.on_enter.enabled")) {
                for (String s : DiscoverMain.getConfiguration().getStringList("messages.on_enter.messages")) {
                    discoverPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("@area", area.getName()).replace("@description", area.getDescription())));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if(ChatInput.isInInputMode(player)){
            ChatInput input = ChatInput.getInput(player);
            if(ChatInput.isStopMessage(message)){
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
