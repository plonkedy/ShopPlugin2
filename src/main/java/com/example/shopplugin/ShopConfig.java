package com.example.shopplugin;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ShopConfig {

    private final ShopPlugin plugin;
    private final List<ShopItem> items = new ArrayList<>();

    public ShopConfig(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        items.clear();
        plugin.reloadConfig();

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("items");
        if (section == null) {
            plugin.getLogger().warning("No 'items' section found in config.yml! Using no items.");
            return;
        }

        for (String key : section.getKeys(false)) {
            ConfigurationSection item = section.getConfigurationSection(key);
            if (item == null) continue;

            String name       = item.getString("name", key);
            String matString  = item.getString("material", "STONE");
            double buyPrice   = item.getDouble("buy-price", 10.0);
            double sellPrice  = item.getDouble("sell-price", 5.0);
            int amount        = item.getInt("amount", 1);

            Material material;
            try {
                material = Material.valueOf(matString.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material '" + matString + "' for item '" + key + "' — skipping.");
                continue;
            }

            items.add(new ShopItem(name, material, buyPrice, sellPrice, amount));
        }
    }

    public List<ShopItem> getItems() {
        return items;
    }
}
