
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {
    
    private final BubbleGumSimulator plugin;
    private final Map<String, Location> worldSpawns;
    
    public WorldManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.worldSpawns = new HashMap<>();
        loadWorldSpawns();
    }
    
    private void loadWorldSpawns() {
        ConfigurationSection worldsSection = plugin.getConfig().getConfigurationSection("worlds");
        if (worldsSection == null) {
            return;
        }
        
        for (String worldKey : worldsSection.getKeys(false)) {
            // For each world defined in config, check if it exists
            String worldName = "world"; // Default world
            
            // In a real implementation, you'd have custom worlds or specific coordinates per world
            World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                // For now, just use the world spawn location
                worldSpawns.put(worldKey, world.getSpawnLocation());
            }
        }
    }
    
    public boolean teleportToWorld(Player player, String worldKey) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Check if world exists in config
        ConfigurationSection worldConfig = plugin.getConfig().getConfigurationSection("worlds." + worldKey);
        if (worldConfig == null) {
            MessageUtils.sendMessage(player, "&cWorld not found: " + worldKey);
            return false;
        }
        
        // Check if world is enabled
        if (!worldConfig.getBoolean("enabled", true)) {
            MessageUtils.sendMessage(player, "&cThis world is currently disabled.");
            return false;
        }
        
        // Check if player has unlocked the world
        if (!worldKey.equals("starter") && !bgPlayer.isWorldUnlocked(worldKey)) {
            // Check unlock requirements
            ConfigurationSection requirementsConfig = worldConfig.getConfigurationSection("unlock-requirements");
            if (requirementsConfig != null) {
                long requiredCoins = requirementsConfig.getLong("coins", 0);
                double requiredBubbleSize = requirementsConfig.getDouble("bubble-size", 0);
                int requiredPets = requirementsConfig.getInt("required-pets", 0);
                
                // Check if player meets requirements
                if (bgPlayer.getCoins() >= requiredCoins && 
                    bgPlayer.getLargestBubble() >= requiredBubbleSize && 
                    bgPlayer.getPets().size() >= requiredPets) {
                    // Unlock the world
                    bgPlayer.unlockWorld(worldKey);
                } else {
                    // Send requirements message
                    MessageUtils.sendMessage(player, "&cYou haven't unlocked this world yet.");
                    MessageUtils.sendMessage(player, "&cRequirements:");
                    MessageUtils.sendMessage(player, "&c - " + requiredCoins + " coins");
                    MessageUtils.sendMessage(player, "&c - " + requiredBubbleSize + " bubble size");
                    MessageUtils.sendMessage(player, "&c - " + requiredPets + " pets");
                    return false;
                }
            } else {
                MessageUtils.sendMessage(player, "&cYou haven't unlocked this world yet.");
                return false;
            }
        }
        
        // Get world spawn location
        Location spawnLocation = worldSpawns.get(worldKey);
        if (spawnLocation == null) {
            MessageUtils.sendMessage(player, "&cCould not find spawn location for world: " + worldKey);
            return false;
        }
        
        // Teleport player
        player.teleport(spawnLocation);
        
        // Send message
        String message = plugin.getConfig().getString("messages.world.teleport", "&aYou have been teleported to &e{world}&a!");
        message = message.replace("{world}", worldConfig.getString("name", worldKey));
        MessageUtils.sendMessage(player, message);
        
        return true;
    }
    
    public void setWorldSpawn(String worldKey, Location location) {
        worldSpawns.put(worldKey, location);
    }
    
    public Location getWorldSpawn(String worldKey) {
        return worldSpawns.get(worldKey);
    }
    
    public boolean isWorldUnlocked(Player player, String worldKey) {
        if (worldKey.equals("starter")) {
            return true;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        return bgPlayer.isWorldUnlocked(worldKey);
    }
}
