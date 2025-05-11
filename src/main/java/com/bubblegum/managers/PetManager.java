
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.ColorUtils;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PetManager {
    
    private final BubbleGumSimulator plugin;
    private final NamespacedKey petIdKey;
    private final Map<String, List<String>> petTypes;
    private final Random random;
    
    public PetManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.petIdKey = new NamespacedKey(plugin, "pet_id");
        this.petTypes = loadPetTypes();
        this.random = new Random();
    }
    
    private Map<String, List<String>> loadPetTypes() {
        Map<String, List<String>> types = new HashMap<>();
        
        // Define pet types for each rarity - this would typically be loaded from a configuration file
        types.put("common", Arrays.asList("Dog", "Cat", "Rabbit", "Mouse", "Chicken"));
        types.put("uncommon", Arrays.asList("Fox", "Wolf", "Panda", "Parrot", "Turtle"));
        types.put("rare", Arrays.asList("Dragon", "Phoenix", "Unicorn", "Griffin", "Hydra"));
        types.put("epic", Arrays.asList("Robot", "Alien", "Ghost", "Demon", "Angel"));
        types.put("legendary", Arrays.asList("Titan", "Kraken", "Leviathan", "Behemoth", "Chimera"));
        types.put("mythical", Arrays.asList("Chronos", "Zeus", "Poseidon", "Hades", "Athena"));
        
        return types;
    }
    
    public Pet hatchEgg(Player player, String eggType) {
        ConfigurationSection eggConfig = plugin.getConfig().getConfigurationSection("eggs." + eggType);
        if (eggConfig == null) {
            return null;
        }
        
        // Get chances for each rarity
        ConfigurationSection chancesSection = eggConfig.getConfigurationSection("chances");
        if (chancesSection == null) {
            return null;
        }
        
        // Apply luck multiplier from pets
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        double luckMultiplier = bgPlayer.getPetLuckMultiplier();
        
        // Calculate total chance
        double totalChance = 0;
        Map<String, Double> adjustedChances = new HashMap<>();
        
        for (String rarity : chancesSection.getKeys(false)) {
            double chance = chancesSection.getDouble(rarity, 0);
            
            // Adjust chance for better rarities based on luck multiplier
            if (!rarity.equals("common")) {
                chance *= luckMultiplier;
            }
            
            adjustedChances.put(rarity, chance);
            totalChance += chance;
        }
        
        // Normalize chances if total is not 100
        if (totalChance != 100) {
            for (Map.Entry<String, Double> entry : adjustedChances.entrySet()) {
                adjustedChances.put(entry.getKey(), (entry.getValue() / totalChance) * 100);
            }
            totalChance = 100;
        }
        
        // Roll for rarity
        double roll = random.nextDouble() * 100;
        double cumulativeChance = 0;
        String selectedRarity = "common"; // Default
        
        for (Map.Entry<String, Double> entry : adjustedChances.entrySet()) {
            String rarity = entry.getKey();
            double chance = entry.getValue();
            
            cumulativeChance += chance;
            if (roll <= cumulativeChance) {
                selectedRarity = rarity;
                break;
            }
        }
        
        // Get random pet type for the selected rarity
        List<String> availablePetTypes = petTypes.getOrDefault(selectedRarity, Collections.singletonList("Unknown"));
        String petType = availablePetTypes.get(random.nextInt(availablePetTypes.size()));
        
        // Create pet
        Pet pet = new Pet(petType, selectedRarity);
        
        // Add pet to player
        bgPlayer.addPet(pet);
        
        // Send message
        String message = plugin.getConfig().getString("messages.pet.hatched", "&aYou hatched a &e{rarity} {pet}&a!");
        message = message.replace("{rarity}", selectedRarity).replace("{pet}", petType);
        MessageUtils.sendMessage(player, message);
        
        return pet;
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
    
    public Pet getPetById(BGPlayer bgPlayer, String petId) {
        for (Pet pet : bgPlayer.getPets()) {
            if (pet.getId().equals(petId)) {
                return pet;
            }
        }
        return null;
    }
    
    public void equipPet(Player player, Pet pet) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        if (!bgPlayer.getPets().contains(pet)) {
            return;
        }
        
        // Check if already equipped
        if (bgPlayer.getEquippedPets().contains(pet)) {
            MessageUtils.sendMessage(player, "&cThis pet is already equipped!");
            return;
        }
        
        // Check if max equipped pets reached
        int maxPets = 3; // Could be configurable or based on player level
        if (bgPlayer.getEquippedPets().size() >= maxPets) {
            MessageUtils.sendMessage(player, "&cYou can only equip " + maxPets + " pets at a time!");
            return;
        }
        
        // Equip the pet
        bgPlayer.equipPet(pet);
        
        // Send message
        String message = plugin.getConfig().getString("messages.pet.equip", "&aYou equipped &e{pet}&a!");
        message = message.replace("{pet}", pet.getType());
        MessageUtils.sendMessage(player, message);
    }
    
    public void unequipPet(Player player, Pet pet) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        if (!bgPlayer.getEquippedPets().contains(pet)) {
            MessageUtils.sendMessage(player, "&cThis pet is not equipped!");
            return;
        }
        
        // Unequip the pet
        bgPlayer.unequipPet(pet);
        
        // Send message
        String message = plugin.getConfig().getString("messages.pet.unequip", "&aYou unequipped &e{pet}&a!");
        message = message.replace("{pet}", pet.getType());
        MessageUtils.sendMessage(player, message);
    }
    
    public void addPetExperience(Player player, Pet pet, int amount) {
        if (amount <= 0) {
            return;
        }
        
        int oldLevel = pet.getLevel();
        pet.addExperience(amount);
        
        // Check if pet leveled up
        if (pet.getLevel() > oldLevel) {
            String message = plugin.getConfig().getString("messages.pet.level-up", "&aYour &e{pet}&a leveled up to level &e{level}&a!");
            message = message.replace("{pet}", pet.getType()).replace("{level}", String.valueOf(pet.getLevel()));
            MessageUtils.sendMessage(player, message);
        }
    }
}
