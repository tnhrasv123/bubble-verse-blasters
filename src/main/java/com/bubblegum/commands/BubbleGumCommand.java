
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BubbleGumCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public BubbleGumCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelpMessage(sender);
                break;
            case "reload":
                if (!sender.hasPermission("bubblegum.reload")) {
                    MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
                    return true;
                }
                plugin.getConfigManager().reloadConfig();
                MessageUtils.sendMessage(sender, "&aConfiguration reloaded!");
                break;
            case "give":
                if (!sender.hasPermission("bubblegum.give")) {
                    MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
                    return true;
                }
                handleGiveCommand(sender, args);
                break;
            case "reset":
                if (!sender.hasPermission("bubblegum.reset")) {
                    MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
                    return true;
                }
                handleResetCommand(sender, args);
                break;
            default:
                MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.unknown-command"));
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        MessageUtils.sendMessage(sender, "&6=== Bubble Gum Simulator Help ===");
        MessageUtils.sendMessage(sender, "&e/bubblegum help &7- Show this help message");
        MessageUtils.sendMessage(sender, "&e/bubble [blow/sell/info] &7- Manage your bubbles");
        MessageUtils.sendMessage(sender, "&e/pets [list/info/equip/unequip] &7- Manage your pets");
        MessageUtils.sendMessage(sender, "&e/eggs [buy/open/list] &7- Manage eggs");
        MessageUtils.sendMessage(sender, "&e/bgsshop &7- Open the shop");
        MessageUtils.sendMessage(sender, "&e/bgsstats &7- View your stats");
        MessageUtils.sendMessage(sender, "&e/bgsworlds &7- Teleport between worlds");
        
        if (sender.hasPermission("bubblegum.admin")) {
            MessageUtils.sendMessage(sender, "&c=== Admin Commands ===");
            MessageUtils.sendMessage(sender, "&e/bubblegum reload &7- Reload plugin configuration");
            MessageUtils.sendMessage(sender, "&e/bubblegum give <player> <item> [amount] &7- Give items to players");
            MessageUtils.sendMessage(sender, "&e/bubblegum reset <player> &7- Reset player data");
        }
    }
    
    private void handleGiveCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            MessageUtils.sendMessage(sender, "&cUsage: /bubblegum give <player> <item> [amount]");
            return;
        }
        
        String playerName = args[1];
        String itemType = args[2].toLowerCase();
        int amount = 1;
        
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount <= 0) {
                    MessageUtils.sendMessage(sender, "&cAmount must be greater than 0");
                    return;
                }
            } catch (NumberFormatException e) {
                MessageUtils.sendMessage(sender, "&cInvalid amount: " + args[3]);
                return;
            }
        }
        
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "&cPlayer not found: " + playerName);
            return;
        }
        
        switch (itemType) {
            case "gum":
                if (args.length < 4) {
                    MessageUtils.sendMessage(sender, "&cUsage: /bubblegum give <player> gum <type> [amount]");
                    return;
                }
                
                String gumType = args[3].toLowerCase();
                if (plugin.getGumManager().giveGumItem(targetPlayer, gumType, amount)) {
                    MessageUtils.sendMessage(sender, "&aGave " + amount + " " + gumType + " gum to " + targetPlayer.getName());
                    MessageUtils.sendMessage(targetPlayer, "&aYou received " + amount + " " + gumType + " gum!");
                } else {
                    MessageUtils.sendMessage(sender, "&cFailed to give gum to player");
                }
                break;
            case "coins":
                plugin.getEconomyManager().addCoins(targetPlayer, amount);
                MessageUtils.sendMessage(sender, "&aGave " + amount + " coins to " + targetPlayer.getName());
                MessageUtils.sendMessage(targetPlayer, "&aYou received " + amount + " coins!");
                break;
            case "gems":
                plugin.getEconomyManager().addGems(targetPlayer, amount);
                MessageUtils.sendMessage(sender, "&aGave " + amount + " gems to " + targetPlayer.getName());
                MessageUtils.sendMessage(targetPlayer, "&aYou received " + amount + " gems!");
                break;
            default:
                MessageUtils.sendMessage(sender, "&cUnknown item type: " + itemType);
                break;
        }
    }
    
    private void handleResetCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "&cUsage: /bubblegum reset <player>");
            return;
        }
        
        String playerName = args[1];
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        
        if (targetPlayer == null) {
            MessageUtils.sendMessage(sender, "&cPlayer not found: " + playerName);
            return;
        }
        
        plugin.getDataManager().resetPlayerData(targetPlayer);
        MessageUtils.sendMessage(sender, "&aReset data for player: " + targetPlayer.getName());
        MessageUtils.sendMessage(targetPlayer, "&aYour Bubble Gum Simulator data has been reset!");
    }
}
