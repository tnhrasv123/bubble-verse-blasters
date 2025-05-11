
package com.bubblegum.listeners;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final BubbleGumSimulator plugin;
    
    public PlayerListener(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Load player data
        plugin.getDataManager().loadPlayerData(player);
        
        // Send welcome message
        if (player.hasPlayedBefore()) {
            MessageUtils.sendMessage(player, "&aWelcome back to Bubble Gum Simulator!");
        } else {
            MessageUtils.sendMessage(player, "&aWelcome to Bubble Gum Simulator!");
            MessageUtils.sendMessage(player, "&7Use &f/bubblegum help &7to view available commands.");
            
            // Give starting gum
            plugin.getGumManager().giveGumItem(player, "regular", 1);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Save player data
        plugin.getDataManager().savePlayerData(player);
        
        // Remove player data from memory
        plugin.getDataManager().removePlayerData(player);
    }
}
