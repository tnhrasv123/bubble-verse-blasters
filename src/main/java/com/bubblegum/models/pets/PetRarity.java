
package com.bubblegum.models.pets;

public enum PetRarity {
    COMMON("common", 1.0, 0.05, 0.02),
    UNCOMMON("uncommon", 1.5, 0.075, 0.03),
    RARE("rare", 2.0, 0.1, 0.04),
    EPIC("epic", 3.0, 0.15, 0.06),
    LEGENDARY("legendary", 5.0, 0.25, 0.1),
    MYTHICAL("mythical", 10.0, 0.5, 0.2),
    INFINITY("infinity", 20.0, 1.0, 0.4);
    
    private final String name;
    private final double value;
    private final double bubbleMultiplierBase;
    private final double luckMultiplierBase;
    
    PetRarity(String name, double value, double bubbleMultiplierBase, double luckMultiplierBase) {
        this.name = name;
        this.value = value;
        this.bubbleMultiplierBase = bubbleMultiplierBase;
        this.luckMultiplierBase = luckMultiplierBase;
    }
    
    public String getName() {
        return name;
    }
    
    public double getValue() {
        return value;
    }
    
    public double getBubbleMultiplierBase() {
        return bubbleMultiplierBase;
    }
    
    public double getLuckMultiplierBase() {
        return luckMultiplierBase;
    }
    
    public static PetRarity fromString(String name) {
        for (PetRarity rarity : values()) {
            if (rarity.name.equalsIgnoreCase(name)) {
                return rarity;
            }
        }
        return COMMON; // Default fallback
    }
}
