
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {
    
    private final BubbleGumSimulator plugin;
    private final String coinSymbol;
    private final String gemSymbol;
    private final Map<UUID, Double> playerCoinMultipliers;
    private final Map<UUID, Double> playerGemMultipliers;
    private final NumberFormat numberFormat;
    
    public EconomyManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.coinSymbol = plugin.getConfig().getString("economy.coin-symbol", "$");
        this.gemSymbol = plugin.getConfig().getString("economy.gem-symbol", "⬙");
        this.playerCoinMultipliers = new HashMap<>();
        this.playerGemMultipliers = new HashMap<>();
        this.numberFormat = NumberFormat.getInstance(Locale.US);
    }
    
    public long getCoins(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.getCoins();
    }
    
    public void addCoins(Player player, long amount) {
        if (amount <= 0) {
            return;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Apply multiplier if exists
        double multiplier = getPlayerCoinMultiplier(player);
        long finalAmount = Math.round(amount * multiplier);
        
        bgPlayer.addCoins(finalAmount);
        
        // Notify player if multiplier was active
        if (multiplier > 1.0) {
            MessageUtils.sendMessage(player, 
                    "&a+" + formatCoins(finalAmount) + " &7(" + formatMultiplier(multiplier) + " multiplier)");
        }
    }
    
    public boolean removeCoins(Player player, long amount) {
        if (amount <= 0) {
            return true;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.removeCoins(amount);
    }
    
    public long getGems(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.getGems();
    }
    
    public void addGems(Player player, long amount) {
        if (amount <= 0) {
            return;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Apply multiplier if exists
        double multiplier = getPlayerGemMultiplier(player);
        long finalAmount = Math.round(amount * multiplier);
        
        bgPlayer.addGems(finalAmount);
        
        // Notify player if multiplier was active
        if (multiplier > 1.0) {
            MessageUtils.sendMessage(player, 
                    "&b+" + formatGems(finalAmount) + " &7(" + formatMultiplier(multiplier) + " multiplier)");
        }
    }
    
    public boolean removeGems(Player player, long amount) {
        if (amount <= 0) {
            return true;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.removeGems(amount);
    }
    
    public boolean canAffordCoins(Player player, long amount) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.getCoins() >= amount;
    }
    
    public boolean canAffordGems(Player player, long amount) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.getGems() >= amount;
    }
    
    public String formatCoins(long amount) {
        return coinSymbol + formatNumber(amount);
    }
    
    public String formatGems(long amount) {
        return formatNumber(amount) + " " + gemSymbol;
    }
    
    public String formatMultiplier(double multiplier) {
        return String.format("%.1fx", multiplier);
    }
    
    public String formatNumber(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else {
            return String.format("%.1fB", number / 1_000_000_000.0);
        }
    }
    
    // Currency multiplier methods
    public void setPlayerCoinMultiplier(Player player, double multiplier, int durationSeconds) {
        UUID playerUUID = player.getUniqueId();
        playerCoinMultipliers.put(playerUUID, multiplier);
        
        // Schedule removal of multiplier
        if (durationSeconds > 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin, 
                    () -> playerCoinMultipliers.remove(playerUUID), 
                    durationSeconds * 20L);
        }
    }
    
    public void setPlayerGemMultiplier(Player player, double multiplier, int durationSeconds) {
        UUID playerUUID = player.getUniqueId();
        playerGemMultipliers.put(playerUUID, multiplier);
        
        // Schedule removal of multiplier
        if (durationSeconds > 0) {
            plugin.getServer().getScheduler().runTaskLater(plugin, 
                    () -> playerGemMultipliers.remove(playerUUID), 
                    durationSeconds * 20L);
        }
    }
    
    public double getPlayerCoinMultiplier(Player player) {
        return playerCoinMultipliers.getOrDefault(player.getUniqueId(), 1.0);
    }
    
    public double getPlayerGemMultiplier(Player player) {
        return playerGemMultipliers.getOrDefault(player.getUniqueId(), 1.0);
    }
    
    public void removeAllMultipliers(Player player) {
        playerCoinMultipliers.remove(player.getUniqueId());
        playerGemMultipliers.remove(player.getUniqueId());
    }
    
    public boolean hasActiveCoinMultiplier(Player player) {
        return playerCoinMultipliers.containsKey(player.getUniqueId());
    }
    
    public boolean hasActiveGemMultiplier(Player player) {
        return playerGemMultipliers.containsKey(player.getUniqueId());
    }
}
