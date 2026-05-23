package com.example.shopplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class ShopPlugin extends JavaPlugin {

    private static ShopPlugin instance;
    private ShopConfig shopConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config.yml if it doesn't exist
        saveDefaultConfig();

        // Load shop items from config
        shopConfig = new ShopConfig(this);
        shopConfig.load();

        getLogger().info("Loaded " + shopConfig.getItems().size() + " shop items from config.yml");
        getLogger().info("ShopPlugin has been enabled!");

        // Register /shop command
        ShopCommand shopCommand = new ShopCommand(this);
        getCommand("shop").setExecutor(shopCommand);
        getCommand("shop").setTabCompleter(shopCommand);

        // Register GUI listener
        getServer().getPluginManager().registerEvents(new ShopListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ShopPlugin has been disabled!");
    }

    public static ShopPlugin getInstance() {
        return instance;
    }

    public ShopConfig getShopConfig() {
        return shopConfig;
    }
}
