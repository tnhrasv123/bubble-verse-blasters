
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class EggsCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public EggsCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.eggs")) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listEggs(player);
                break;
            case "buy":
                if (args.length < 2) {
                    MessageUtils.sendMessage(player, "&cUsage: /eggs buy <egg-type>");
                    return true;
                }
                buyEgg(player, args[1]);
                break;
            case "open":
                if (args.length < 2) {
                    MessageUtils.sendMessage(player, "&cUsage: /eggs open <egg-type>");
                    return true;
                }
                openEgg(player, args[1]);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, "&6=== Egg Commands ===");
        MessageUtils.sendMessage(player, "&e/eggs list &7- List available eggs");
        MessageUtils.sendMessage(player, "&e/eggs buy <egg-type> &7- Buy an egg");
        MessageUtils.sendMessage(player, "&e/eggs open <egg-type> &7- Open an egg you own");
    }
    
    private void listEggs(Player player) {
        ConfigurationSection eggsSection = plugin.getConfig().getConfigurationSection("eggs");
        if (eggsSection == null) {
            MessageUtils.sendMessage(player, "&cNo eggs are currently available.");
            return;
        }
        
        MessageUtils.sendMessage(player, "&6=== Available Eggs ===");
        
        for (String eggType : eggsSection.getKeys(false)) {
            ConfigurationSection eggConfig = eggsSection.getConfigurationSection(eggType);
            if (eggConfig != null) {
                String name = eggConfig.getString("name", eggType);
                long coinCost = eggConfig.getLong("cost.coins", 0);
                long gemCost = eggConfig.getLong("cost.gems", 0);
                
                String costString = "";
                if (coinCost > 0) {
                    costString += plugin.getEconomyManager().formatCoins(coinCost);
                }
                if (gemCost > 0) {
                    if (!costString.isEmpty()) {
                        costString += " & ";
                    }
                    costString += plugin.getEconomyManager().formatGems(gemCost);
                }
                
                MessageUtils.sendMessage(player, "&7- &f" + name + " &7- Cost: &f" + costString);
                
                // Show drop chances
                ConfigurationSection chancesSection = eggConfig.getConfigurationSection("chances");
                if (chancesSection != null) {
                    StringBuilder chancesString = new StringBuilder("  &7Chances: ");
                    for (String rarity : chancesSection.getKeys(false)) {
                        double chance = chancesSection.getDouble(rarity, 0);
                        if (chance > 0) {
                            chancesString.append(plugin.getPetManager().getRarityColor(rarity))
                                         .append(rarity)
                                         .append(" &7(")
                                         .append(String.format("%.1f", chance))
                                         .append("%) ");
                        }
                    }
                    MessageUtils.sendMessage(player, chancesString.toString());
                }
            }
        }
        
        MessageUtils.sendMessage(player, "&7Use &f/eggs buy <egg-type> &7to purchase an egg");
    }
    
    private void buyEgg(Player player, String eggType) {
        ConfigurationSection eggConfig = plugin.getConfig().getConfigurationSection("eggs." + eggType);
        if (eggConfig == null) {
            MessageUtils.sendMessage(player, "&cEgg type not found: " + eggType);
            return;
        }
        
        String name = eggConfig.getString("name", eggType);
        long coinCost = eggConfig.getLong("cost.coins", 0);
        long gemCost = eggConfig.getLong("cost.gems", 0);
        
        // Check if player can afford the egg
        if (coinCost > 0 && !plugin.getEconomyManager().canAffordCoins(player, coinCost)) {
            String message = plugin.getConfig().getString("messages.shop.insufficient-funds", "&cYou don't have enough {currency} to buy this!");
            message = message.replace("{currency}", "coins");
            MessageUtils.sendMessage(player, message);
            return;
        }
        
        if (gemCost > 0 && !plugin.getEconomyManager().canAffordGems(player, gemCost)) {
            String message = plugin.getConfig().getString("messages.shop.insufficient-funds", "&cYou don't have enough {currency} to buy this!");
            message = message.replace("{currency}", "gems");
            MessageUtils.sendMessage(player, message);
            return;
        }
        
        // Charge player
        if (coinCost > 0) {
            plugin.getEconomyManager().removeCoins(player, coinCost);
        }
        
        if (gemCost > 0) {
            plugin.getEconomyManager().removeGems(player, gemCost);
        }
        
        // Open the egg immediately
        openEgg(player, eggType);
        
        // Send purchase message
        String message = plugin.getConfig().getString("messages.shop.purchase", "&aYou purchased &e{item}&a for &e{price}&a!");
        
        String costString = "";
        if (coinCost > 0) {
            costString += plugin.getEconomyManager().formatCoins(coinCost);
        }
        if (gemCost > 0) {
            if (!costString.isEmpty()) {
                costString += " & ";
            }
            costString += plugin.getEconomyManager().formatGems(gemCost);
        }
        
        message = message.replace("{item}", name).replace("{price}", costString);
        MessageUtils.sendMessage(player, message);
    }
    
    private void openEgg(Player player, String eggType) {
        // In a full implementation, eggs would be inventory items that players can save
        // For this simplified version, we'll just hatch the egg immediately
        plugin.getPetManager().hatchEgg(player, eggType);
    }
}
