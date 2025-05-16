
package com.bubblegum.models.pets;

public class PetExperience {
    private int level;
    private int experience;
    
    public PetExperience() {
        this.level = 1;
        this.experience = 0;
    }
    
    public PetExperience(int level, int experience) {
        this.level = Math.max(1, level); // Ensure level is at least 1
        this.experience = Math.max(0, experience); // Ensure experience is not negative
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getExperience() {
        return experience;
    }
    
    public void setLevel(int level) {
        this.level = Math.max(1, level); // Ensure level is at least 1
    }
    
    public void setExperience(int experience) {
        this.experience = Math.max(0, experience); // Ensure experience is not negative
    }
    
    /**
     * Add experience to the pet and handle level ups
     * @param amount Amount of experience to add
     * @return Number of levels gained
     */
    public int addExperience(int amount) {
        if (amount <= 0) {
            return 0;
        }
        
        int oldLevel = level;
        this.experience += amount;
        
        // Process level ups
        checkLevelUp();
        
        // Return the number of levels gained
        return level - oldLevel;
    }
    
    /**
     * Calculate required experience for a specific level
     * @param level The level to calculate required experience for
     * @return The amount of experience required
     */
    public static int getRequiredExperience(int level) {
        return level * 50;
    }
    
    /**
     * Get the required experience for the current level
     * @return Required experience for the current level
     */
    public int getRequiredExperience() {
        return getRequiredExperience(level);
    }
    
    /**
     * Check if the pet has enough experience to level up
     * Optimized to handle multiple level ups in a single call
     */
    private void checkLevelUp() {
        // Calculate how many levels can be gained with the current experience
        int requiredExp;
        int totalLevelsGained = 0;
        
        // Process level ups until there's not enough experience
        while (experience >= (requiredExp = getRequiredExperience(level))) {
            experience -= requiredExp;
            level++;
            totalLevelsGained++;
            
            // Safety check to prevent infinite loops (just in case)
            if (totalLevelsGained > 1000) {
                break;
            }
        }
    }
}
