
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public StatsCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.stats")) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        if (args.length > 0 && player.hasPermission("bubblegum.stats.others")) {
            Player targetPlayer = plugin.getServer().getPlayer(args[0]);
            if (targetPlayer != null) {
                showStats(player, targetPlayer);
                return true;
            } else {
                MessageUtils.sendMessage(player, "&cPlayer not found: " + args[0]);
            }
        }
        
        showStats(player, player);
        
        return true;
    }
    
    private void showStats(Player viewer, Player target) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(target);
        
        MessageUtils.sendMessage(viewer, "&6=== " + target.getName() + "'s Stats ===");
        MessageUtils.sendMessage(viewer, "&7Level: &f" + bgPlayer.getLevel());
        MessageUtils.sendMessage(viewer, "&7Experience: &f" + bgPlayer.getExperience() + "&7/&f" + (bgPlayer.getLevel() * 100));
        MessageUtils.sendMessage(viewer, "&7Coins: &f" + plugin.getEconomyManager().formatCoins(bgPlayer.getCoins()));
        MessageUtils.sendMessage(viewer, "&7Gems: &f" + plugin.getEconomyManager().formatGems(bgPlayer.getGems()));
        MessageUtils.sendMessage(viewer, "&7Total Coins Earned: &f" + plugin.getEconomyManager().formatCoins(bgPlayer.getTotalCoinsEarned()));
        MessageUtils.sendMessage(viewer, "&7Total Gems Earned: &f" + plugin.getEconomyManager().formatGems(bgPlayer.getTotalGemsEarned()));
        MessageUtils.sendMessage(viewer, "");
        MessageUtils.sendMessage(viewer, "&7Current Gum Type: &f" + bgPlayer.getCurrentGumType());
        MessageUtils.sendMessage(viewer, "&7Largest Bubble: &f" + String.format("%.2f", bgPlayer.getLargestBubble()));
        MessageUtils.sendMessage(viewer, "&7Total Bubbles Blown: &f" + bgPlayer.getTotalBubblesBlown());
        MessageUtils.sendMessage(viewer, "&7Backpack: &f" + bgPlayer.getBackpack().getUsed() + "&7/&f" + bgPlayer.getBackpack().getCapacity());
        MessageUtils.sendMessage(viewer, "&7Pets: &f" + bgPlayer.getPets().size() + " &7(&f" + bgPlayer.getEquippedPets().size() + " &7equipped)");
        
        // Show Gum Mastery
        MessageUtils.sendMessage(viewer, "");
        MessageUtils.sendMessage(viewer, "&6=== Gum Mastery ===");
        for (String gumType : bgPlayer.getCurrentGumType().equals("regular") ? new String[]{"regular"} : new String[]{bgPlayer.getCurrentGumType(), "regular"}) {
            int masteryLevel = bgPlayer.getGumMastery(gumType);
            MessageUtils.sendMessage(viewer, "&7" + gumType + ": &f" + masteryLevel);
        }
        
        // Show Unlocked Worlds
        MessageUtils.sendMessage(viewer, "");
        MessageUtils.sendMessage(viewer, "&6=== Worlds ===");
        for (String worldKey : new String[]{"starter", "candyland", "space", "atlantis", "void"}) {
            boolean unlocked = worldKey.equals("starter") || bgPlayer.isWorldUnlocked(worldKey);
            MessageUtils.sendMessage(viewer, (unlocked ? "&a✓ " : "&c✗ ") + plugin.getConfig().getString("worlds." + worldKey + ".name", worldKey));
        }
    }
}
