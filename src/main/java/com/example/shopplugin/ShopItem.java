package com.example.shopplugin;

import org.bukkit.Material;

public class ShopItem {

    private final String name;
    private final Material material;
    private final double buyPrice;
    private final double sellPrice;
    private final int amount;

    public ShopItem(String name, Material material, double buyPrice, double sellPrice, int amount) {
        this.name = name;
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
    }

    public String getName()       { return name; }
    public Material getMaterial() { return material; }
    public double getBuyPrice()   { return buyPrice; }
    public double getSellPrice()  { return sellPrice; }
    public int getAmount()        { return amount; }
}
