
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public ShopCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.shop")) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        openShopMenu(player);
        
        return true;
    }
    
    private void openShopMenu(Player player) {
        // In a full implementation, this would open a GUI
        // For this simplified version, we'll just show a text menu
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        MessageUtils.sendMessage(player, "&6=== Bubble Gum Simulator Shop ===");
        MessageUtils.sendMessage(player, "&7Your Coins: &f" + plugin.getEconomyManager().formatCoins(bgPlayer.getCoins()));
        MessageUtils.sendMessage(player, "&7Your Gems: &f" + plugin.getEconomyManager().formatGems(bgPlayer.getGems()));
        MessageUtils.sendMessage(player, "");
        
        // Show gum types
        MessageUtils.sendMessage(player, "&b=== Gum Types ===");
        ConfigurationSection gumTypesSection = plugin.getConfig().getConfigurationSection("gum-types");
        if (gumTypesSection != null) {
            for (String gumType : gumTypesSection.getKeys(false)) {
                ConfigurationSection gumConfig = gumTypesSection.getConfigurationSection(gumType);
                if (gumConfig != null) {
                    String name = gumConfig.getString("name", gumType);
                    long coinCost = gumConfig.getLong("cost.coins", 0);
                    long gemCost = gumConfig.getLong("cost.gems", 0);
                    int requiredLevel = gumConfig.getInt("required-level", 0);
                    
                    String costString = "";
                    if (coinCost > 0) {
                        costString += plugin.getEconomyManager().formatCoins(coinCost);
                    }
                    if (gemCost > 0) {
                        if (!costString.isEmpty()) {
                            costString += " & ";
                        }
                        costString += plugin.getEconomyManager().formatGems(gemCost);
                    }
                    
                    String levelReq = requiredLevel > 0 ? " &7(Level " + requiredLevel + ")" : "";
                    MessageUtils.sendMessage(player, "&7- &f" + name + " &7- Cost: &f" + costString + levelReq);
                }
            }
            MessageUtils.sendMessage(player, "&7To buy gum: &f/bubblegum give " + player.getName() + " gum <type>");
        }
        
        // Show backpack upgrades
        MessageUtils.sendMessage(player, "&b=== Backpack Upgrades ===");
        ConfigurationSection backpacksSection = plugin.getConfig().getConfigurationSection("backpacks");
        if (backpacksSection != null) {
            for (String backpackType : backpacksSection.getKeys(false)) {
                ConfigurationSection backpackConfig = backpacksSection.getConfigurationSection(backpackType);
                if (backpackConfig != null) {
                    String name = backpackConfig.getString("name", backpackType);
                    int capacity = backpackConfig.getInt("capacity", 10);
                    long coinCost = backpackConfig.getLong("cost.coins", 0);
                    long gemCost = backpackConfig.getLong("cost.gems", 0);
                    
                    String costString = "";
                    if (coinCost > 0) {
                        costString += plugin.getEconomyManager().formatCoins(coinCost);
                    }
                    if (gemCost > 0) {
                        if (!costString.isEmpty()) {
                            costString += " & ";
                        }
                        costString += plugin.getEconomyManager().formatGems(gemCost);
                    }
                    
                    if (backpackType.equals(bgPlayer.getBackpack().getType())) {
                        MessageUtils.sendMessage(player, "&a✓ &f" + name + " &7- Capacity: &f" + capacity + " &7- Current");
                    } else {
                        MessageUtils.sendMessage(player, "&7- &f" + name + " &7- Capacity: &f" + capacity + " &7- Cost: &f" + costString);
                    }
                }
            }
            MessageUtils.sendMessage(player, "&7To upgrade backpack: &fmenu not yet implemented");
        }
        
        // Show eggs
        MessageUtils.sendMessage(player, "&b=== Eggs ===");
        MessageUtils.sendMessage(player, "&7Use &f/eggs list &7to view available eggs");
        MessageUtils.sendMessage(player, "&7Use &f/eggs buy <egg-type> &7to purchase an egg");
    }
}
