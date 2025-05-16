
package com.bubblegum.managers.pets;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PetHatchingManager {
    
    private final BubbleGumSimulator plugin;
    private final PetTypeManager petTypeManager;
    private final Random random;
    
    public PetHatchingManager(BubbleGumSimulator plugin, PetTypeManager petTypeManager) {
        this.plugin = plugin;
        this.petTypeManager = petTypeManager;
        this.random = new Random();
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
        List<String> availablePetTypes = petTypeManager.getPetTypesForRarity(selectedRarity);
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
}
