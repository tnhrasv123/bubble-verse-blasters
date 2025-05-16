
package com.bubblegum.utils;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Bubble;
import org.bukkit.configuration.ConfigurationSection;

public class BubbleCalculator {
    
    private final BubbleGumSimulator plugin;
    
    public BubbleCalculator(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Calculate total value of all bubbles in player's backpack
     * 
     * @param bgPlayer The player's data
     * @return Total coin value
     */
    public long calculateBubblesSellValue(BGPlayer bgPlayer) {
        long totalValue = 0;
        
        for (Bubble bubble : bgPlayer.getBackpack().getBubbles()) {
            String gumType = bubble.getGumType();
            double size = bubble.getSize();
            double baseValue = plugin.getConfig().getDouble("bubbles.base-value", 5);
            
            // Get value multiplier for the gum type
            ConfigurationSection gumConfig = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
            double valueMultiplier = 1.0;
            if (gumConfig != null) {
                valueMultiplier = gumConfig.getDouble("value-multiplier", 1.0);
            }
            
            // Apply gum mastery multiplier
            int masteryLevel = bgPlayer.getGumMastery(gumType);
            valueMultiplier *= (1 + masteryLevel * 0.01);
            
            // Apply pet multipliers
            valueMultiplier *= bgPlayer.getPetBubbleMultiplier();
            
            // Calculate value
            long value = (long) (size * baseValue * valueMultiplier);
            totalValue += value;
        }
        
        return totalValue;
    }
    
    /**
     * Calculate the growth rate for a specific gum type and player
     * 
     * @param bgPlayer The player's data
     * @param gumType The type of gum being used
     * @return Growth rate per tick
     */
    public double calculateGrowthRate(BGPlayer bgPlayer, String gumType) {
        ConfigurationSection gumConfig = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
        if (gumConfig == null) {
            return 0.05; // Default growth rate
        }
        
        // Get growth rate from config
        double growthRate = gumConfig.getDouble("growth-rate", 1.0) * 0.05;
        
        // Apply pet multipliers
        growthRate *= bgPlayer.getPetBubbleMultiplier();
        
        // Apply gum mastery multiplier
        int masteryLevel = bgPlayer.getGumMastery(gumType);
        growthRate *= (1 + masteryLevel * 0.02);
        
        return growthRate;
    }
    
    /**
     * Calculate the maximum size for a bubble with the given gum type
     * 
     * @param bgPlayer The player's data
     * @param gumType The type of gum being used
     * @return Maximum bubble size
     */
    public double calculateMaxSize(BGPlayer bgPlayer, String gumType) {
        ConfigurationSection gumConfig = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
        if (gumConfig == null) {
            return 10.0; // Default max size
        }
        
        // Check maximum size
        double maxSize = gumConfig.getDouble("max-size", 10.0);
        
        // Apply pet multipliers to max size
        maxSize *= bgPlayer.getPetBubbleMultiplier();
        
        return maxSize;
    }
}
