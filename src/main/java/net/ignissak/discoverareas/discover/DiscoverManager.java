package net.ignissak.discoverareas.discover;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.events.PlayerDiscoverEvent;
import net.ignissak.discoverareas.events.worldguard.RegionEnterEvent;
import net.ignissak.discoverareas.objects.Area;
import net.ignissak.discoverareas.utils.chatinput.ChatInput;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DiscoverManager implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        DiscoverPlayer discoverPlayer = new DiscoverPlayer(p);

        DiscoverMain.getInstance().getPlayers().put(p, discoverPlayer);
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
