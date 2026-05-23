package com.example.shopplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopListener implements Listener {

    private final ShopPlugin plugin;

    public ShopListener(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        String title = event.getView().getTitle();
        if (!title.contains("Shop")) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) return;

        int currentPage = parsePageFromTitle(title);

        if (slot == ShopGUI.CLOSE_SLOT) {
            player.closeInventory();
            return;
        }
        if (slot == ShopGUI.PREV_SLOT && currentPage > 0) {
            player.openInventory(ShopGUI.buildPage(currentPage - 1));
            return;
        }
        if (slot == ShopGUI.NEXT_SLOT && currentPage < ShopGUI.getTotalPages() - 1) {
            player.openInventory(ShopGUI.buildPage(currentPage + 1));
            return;
        }

        if (slot >= ShopGUI.getItemsPerPage()) return;

        Inventory topInv = event.getView().getTopInventory();
        ItemStack clicked = topInv.getItem(slot);
        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        int itemIndex = currentPage * ShopGUI.getItemsPerPage() + slot;
        List<ShopItem> items = plugin.getShopConfig().getItems();
        if (itemIndex >= items.size()) return;

        ShopItem shopItem = items.get(itemIndex);

        if (event.isLeftClick()) {
            handleBuy(player, shopItem);
        } else if (event.isRightClick()) {
            handleSell(player, shopItem);
        }
    }

    private void handleBuy(Player player, ShopItem shopItem) {
        // Hook Vault economy here if desired:
        // if (!eco.has(player, shopItem.getBuyPrice())) { player.sendMessage(...); return; }
        // eco.withdrawPlayer(player, shopItem.getBuyPrice());

        ItemStack reward = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
        player.getInventory().addItem(reward).forEach((k, leftover) ->
                player.getWorld().dropItemNaturally(player.getLocation(), leftover));

        player.sendMessage(ChatColor.GREEN + "✔ Bought " + shopItem.getAmount()
                + "x " + shopItem.getName()
                + ChatColor.GRAY + " for " + ChatColor.WHITE + "$" + shopItem.getBuyPrice());
    }

    private void handleSell(Player player, ShopItem shopItem) {
        ItemStack toSell = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
        if (!player.getInventory().containsAtLeast(toSell, shopItem.getAmount())) {
            player.sendMessage(ChatColor.RED + "✘ You don't have enough "
                    + shopItem.getName() + " to sell!");
            return;
        }

        player.getInventory().removeItem(toSell);

        // eco.depositPlayer(player, shopItem.getSellPrice());

        player.sendMessage(ChatColor.GOLD + "✔ Sold " + shopItem.getAmount()
                + "x " + shopItem.getName()
                + ChatColor.GRAY + " for " + ChatColor.WHITE + "$" + shopItem.getSellPrice());
    }

    private int parsePageFromTitle(String title) {
        try {
            int parenOpen = title.lastIndexOf('(');
            int slash     = title.lastIndexOf('/');
            if (parenOpen < 0 || slash < 0) return 0;
            String num = title.substring(parenOpen + 1, slash).replaceAll("[^0-9]", "").trim();
            return Integer.parseInt(num) - 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
