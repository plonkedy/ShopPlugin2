package com.example.shopplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private static final int ITEMS_PER_PAGE = 36;
    private static final int GUI_SIZE       = 45;

    public static final int PREV_SLOT  = 39;
    public static final int INFO_SLOT  = 40;
    public static final int NEXT_SLOT  = 41;
    public static final int CLOSE_SLOT = 44;

    public static int getTotalPages() {
        int size = ShopPlugin.getInstance().getShopConfig().getItems().size();
        return Math.max(1, (int) Math.ceil((double) size / ITEMS_PER_PAGE));
    }

    public static int getItemsPerPage() { return ITEMS_PER_PAGE; }

    public static Inventory buildPage(int page) {
        List<ShopItem> shopItems = ShopPlugin.getInstance().getShopConfig().getItems();
        int totalPages = getTotalPages();

        String title = ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "✦ Shop" +
                ChatColor.GRAY + " (Page " + (page + 1) + "/" + totalPages + ")";
        Inventory inv = Bukkit.createInventory(null, GUI_SIZE, title);

        // Fill item slots
        int start = page * ITEMS_PER_PAGE;
        int end   = Math.min(start + ITEMS_PER_PAGE, shopItems.size());

        for (int i = start; i < end; i++) {
            inv.setItem(i - start, buildItemStack(shopItems.get(i)));
        }

        // Filler for empty item slots
        ItemStack filler = buildFiller();
        for (int slot = 0; slot < ITEMS_PER_PAGE; slot++) {
            if (inv.getItem(slot) == null) inv.setItem(slot, filler);
        }

        // Bottom bar
        for (int slot = 36; slot < 45; slot++) inv.setItem(slot, buildFiller());

        if (page > 0)
            inv.setItem(PREV_SLOT, buildNavButton(Material.ARROW, ChatColor.YELLOW + "◀ Previous Page"));
        if (page < totalPages - 1)
            inv.setItem(NEXT_SLOT, buildNavButton(Material.ARROW, ChatColor.YELLOW + "Next Page ▶"));

        inv.setItem(INFO_SLOT, buildNavButton(Material.BOOK,
                ChatColor.AQUA + "Left-click" + ChatColor.GRAY + " = Buy  |  " +
                ChatColor.GOLD + "Right-click" + ChatColor.GRAY + " = Sell"));
        inv.setItem(CLOSE_SLOT, buildNavButton(Material.BARRIER, ChatColor.RED + "Close Shop"));

        return inv;
    }

    private static ItemStack buildItemStack(ShopItem si) {
        ItemStack stack = new ItemStack(si.getMaterial(), si.getAmount());
        ItemMeta meta   = stack.getItemMeta();
        if (meta == null) return stack;

        meta.setDisplayName(ChatColor.GREEN + si.getName());

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Amount: " + ChatColor.WHITE + si.getAmount());
        lore.add("");
        lore.add(ChatColor.AQUA + "Buy:  " + ChatColor.WHITE + "$" + formatPrice(si.getBuyPrice()));
        lore.add(ChatColor.GOLD + "Sell: " + ChatColor.WHITE + "$" + formatPrice(si.getSellPrice()));
        lore.add("");
        lore.add(ChatColor.YELLOW + "Left-click"  + ChatColor.GRAY + " to buy");
        lore.add(ChatColor.YELLOW + "Right-click" + ChatColor.GRAY + " to sell");

        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private static ItemStack buildFiller() {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta  = pane.getItemMeta();
        if (meta != null) { meta.setDisplayName(" "); pane.setItemMeta(meta); }
        return pane;
    }

    private static ItemStack buildNavButton(Material mat, String name) {
        ItemStack btn = new ItemStack(mat);
        ItemMeta meta = btn.getItemMeta();
        if (meta != null) { meta.setDisplayName(name); btn.setItemMeta(meta); }
        return btn;
    }

    private static String formatPrice(double price) {
        if (price == Math.floor(price)) return String.valueOf((int) price);
        return String.format("%.2f", price);
    }

    public static void open(Player player) {
        player.openInventory(buildPage(0));
    }
}
