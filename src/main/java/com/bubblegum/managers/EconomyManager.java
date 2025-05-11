
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import org.bukkit.entity.Player;

public class EconomyManager {
    
    private final BubbleGumSimulator plugin;
    private final String coinSymbol;
    private final String gemSymbol;
    
    public EconomyManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.coinSymbol = plugin.getConfig().getString("economy.coin-symbol", "$");
        this.gemSymbol = plugin.getConfig().getString("economy.gem-symbol", "⬙");
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
        bgPlayer.addCoins(amount);
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
        bgPlayer.addGems(amount);
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
        return coinSymbol + amount;
    }
    
    public String formatGems(long amount) {
        return amount + " " + gemSymbol;
    }
}
