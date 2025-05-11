
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GumManager {
    
    private final BubbleGumSimulator plugin;
    private final NamespacedKey gumTypeKey;
    
    public GumManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.gumTypeKey = new NamespacedKey(plugin, "gum_type");
    }
    
    public ItemStack createGumItem(String gumType) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
        if (config == null) {
            return null;
        }
        
        String name = config.getString("name", "Unknown Gum");
        String colorName = config.getString("color", "WHITE");
        
        // Create item
        ItemStack item = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            return item;
        }
        
        // Set display name with color
        meta.setDisplayName(ColorUtils.getChatColor(colorName) + name);
        
        // Set lore
        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.colorize("&7Growth Rate: &f" + config.getDouble("growth-rate", 1.0) + "x"));
        lore.add(ColorUtils.colorize("&7Max Size: &f" + config.getDouble("max-size", 10.0)));
        lore.add(ColorUtils.colorize("&7Value Multiplier: &f" + config.getDouble("value-multiplier", 1.0) + "x"));
        lore.add("");
        lore.add(ColorUtils.colorize("&eRight-click to blow bubbles"));
        meta.setLore(lore);
        
        // Add glow effect
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        
        // Store gum type in persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(gumTypeKey, PersistentDataType.STRING, gumType);
        
        item.setItemMeta(meta);
        return item;
    }
    
    public boolean isGumItem(ItemStack item) {
        if (item == null || item.getType() != Material.SLIME_BALL) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(gumTypeKey, PersistentDataType.STRING);
    }
    
    public String getGumType(ItemStack item) {
        if (!isGumItem(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(gumTypeKey, PersistentDataType.STRING);
    }
    
    public boolean giveGumItem(Player player, String gumType, int amount) {
        // Check if gum type exists
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
        if (config == null) {
            return false;
        }
        
        // Check if player has required level
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        int requiredLevel = config.getInt("required-level", 0);
        if (bgPlayer.getLevel() < requiredLevel) {
            return false;
        }
        
        // Create gum item
        ItemStack gumItem = createGumItem(gumType);
        if (gumItem == null) {
            return false;
        }
        
        gumItem.setAmount(amount);
        
        // Add to inventory
        player.getInventory().addItem(gumItem);
        return true;
    }
    
    public boolean setPlayerGumType(Player player, String gumType) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("gum-types." + gumType);
        if (config == null) {
            return false;
        }
        
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Check if player has required level
        int requiredLevel = config.getInt("required-level", 0);
        if (bgPlayer.getLevel() < requiredLevel) {
            return false;
        }
        
        // Set gum type
        bgPlayer.setCurrentGumType(gumType);
        return true;
    }
}
