
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class WorldsCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public WorldsCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.worlds")) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            listWorlds(player);
            return true;
        }
        
        String worldKey = args[0].toLowerCase();
        plugin.getWorldManager().teleportToWorld(player, worldKey);
        
        return true;
    }
    
    private void listWorlds(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        ConfigurationSection worldsSection = plugin.getConfig().getConfigurationSection("worlds");
        
        if (worldsSection == null) {
            MessageUtils.sendMessage(player, "&cNo worlds are configured.");
            return;
        }
        
        MessageUtils.sendMessage(player, "&6=== Available Worlds ===");
        
        for (String worldKey : worldsSection.getKeys(false)) {
            ConfigurationSection worldConfig = worldsSection.getConfigurationSection(worldKey);
            if (worldConfig == null || !worldConfig.getBoolean("enabled", true)) {
                continue;
            }
            
            String name = worldConfig.getString("name", worldKey);
            String description = worldConfig.getString("description", "");
            boolean unlocked = worldKey.equals("starter") || bgPlayer.isWorldUnlocked(worldKey);
            
            if (unlocked) {
                MessageUtils.sendMessage(player, "&a✓ &f" + name + " &7- " + description);
                MessageUtils.sendMessage(player, "&7  To teleport: &f/bgsworlds " + worldKey);
            } else {
                MessageUtils.sendMessage(player, "&c✗ &f" + name + " &7- " + description + " &c(Locked)");
                
                // Show requirements
                ConfigurationSection requirementsConfig = worldConfig.getConfigurationSection("unlock-requirements");
                if (requirementsConfig != null) {
                    long requiredCoins = requirementsConfig.getLong("coins", 0);
                    double requiredBubbleSize = requirementsConfig.getDouble("bubble-size", 0);
                    int requiredPets = requirementsConfig.getInt("required-pets", 0);
                    
                    MessageUtils.sendMessage(player, "&7  Requirements:");
                    if (requiredCoins > 0) {
                        MessageUtils.sendMessage(player, "&7  - " + plugin.getEconomyManager().formatCoins(requiredCoins) + 
                            (bgPlayer.getCoins() >= requiredCoins ? " &a✓" : " &c✗"));
                    }
                    if (requiredBubbleSize > 0) {
                        MessageUtils.sendMessage(player, "&7  - Bubble Size: &f" + requiredBubbleSize + 
                            (bgPlayer.getLargestBubble() >= requiredBubbleSize ? " &a✓" : " &c✗"));
                    }
                    if (requiredPets > 0) {
                        MessageUtils.sendMessage(player, "&7  - Pets: &f" + requiredPets + 
                            (bgPlayer.getPets().size() >= requiredPets ? " &a✓" : " &c✗"));
                    }
                }
            }
        }
    }
}
