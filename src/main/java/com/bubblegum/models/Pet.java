
package com.bubblegum.models;

import com.bubblegum.models.pets.PetExperience;
import com.bubblegum.models.pets.PetRarity;

import java.util.UUID;

public class Pet {
    
    private final String id;
    private final String type;
    private final PetRarity rarity;
    private final PetExperience experience;
    
    public Pet(String type, String rarityName) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.rarity = PetRarity.fromString(rarityName);
        this.experience = new PetExperience();
    }
    
    public Pet(String id, String type, String rarityName) {
        this.id = id;
        this.type = type;
        this.rarity = PetRarity.fromString(rarityName);
        this.experience = new PetExperience();
    }
    
    public Pet(String id, String type, String rarityName, int level, int exp) {
        this.id = id;
        this.type = type;
        this.rarity = PetRarity.fromString(rarityName);
        this.experience = new PetExperience(level, exp);
    }
    
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getRarity() {
        return rarity.getName();
    }
    
    public int getLevel() {
        return experience.getLevel();
    }
    
    public void setLevel(int level) {
        experience.setLevel(level);
    }
    
    public int getExperience() {
        return experience.getExperience();
    }
    
    public void setExperience(int exp) {
        experience.setExperience(exp);
    }
    
    public void addExperience(int amount) {
        experience.addExperience(amount);
    }
    
    /**
     * Get the required experience for the current level
     * @return Required experience for current level
     */
    public int getRequiredExperience() {
        return experience.getRequiredExperience();
    }
    
    /**
     * Get the multiplier for bubble production based on pet rarity and level
     * @return The bubble multiplier value
     */
    public double getBubbleMultiplier() {
        double baseMultiplier = rarity.getBubbleMultiplierBase();
        return baseMultiplier + (getLevel() - 1) * 0.01;
    }
    
    /**
     * Get the multiplier for luck based on pet rarity and level
     * @return The luck multiplier value
     */
    public double getLuckMultiplier() {
        double baseMultiplier = rarity.getLuckMultiplierBase();
        return baseMultiplier + (getLevel() - 1) * 0.005;
    }
}
