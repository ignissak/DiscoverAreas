package net.ignissak.discoverareas.menu.items;

import net.ignissak.discoverareas.utils.PlayerRunnable;
import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private ItemStack itemStack;
    private PlayerRunnable method, shiftMethod, rightMethod;
    private MenuItemUpdateMethod updateMethod;
    private boolean closing = false, update = false;

    public MenuItem(ItemStack itemStack, PlayerRunnable method) {
        this.itemStack = itemStack;
        this.method = method;
    }

    public MenuItem(ItemStack itemStack, PlayerRunnable method, boolean closing) {
        this.itemStack = itemStack;
        this.method = method;
        this.closing = closing;
    }

    public MenuItem(ItemStack itemStack, PlayerRunnable method, MenuItemUpdateMethod updateMethod) {
        this.itemStack = itemStack;
        this.method = method;
        this.update = true;
        this.updateMethod = updateMethod;
    }

    public MenuItem(ItemStack itemStack, PlayerRunnable method, MenuItemUpdateMethod updateMethod, boolean closing) {
        this.itemStack = itemStack;
        this.method = method;
        this.update = true;
        this.updateMethod = updateMethod;
        this.closing = closing;
    }

    public MenuItem(ItemStack itemStack, MenuItemUpdateMethod updateMethod) {
        this.itemStack = itemStack;
        this.method = p -> {};
        this.update = true;
        this.updateMethod = updateMethod;
    }

    public MenuItem(ItemStack itemStack, PlayerRunnable method, PlayerRunnable shiftMethod, boolean closing) {
        this.itemStack = itemStack;
        this.method = method;
        this.shiftMethod = shiftMethod;
        this.closing = closing;
    }

    public MenuItem(ItemStack itemStack, PlayerRunnable method, PlayerRunnable shiftMethod, PlayerRunnable rightMethod, boolean closing) {
        this.itemStack = itemStack;
        this.method = method;
        this.shiftMethod = shiftMethod;
        this.rightMethod = rightMethod;
        this.closing = closing;
    }


    public PlayerRunnable getMethod() {
        return method;
    }

    public PlayerRunnable getShiftMethod() {
        return shiftMethod;
    }

    public PlayerRunnable getRightMethod() {
        return rightMethod;
    }

    public boolean isClosing() {
        return closing;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isUpdated() {
        return update;
    }

    public void update() {
        this.itemStack = this.updateMethod.run();
    }
}
