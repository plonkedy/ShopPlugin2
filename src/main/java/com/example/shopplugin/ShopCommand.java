package com.example.shopplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShopCommand implements CommandExecutor, TabCompleter {

    private final ShopPlugin plugin;

    public ShopCommand(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /shop reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("shopplugin.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to reload the shop.");
                return true;
            }
            plugin.getShopConfig().load();
            sender.sendMessage(ChatColor.GREEN + "✔ ShopPlugin config reloaded! Loaded "
                    + plugin.getShopConfig().getItems().size() + " items.");
            return true;
        }

        // /shop — open GUI
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can open the shop.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("shopplugin.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use the shop.");
            return true;
        }

        ShopGUI.open(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("shopplugin.reload")) {
            return Arrays.asList("reload");
        }
        return Collections.emptyList();
    }
}
