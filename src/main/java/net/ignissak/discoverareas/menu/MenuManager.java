package net.ignissak.discoverareas.menu;

import net.ignissak.discoverareas.DiscoverMain;
import net.ignissak.discoverareas.menu.items.MenuItem;
import net.ignissak.discoverareas.objects.Area;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuManager {

    private HashMap<String, Menu> menus;
    ItemStack previous, next;

    public MenuManager() {
        this.menus = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), DiscoverMain.getInstance());

        //this.previous = DiscoverMain.getInstance().getPrevious().build();
        //this.next = DiscoverMain.getInstance().getNext().build();

        updateMenus();
    }

    public Menu getMenu(String menuName) {
        Menu menu = this.menus.getOrDefault(menuName, null);
        if (menu == null) return null;

        boolean updated = false;
        for (MenuItem item : menu.getItems()) {
            if (item == null) continue;
            if (item.isUpdated()) {
                item.update();
                updated = true;
            }
        }
        if (updated) menu.refreshItems();
        return menu;
    }

    public Menu getMenuByTitle(String title) {
        for (Menu m : this.menus.values()) {
            if (m.getTitle().equals(title)) return m;
        }
        return null;
    }

    public void updateMenus() {
        List<Area> areasList = new ArrayList<>(DiscoverMain.getInstance().getCache());
        areasList.sort(Comparator.comparing(Area::getName));
        Iterator<Area> iterator = areasList.iterator();

        if (areasList.size() <= 45) {
            MenuItem[] items = new MenuItem[45];
            int i = 0;
            while (iterator.hasNext()) {
                Area a = iterator.next();
                items[i] = new MenuItem(DiscoverMain.getInstance().getAdminItem(a).build(), a::sendCommands, a::teleport, true);
                i++;
                iterator.remove();
            }

            Menu adminMenu = new Menu("Areas | Admin menu", items);
            this.menus.put("adminMenu", adminMenu);
        }
    }

}
