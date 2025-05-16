
package com.bubblegum.managers.pets;

import com.bubblegum.BubbleGumSimulator;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class PetTypeManager {
    
    private final BubbleGumSimulator plugin;
    private final Map<String, List<String>> petTypes;
    
    public PetTypeManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.petTypes = loadPetTypes();
    }
    
    private Map<String, List<String>> loadPetTypes() {
        Map<String, List<String>> types = new HashMap<>();
        
        ConfigurationSection petsSection = plugin.getConfig().getConfigurationSection("pet-types");
        if (petsSection != null) {
            for (String rarity : petsSection.getKeys(false)) {
                types.put(rarity, petsSection.getStringList(rarity));
            }
        } else {
            // Default pet types if not found in config
            types.put("common", Arrays.asList("Dog", "Cat", "Rabbit", "Mouse", "Chicken"));
            types.put("uncommon", Arrays.asList("Fox", "Wolf", "Panda", "Parrot", "Turtle"));
            types.put("rare", Arrays.asList("Dragon", "Phoenix", "Unicorn", "Griffin", "Hydra"));
            types.put("epic", Arrays.asList("Robot", "Alien", "Ghost", "Demon", "Angel"));
            types.put("legendary", Arrays.asList("Titan", "Kraken", "Leviathan", "Behemoth", "Chimera"));
            types.put("mythical", Arrays.asList("Chronos", "Zeus", "Poseidon", "Hades", "Athena"));
        }
        
        return types;
    }
    
    public List<String> getPetTypesForRarity(String rarity) {
        return petTypes.getOrDefault(rarity, Collections.singletonList("Unknown"));
    }
    
    public Map<String, List<String>> getAllPetTypes() {
        return petTypes;
    }
}
