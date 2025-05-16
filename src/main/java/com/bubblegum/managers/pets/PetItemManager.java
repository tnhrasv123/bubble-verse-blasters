
package com.bubblegum.managers.pets;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PetItemManager {
    
    private final BubbleGumSimulator plugin;
    private final NamespacedKey petIdKey;
    
    public PetItemManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.petIdKey = new NamespacedKey(plugin, "pet_id");
    }
    
    public ItemStack createPetItem(Pet pet) {
        Material material;
        
        // Choose material based on rarity
        switch (pet.getRarity().toLowerCase()) {
            case "common":
                material = Material.BONE;
                break;
            case "uncommon":
                material = Material.RABBIT_FOOT;
                break;
            case "rare":
                material = Material.BLAZE_ROD;
                break;
            case "epic":
                material = Material.GHAST_TEAR;
                break;
            case "legendary":
                material = Material.NETHER_STAR;
                break;
            case "mythical":
                material = Material.END_CRYSTAL;
                break;
            default:
                material = Material.FEATHER;
                break;
        }
        
        // Create item
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta == null) {
            return item;
        }
        
        // Set display name with color based on rarity
        String displayName = getRarityColor(pet.getRarity()) + pet.getType() + " Pet";
        meta.setDisplayName(ColorUtils.colorize(displayName));
        
        // Set lore
        List<String> lore = new ArrayList<>();
        lore.add(ColorUtils.colorize("&7Rarity: " + getRarityColor(pet.getRarity()) + pet.getRarity()));
        lore.add(ColorUtils.colorize("&7Level: &f" + pet.getLevel()));
        lore.add(ColorUtils.colorize("&7Bubble Multiplier: &f" + String.format("%.2f", pet.getBubbleMultiplier()) + "x"));
        lore.add(ColorUtils.colorize("&7Luck Multiplier: &f" + String.format("%.2f", pet.getLuckMultiplier()) + "x"));
        lore.add("");
        lore.add(ColorUtils.colorize("&eRight-click to equip/unequip"));
        meta.setLore(lore);
        
        // Add item flags
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        
        // Store pet ID in persistent data
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(petIdKey, PersistentDataType.STRING, pet.getId());
        
        item.setItemMeta(meta);
        return item;
    }
    
    public boolean isPetItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(petIdKey, PersistentDataType.STRING);
    }
    
    public String getPetId(ItemStack item) {
        if (!isPetItem(item)) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(petIdKey, PersistentDataType.STRING);
    }
    
    public String getRarityColor(String rarity) {
        switch (rarity.toLowerCase()) {
            case "common":
                return "&f";
            case "uncommon":
                return "&a";
            case "rare":
                return "&9";
            case "epic":
                return "&5";
            case "legendary":
                return "&6";
            case "mythical":
                return "&d";
            default:
                return "&7";
        }
    }
}
