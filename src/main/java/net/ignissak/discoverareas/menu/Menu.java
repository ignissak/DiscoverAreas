package net.ignissak.discoverareas.menu;

import net.ignissak.discoverareas.menu.items.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Menu {

    private String title;
    private MenuItem[] items;
    private Inventory inventory;

    public Menu(String title, MenuItem[] items) throws IllegalArgumentException {
        this.title = title;
        this.items = items;

        int size = items.length;

        if (size % 9 != 0) {
            throw new IllegalArgumentException("Inventory size must be divisibe by 9");
        }

        this.inventory = Bukkit.createInventory(null, size, title);

        this.refreshItems();

    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }

    public MenuItem[] getItems() {
        return items;
    }

    public void refreshItems(){
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) continue;
            inventory.setItem(i, items[i].getItemStack());
        }
    }
}
