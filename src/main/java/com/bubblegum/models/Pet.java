
package com.bubblegum.models;

import java.util.UUID;

public class Pet {
    
    private final String id;
    private final String type;
    private final String rarity;
    private int level;
    private int experience;
    
    public Pet(String type, String rarity) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.rarity = rarity;
        this.level = 1;
        this.experience = 0;
    }
    
    public Pet(String id, String type, String rarity) {
        this.id = id;
        this.type = type;
        this.rarity = rarity;
        this.level = 1;
        this.experience = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
    
    public String getRarity() {
        return rarity;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setExperience(int experience) {
        this.experience = experience;
    }
    
    public void addExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        int requiredExp = level * 50;
        if (experience >= requiredExp) {
            experience -= requiredExp;
            level++;
            checkLevelUp();
        }
    }
    
    public double getBubbleMultiplier() {
        double baseMultiplier = getRarityValue() * 0.05;
        return baseMultiplier + (level - 1) * 0.01;
    }
    
    public double getLuckMultiplier() {
        double baseMultiplier = getRarityValue() * 0.02;
        return baseMultiplier + (level - 1) * 0.005;
    }
    
    private double getRarityValue() {
        switch (rarity.toLowerCase()) {
            case "common":
                return 1.0;
            case "uncommon":
                return 1.5;
            case "rare":
                return 2.0;
            case "epic":
                return 3.0;
            case "legendary":
                return 5.0;
            case "mythical":
                return 10.0;
            case "infinity":
                return 20.0;
            default:
                return 1.0;
        }
    }
}
