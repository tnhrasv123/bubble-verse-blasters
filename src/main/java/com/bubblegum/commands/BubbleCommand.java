
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Bubble;
import com.bubblegum.models.Backpack;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BubbleCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public BubbleCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.bubble")) {
            MessageUtils.sendMessage(player, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "blow":
                if (plugin.getBubbleManager().isBlowingBubble(player)) {
                    plugin.getBubbleManager().cancelBlowingBubble(player);
                } else {
                    plugin.getBubbleManager().startBlowingBubble(player);
                }
                break;
            case "sell":
                plugin.getBubbleManager().sellBubbles(player);
                break;
            case "info":
                showBubbleInfo(player);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, "&6=== Bubble Commands ===");
        MessageUtils.sendMessage(player, "&e/bubble blow &7- Start/stop blowing a bubble");
        MessageUtils.sendMessage(player, "&e/bubble sell &7- Sell all bubbles in your backpack");
        MessageUtils.sendMessage(player, "&e/bubble info &7- Show information about your bubbles");
    }
    
    private void showBubbleInfo(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        Backpack backpack = bgPlayer.getBackpack();
        
        MessageUtils.sendMessage(player, "&6=== Bubble Info ===");
        MessageUtils.sendMessage(player, "&7Current Gum Type: &f" + bgPlayer.getCurrentGumType());
        MessageUtils.sendMessage(player, "&7Current Bubble Size: &f" + String.format("%.2f", bgPlayer.getCurrentBubbleSize()));
        MessageUtils.sendMessage(player, "&7Largest Bubble: &f" + String.format("%.2f", bgPlayer.getLargestBubble()));
        MessageUtils.sendMessage(player, "&7Total Bubbles Blown: &f" + bgPlayer.getTotalBubblesBlown());
        MessageUtils.sendMessage(player, "&7Backpack: &f" + backpack.getUsed() + "&7/&f" + backpack.getCapacity());
        
        if (!backpack.getBubbles().isEmpty()) {
            MessageUtils.sendMessage(player, "&6=== Bubbles in Backpack ===");
            
            for (Bubble bubble : backpack.getBubbles()) {
                MessageUtils.sendMessage(player, "&7- &f" + bubble.getGumType() + " &7bubble of size &f" + 
                                       String.format("%.2f", bubble.getSize()) + " &7worth &f" + bubble.calculateValue() + " coins");
            }
            
            MessageUtils.sendMessage(player, "&7Total Value: &f" + backpack.calculateTotalValue() + " coins");
        }
    }
}
