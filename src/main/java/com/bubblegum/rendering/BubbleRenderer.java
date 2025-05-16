
package com.bubblegum.rendering;

import com.bubblegum.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BubbleRenderer {
    
    /**
     * Displays a bubble particle effect around the player
     * 
     * @param player The player who is blowing the bubble
     * @param size The size of the bubble
     * @param gumType The type of gum being used
     */
    public static void displayBubble(Player player, double size, String gumType) {
        // Get bubble color
        String colorStr = player.getServer().getPluginManager()
                .getPlugin("BubbleGumSimulator").getConfig()
                .getString("gum-types." + gumType + ".color", "WHITE");
        
        Particle.DustOptions dustOptions = ColorUtils.getDustOptions(colorStr, (float) (size * 0.2));
        
        // Calculate display position (in front of player's face)
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(0.5));
        
        // Spawn particles
        player.getWorld().spawnParticle(Particle.REDSTONE, loc, 10, size * 0.05, size * 0.05, size * 0.05, 0, dustOptions);
    }
}
