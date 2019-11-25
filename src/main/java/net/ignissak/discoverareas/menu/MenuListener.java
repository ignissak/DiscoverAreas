package net.ignissak.discoverareas.menu;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.menu.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Menu menu = DiscoverMain.getMenuManager().getMenuByTitle(e.getView().getTitle());
        if (menu == null) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType().equals(Material.AIR)) {
            return;
        }
        if (e.getRawSlot() >= e.getInventory().getSize()) {
            return;
        }
        MenuItem menuItem = menu.getItems()[e.getRawSlot()];
        if (menuItem.isClosing()) {
            p.closeInventory();
        }
        if ((e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) && menuItem.getShiftMethod() != null) {
            menuItem.getShiftMethod().run(p);
        } else {
            menuItem.getMethod().run(p);
        }
        e.setCancelled(true);
    }
}
