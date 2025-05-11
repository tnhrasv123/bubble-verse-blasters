
package com.bubblegum.listeners;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class BubbleListener implements Listener {
    
    private final BubbleGumSimulator plugin;
    
    public BubbleListener(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        // Check if player is blowing a bubble
        if (plugin.getBubbleManager().isBlowingBubble(player)) {
            // If player interacts, stop blowing bubble
            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                plugin.getBubbleManager().cancelBlowingBubble(player);
            } else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                plugin.getBubbleManager().finishBlowingBubble(player);
            }
            return;
        }
        
        // Check if player is using gum
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && plugin.getGumManager().isGumItem(event.getItem())) {
                // Get gum type
                String gumType = plugin.getGumManager().getGumType(event.getItem());
                if (gumType != null) {
                    // Set player's gum type
                    BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
                    bgPlayer.setCurrentGumType(gumType);
                    
                    // Start blowing bubble
                    plugin.getBubbleManager().startBlowingBubble(player);
                    
                    // Prevent default action
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // If player changes item, cancel bubble blowing
        if (plugin.getBubbleManager().isBlowingBubble(player)) {
            plugin.getBubbleManager().cancelBlowingBubble(player);
        }
    }
}
