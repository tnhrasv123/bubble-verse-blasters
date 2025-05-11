
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class BackpackManager {
    
    private final BubbleGumSimulator plugin;
    
    public BackpackManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    public boolean upgradeBackpack(Player player, String newType) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Check if already has this backpack type
        if (bgPlayer.getBackpack().getType().equals(newType)) {
            return false;
        }
        
        // Get backpack config
        ConfigurationSection backpackConfig = plugin.getConfig().getConfigurationSection("backpacks." + newType);
        if (backpackConfig == null) {
            return false;
        }
        
        // Check if player can afford the upgrade
        long coinCost = backpackConfig.getLong("cost.coins", 0);
        long gemCost = backpackConfig.getLong("cost.gems", 0);
        
        if (!plugin.getEconomyManager().canAffordCoins(player, coinCost)) {
            return false;
        }
        
        if (!plugin.getEconomyManager().canAffordGems(player, gemCost)) {
            return false;
        }
        
        // Charge player
        plugin.getEconomyManager().removeCoins(player, coinCost);
        plugin.getEconomyManager().removeGems(player, gemCost);
        
        // Update backpack
        int capacity = backpackConfig.getInt("capacity", 10);
        bgPlayer.getBackpack().setType(newType);
        bgPlayer.getBackpack().setCapacity(capacity);
        
        return true;
    }
    
    public int getBackpackCapacity(String type) {
        ConfigurationSection backpackConfig = plugin.getConfig().getConfigurationSection("backpacks." + type);
        if (backpackConfig == null) {
            return 10; // Default capacity
        }
        return backpackConfig.getInt("capacity", 10);
    }
    
    public long getBackpackCoinCost(String type) {
        ConfigurationSection backpackConfig = plugin.getConfig().getConfigurationSection("backpacks." + type);
        if (backpackConfig == null) {
            return 0;
        }
        return backpackConfig.getLong("cost.coins", 0);
    }
    
    public long getBackpackGemCost(String type) {
        ConfigurationSection backpackConfig = plugin.getConfig().getConfigurationSection("backpacks." + type);
        if (backpackConfig == null) {
            return 0;
        }
        return backpackConfig.getLong("cost.gems", 0);
    }
}
