
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Bubble;
import com.bubblegum.rendering.BubbleRenderer;
import com.bubblegum.utils.BubbleCalculator;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BubbleManager {
    
    private final BubbleGumSimulator plugin;
    private final Map<UUID, BukkitTask> blowingTasks;
    private final Map<UUID, Double> currentBubbleSizes;
    private final BubbleCalculator calculator;
    
    public BubbleManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.blowingTasks = new HashMap<>();
        this.currentBubbleSizes = new HashMap<>();
        this.calculator = new BubbleCalculator(plugin);
    }
    
    public void startBlowingBubble(Player player) {
        UUID uuid = player.getUniqueId();
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        if (blowingTasks.containsKey(uuid)) {
            // Player is already blowing a bubble
            return;
        }
        
        // Check if player has the required gum
        String gumType = bgPlayer.getCurrentGumType();
        
        if (!isValidGumType(gumType)) {
            MessageUtils.sendMessage(player, "&cInvalid gum type!");
            return;
        }
        
        // Calculate growth rate for this bubble
        double growthRate = calculator.calculateGrowthRate(bgPlayer, gumType);
        
        // Start growing the bubble
        currentBubbleSizes.put(uuid, 0.0);
        
        MessageUtils.sendMessage(player, plugin.getConfig().getString("messages.bubble.blow-start"));
        
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isValid()) {
                    cancelTask(uuid);
                    return;
                }
                
                double size = currentBubbleSizes.getOrDefault(uuid, 0.0);
                size += growthRate;
                
                // Check maximum size
                double maxSize = calculator.calculateMaxSize(bgPlayer, gumType);
                
                if (size > maxSize) {
                    size = maxSize;
                }
                
                currentBubbleSizes.put(uuid, size);
                bgPlayer.setCurrentBubbleSize(size);
                
                // Display bubble
                BubbleRenderer.displayBubble(player, size, gumType);
                
                // If reached max size, finish blowing
                if (size >= maxSize) {
                    finishBlowingBubble(player);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
        
        blowingTasks.put(uuid, task);
    }
    
    public void cancelBlowingBubble(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (blowingTasks.containsKey(uuid)) {
            blowingTasks.get(uuid).cancel();
            blowingTasks.remove(uuid);
            
            MessageUtils.sendMessage(player, plugin.getConfig().getString("messages.bubble.blow-cancel"));
        }
        
        currentBubbleSizes.remove(uuid);
    }
    
    public void finishBlowingBubble(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (!blowingTasks.containsKey(uuid)) {
            return;
        }
        
        // Cancel the task
        cancelTask(uuid);
        
        // Get the final size
        double size = currentBubbleSizes.getOrDefault(uuid, 0.0);
        currentBubbleSizes.remove(uuid);
        
        if (size <= 0) {
            return;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        String gumType = bgPlayer.getCurrentGumType();
        
        // Create a bubble
        Bubble bubble = new Bubble(size, gumType);
        
        // Add to player's backpack if there's room
        if (!bgPlayer.getBackpack().addBubble(bubble)) {
            MessageUtils.sendMessage(player, plugin.getConfig().getString("messages.bubble.backpack-full"));
            return;
        }
        
        // Increment stats
        bgPlayer.incrementTotalBubblesBlown();
        bgPlayer.increaseGumMastery(gumType, 1);
        bgPlayer.addExperience(1);
        
        // Send success message
        String message = plugin.getConfig().getString("messages.bubble.blow-complete", "&aYou blew a bubble of size &e{size}&a!");
        message = message.replace("{size}", String.format("%.2f", size));
        MessageUtils.sendMessage(player, message);
        
        // Break through with a particle effect
        Location loc = player.getLocation().add(0, 1, 0);
        player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 5, 0.2, 0.2, 0.2, 0.1);
    }
    
    public long sellBubbles(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        if (bgPlayer.getBackpack().getBubbles().isEmpty()) {
            MessageUtils.sendMessage(player, "&cYou don't have any bubbles to sell!");
            return 0;
        }
        
        long totalValue = calculator.calculateBubblesSellValue(bgPlayer);
        
        // Add coins to player
        bgPlayer.addCoins(totalValue);
        
        // Clear backpack
        bgPlayer.getBackpack().clearBubbles();
        
        // Send success message
        String message = plugin.getConfig().getString("messages.bubble.sell", "&aYou sold your bubbles for &e{coins} coins&a!");
        message = message.replace("{coins}", String.valueOf(totalValue));
        MessageUtils.sendMessage(player, message);
        
        return totalValue;
    }
    
    private boolean isValidGumType(String gumType) {
        return plugin.getConfig().getConfigurationSection("gum-types." + gumType) != null;
    }
    
    private void cancelTask(UUID uuid) {
        if (blowingTasks.containsKey(uuid)) {
            blowingTasks.get(uuid).cancel();
            blowingTasks.remove(uuid);
        }
    }
    
    public boolean isBlowingBubble(Player player) {
        return blowingTasks.containsKey(player.getUniqueId());
    }
    
    public long calculateBubblesSellValue(BGPlayer bgPlayer) {
        return calculator.calculateBubblesSellValue(bgPlayer);
    }
}
