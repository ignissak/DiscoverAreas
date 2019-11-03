package net.ignissak.discoverareas.utils;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemBuilder {

    protected ItemStack is;
    private HashMap<String, String> placeholders = new HashMap<String, String>();

    /**
     * Create a new ItemBuilder over an existing itemstack.
     *
     * @param is The itemstack to create the ItemBuilder over.
     */
    public ItemBuilder(ItemStack is) {
        this.is = is;
    }


    /**
     * Create a new ItemBuilder from Vanilla Material.
     *
     * @param m The material to create the ItemBuilder with.
     */
    public ItemBuilder(Material m) {
        this(m, 1);
    }


    /**
     * Create a new ItemBuilder from Vanilla Material.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public ItemBuilder(Material m, int amount) {
        is = new ItemStack(m, amount);
    }

    /**
     * Create a new ItemBuilder from Vanilla Material.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     * @param s      The durability of the item.
     */
    public ItemBuilder(Material m, int amount, short s) {
        is = new ItemStack(m, amount, s);
    }

    public ItemBuilder(Material m, short s, String name) {
        is = new ItemStack(m, s);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
    }

    /**
     * Add an enchant to the item.
     *
     * @param ench  The enchant to add
     * @param level The level
     * @return ItemBuilder
     */
    public ItemBuilder addEnchant(Enchantment ench, int level) {
        if (ench != null) {
            ItemMeta im = is.getItemMeta();
            im.addEnchant(ench, level, true);
            is.setItemMeta(im);
        }
        return this;
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchants The enchants to add.
     * @return ItemBuilder
     */
    public ItemBuilder addEnchantments(HashMap<String, Integer> enchants) {
        if ((enchants == null) || (enchants.size() == 0)) {
            return this;
        }
        HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
        for (String enchant : enchants.keySet()) {
            enchantments.put(Enchantment.getByName(enchant), enchants.get(enchant));
        }
        return addEnchantments(enchantments);
    }

    /**
     * Add multiple enchants at once.
     *
     * @param enchantments The enchants to add.
     * @return ItemBuilder
     */
    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            addEnchant(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Hides all ItemFlags on the item
     *
     * @return ItemBuilder
     */
    public ItemBuilder hideAllFlags() {
        try {
            ItemMeta meta = is.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(meta);
        } catch (Exception ex) {
            //TODO: Main.getInstance().debug("Invalid flag: " + flag);
        }
        return this;
    }

    /**
     * Adds selected ItemFlag on the item
     *
     * @param flag selected ItemFlag
     * @return ItemBuilder
     */
    public ItemBuilder addItemFlag(ItemFlag flag) {
        try {
            ItemMeta meta = is.getItemMeta();
            meta.addItemFlags(flag);
            is.setItemMeta(meta);
        } catch (Exception ex) {
            //TODO: Main.getInstance().debug("Invalid flag: " + flag);
        }
        return this;
    }

    /**
     * Adds selected ItemFlag on the item by name
     *
     * @param flag selected ItemFlag
     * @return ItemBuilder
     */
    public ItemBuilder addItemFlag(String flag) {
        try {
            ItemMeta meta = is.getItemMeta();
            meta.addItemFlags(ItemFlag.valueOf(flag));
            is.setItemMeta(meta);
        } catch (Exception ex) {
            //TODO: Main.getInstance().debug("Invalid flag: " + flag);
        }
        return this;
    }

    /**
     * Adds Glowing effect on the item
     *
     * <b>Please use ONLY on a lobby/hub items!</b>
     *
     * @return ItemBuilder
     */
    public ItemBuilder setGlowing(){
        try {
            ItemMeta meta = is.getItemMeta();
            meta.addEnchant(Enchantment.DURABILITY, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(meta);
        } catch (Exception ex) {
            //TODO: Main.getInstance().debug("Invalid flag: " + flag);
        }
        return this;
    }

    /**
     * Adds array of lore on the item
     *
     * @param lore Array of lore
     * @return ItemBuilder
     */
    public ItemBuilder addLore(String... lore) {
        if (lore != null) {
            List<String> finalLore = new ArrayList<>(Arrays.asList(lore));
            ItemMeta meta = is.getItemMeta();
            meta.setLore(finalLore);
            is.setItemMeta(meta);
        }
        return this;
    }

    /**
     * Add a lore line.
     * <b>\n</b> will create new line
     *
     * @param line The lore line to add.
     * @return ItemBuilder
     */
    public ItemBuilder addLoreLine(String line) {
        if (line != null) {
            ItemMeta im = is.getItemMeta();
            List<String> lore = new ArrayList<>();
            if (im.hasLore()) {
                lore = new ArrayList<>(im.getLore());
            }
            for (String str : line.split("\n")) {
                lore.add(str);
            }
            setLore(lore);
        }
        return this;
    }

    /**
     * Add a lore line.
     *
     * @param line The lore line to add.
     * @param pos  The index of where to put it.
     * @return ItemBuilder
     */
    public ItemBuilder addLoreLine(String line, int pos) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.add(line);
        for (int i = lore.size() - 1; i > pos; i--) {
            Collections.swap(lore, i, i - 1);
        }
        return setLore(lore);
    }

    public ItemBuilder addPlaceholder(String toReplace, String replaceWith) {
        placeholders.put(toReplace, replaceWith);
        return this;
    }

    /**
     * Clone the ItemBuilder into a new one.
     *
     * @return The cloned instance.
     */
    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(is);
    }

    private int getAmount() {
        return is.getAmount();
    }

    public ArrayList<String> getLore() {
        if (hasCustomLore()) {
            List<String> lore = is.getItemMeta().getLore();
            ArrayList<String> list = new ArrayList<String>();
            if (lore != null) {
                list.addAll(lore);
            }
            return list;
        }
        return new ArrayList<String>();

    }

    public String getName() {
        if (hasCustomDisplayName()) {
            return is.getItemMeta().getDisplayName();
        }
        return "";
    }

    /**
     * @return the skull
     */
    @Deprecated
    public String getSkull() {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            if (im.hasOwner()) {
                return im.getOwner();
            }
        } catch (ClassCastException expected) {
        }
        return "";
    }

    public OfflinePlayer getSkullOwner() {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            if (im.hasOwner()) {
                return im.getOwningPlayer();
            }
        } catch (ClassCastException expected) {
        }
        return null;
    }

    public boolean hasCustomDisplayName() {
        return hasItemMeta() && is.getItemMeta().hasDisplayName();
    }

    public boolean hasCustomLore() {
        return hasItemMeta() && is.getItemMeta().hasLore();
    }

    public boolean hasItemMeta() {
        return is.hasItemMeta();
    }

    /**
     * Remove a certain enchant from the item.
     *
     * @param ench The enchantment to remove
     * @return ItemBuilder
     */
    public ItemBuilder removeEnchantment(Enchantment ench) {
        is.removeEnchantment(ench);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param index The index of the lore line to remove.
     * @return ItemBuilder
     */
    public ItemBuilder removeLoreLine(int index) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (index < 0 || index > lore.size()) {
            return this;
        }
        lore.remove(index);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Remove a lore line.
     *
     * @param line The lore to remove.
     * @return ItemBuilder
     */
    public ItemBuilder removeLoreLine(String line) {
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) {
            return this;
        }
        lore.remove(line);
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder setAmountNone(int i) {
        if (getAmount() == 0) {
            setAmount(i);
        }
        return this;
    }

    /**
     * Change the durability of the item.
     *
     * @param dur The durability to set it to.
     * @return ItemBuilder
     */
    public ItemBuilder setDurability(short dur) {
        is.setDurability(dur);
        return this;
    }

    /**
     * Sets the dye color on an item. <b>* Notice that this doesn't check for item
     * type, sets the literal data of the dyecolor as durability.</b>
     *
     * @param color The color to put.
     * @return ItemBuilder
     */
    @SuppressWarnings("deprecation")
    public ItemBuilder setDyeColor(DyeColor color) {
        is.setDurability(color.getDyeData());
        return this;
    }

    /**
     * Sets infinity durability on the item by setting the durability to
     * Short.MAX_VALUE.
     *
     * @return ItemBuilder
     */
    public ItemBuilder setInfinityDurability() {
        is.setDurability(Short.MAX_VALUE);
        return this;
    }

    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor
     * pieces.
     *
     * @param color The color to set it to.
     * @return ItemBuilder
     */
    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
            im.setColor(color);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     * @return ItemBuilder
     */
    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = is.getItemMeta();
        im.setLore(lore);
        is.setItemMeta(im);
        return this;
    }

    /**
     * Re-sets the lore.
     *
     * @param lore The lore to set it to.
     * @return ItemBuilder
     */
    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    /**
     * Set the displayname of the item.
     *
     * @param name The name to change it to.
     * @return ItemBuilder
     */
    public ItemBuilder setName(String name) {
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(im);
        return this;
    }

    public ItemBuilder setPlaceholders(HashMap<String, String> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer offlinePlayer) {
        if (offlinePlayer != null) {
            String version = "1_13";
            try {
                SkullMeta im = (SkullMeta) is.getItemMeta();
                im.setOwningPlayer(offlinePlayer);
                is.setItemMeta(im);
            } catch (Exception expected) {
                setSkullOwner(offlinePlayer.getName());
            }
        }
        return this;
    }

    /**
     * Set the skull owner for the item. Works on skulls only.
     *
     * @param owner The name of the skull's owner.
     * @return ItemBuilder
     */
    @Deprecated
    public ItemBuilder setSkullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) is.getItemMeta();
            im.setOwner(owner);
            is.setItemMeta(im);
        } catch (ClassCastException expected) {
        }
        return this;
    }

    /**
     * Build Item from ItemBuilder to ItemStack
     *
     * @return ItemStack of create item
     */
    public ItemStack build() {
        return is;
    }

}
