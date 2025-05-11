
package com.bubblegum.models;

import com.bubblegum.BubbleGumSimulator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BGPlayer {
    
    private final Player player;
    private long coins;
    private long gems;
    private int level;
    private int experience;
    private double currentBubbleSize;
    private double largestBubble;
    private int totalBubblesBlown;
    private int totalCoinsEarned;
    private int totalGemsEarned;
    private String currentGumType;
    private final Backpack backpack;
    private final List<Pet> pets;
    private final List<Pet> equippedPets;
    private final Map<String, Integer> gumMastery;
    private final Map<String, Boolean> unlockedWorlds;
    private final Map<String, Integer> achievements;
    
    public BGPlayer(Player player) {
        this.player = player;
        
        // Initialize with default values from config
        FileConfiguration config = BubbleGumSimulator.getInstance().getConfig();
        this.coins = config.getLong("economy.starting-coins", 500);
        this.gems = config.getLong("economy.starting-gems", 0);
        this.level = 1;
        this.experience = 0;
        this.currentBubbleSize = 0;
        this.largestBubble = 0;
        this.totalBubblesBlown = 0;
        this.totalCoinsEarned = 0;
        this.totalGemsEarned = 0;
        this.currentGumType = "regular";
        this.backpack = new Backpack("basic", config.getInt("backpacks.basic.capacity", 10));
        this.pets = new ArrayList<>();
        this.equippedPets = new ArrayList<>();
        this.gumMastery = new HashMap<>();
        this.unlockedWorlds = new HashMap<>();
        this.achievements = new HashMap<>();
        
        // Initialize gum mastery for all types
        ConfigurationSection gumTypesSection = config.getConfigurationSection("gum-types");
        if (gumTypesSection != null) {
            for (String gumType : gumTypesSection.getKeys(false)) {
                gumMastery.put(gumType, 0);
            }
        }
        
        // Initialize world unlocks
        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String worldKey : worldsSection.getKeys(false)) {
                if (worldKey.equals("starter")) {
                    unlockedWorlds.put(worldKey, true);
                } else {
                    unlockedWorlds.put(worldKey, false);
                }
            }
        }
    }
    
    public static BGPlayer fromConfig(Player player, FileConfiguration config) {
        BGPlayer bgPlayer = new BGPlayer(player);
        
        if (config == null) {
            return bgPlayer;
        }
        
        // Load player data from config
        bgPlayer.coins = config.getLong("coins", bgPlayer.coins);
        bgPlayer.gems = config.getLong("gems", bgPlayer.gems);
        bgPlayer.level = config.getInt("level", bgPlayer.level);
        bgPlayer.experience = config.getInt("experience", bgPlayer.experience);
        bgPlayer.currentBubbleSize = config.getDouble("current-bubble-size", bgPlayer.currentBubbleSize);
        bgPlayer.largestBubble = config.getDouble("largest-bubble", bgPlayer.largestBubble);
        bgPlayer.totalBubblesBlown = config.getInt("total-bubbles-blown", bgPlayer.totalBubblesBlown);
        bgPlayer.totalCoinsEarned = config.getInt("total-coins-earned", bgPlayer.totalCoinsEarned);
        bgPlayer.totalGemsEarned = config.getInt("total-gems-earned", bgPlayer.totalGemsEarned);
        bgPlayer.currentGumType = config.getString("current-gum-type", bgPlayer.currentGumType);
        
        // Load backpack
        ConfigurationSection backpackSection = config.getConfigurationSection("backpack");
        if (backpackSection != null) {
            String backpackType = backpackSection.getString("type", "basic");
            int capacity = backpackSection.getInt("capacity", 10);
            int used = backpackSection.getInt("used", 0);
            
            bgPlayer.backpack.setType(backpackType);
            bgPlayer.backpack.setCapacity(capacity);
            
            // Load bubbles in backpack
            ConfigurationSection bubblesSection = backpackSection.getConfigurationSection("bubbles");
            if (bubblesSection != null) {
                for (String key : bubblesSection.getKeys(false)) {
                    ConfigurationSection bubbleSection = bubblesSection.getConfigurationSection(key);
                    if (bubbleSection != null) {
                        double size = bubbleSection.getDouble("size");
                        String gumType = bubbleSection.getString("gum-type");
                        bgPlayer.backpack.addBubble(new Bubble(size, gumType));
                    }
                }
            }
        }
        
        // Load pets
        ConfigurationSection petsSection = config.getConfigurationSection("pets");
        if (petsSection != null) {
            for (String key : petsSection.getKeys(false)) {
                ConfigurationSection petSection = petsSection.getConfigurationSection(key);
                if (petSection != null) {
                    String petId = petSection.getString("id");
                    String petType = petSection.getString("type");
                    String petRarity = petSection.getString("rarity");
                    int petLevel = petSection.getInt("level");
                    int petExperience = petSection.getInt("experience");
                    boolean equipped = petSection.getBoolean("equipped");
                    
                    Pet pet = new Pet(petId, petType, petRarity);
                    pet.setLevel(petLevel);
                    pet.setExperience(petExperience);
                    
                    bgPlayer.pets.add(pet);
                    
                    if (equipped) {
                        bgPlayer.equippedPets.add(pet);
                    }
                }
            }
        }
        
        // Load gum mastery
        ConfigurationSection gumMasterySection = config.getConfigurationSection("gum-mastery");
        if (gumMasterySection != null) {
            for (String gumType : gumMasterySection.getKeys(false)) {
                int masteryLevel = gumMasterySection.getInt(gumType);
                bgPlayer.gumMastery.put(gumType, masteryLevel);
            }
        }
        
        // Load unlocked worlds
        ConfigurationSection unlockedWorldsSection = config.getConfigurationSection("unlocked-worlds");
        if (unlockedWorldsSection != null) {
            for (String worldKey : unlockedWorldsSection.getKeys(false)) {
                boolean unlocked = unlockedWorldsSection.getBoolean(worldKey);
                bgPlayer.unlockedWorlds.put(worldKey, unlocked);
            }
        }
        
        // Load achievements
        ConfigurationSection achievementsSection = config.getConfigurationSection("achievements");
        if (achievementsSection != null) {
            for (String achievementKey : achievementsSection.getKeys(false)) {
                int progress = achievementsSection.getInt(achievementKey);
                bgPlayer.achievements.put(achievementKey, progress);
            }
        }
        
        return bgPlayer;
    }
    
    public void saveToConfig(FileConfiguration config) {
        // Save basic player data
        config.set("coins", coins);
        config.set("gems", gems);
        config.set("level", level);
        config.set("experience", experience);
        config.set("current-bubble-size", currentBubbleSize);
        config.set("largest-bubble", largestBubble);
        config.set("total-bubbles-blown", totalBubblesBlown);
        config.set("total-coins-earned", totalCoinsEarned);
        config.set("total-gems-earned", totalGemsEarned);
        config.set("current-gum-type", currentGumType);
        
        // Save backpack
        ConfigurationSection backpackSection = config.createSection("backpack");
        backpackSection.set("type", backpack.getType());
        backpackSection.set("capacity", backpack.getCapacity());
        backpackSection.set("used", backpack.getUsed());
        
        // Save bubbles in backpack
        ConfigurationSection bubblesSection = backpackSection.createSection("bubbles");
        List<Bubble> bubbles = backpack.getBubbles();
        for (int i = 0; i < bubbles.size(); i++) {
            Bubble bubble = bubbles.get(i);
            ConfigurationSection bubbleSection = bubblesSection.createSection(String.valueOf(i));
            bubbleSection.set("size", bubble.getSize());
            bubbleSection.set("gum-type", bubble.getGumType());
        }
        
        // Save pets
        ConfigurationSection petsSection = config.createSection("pets");
        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            ConfigurationSection petSection = petsSection.createSection(String.valueOf(i));
            petSection.set("id", pet.getId());
            petSection.set("type", pet.getType());
            petSection.set("rarity", pet.getRarity());
            petSection.set("level", pet.getLevel());
            petSection.set("experience", pet.getExperience());
            petSection.set("equipped", equippedPets.contains(pet));
        }
        
        // Save gum mastery
        ConfigurationSection gumMasterySection = config.createSection("gum-mastery");
        for (Map.Entry<String, Integer> entry : gumMastery.entrySet()) {
            gumMasterySection.set(entry.getKey(), entry.getValue());
        }
        
        // Save unlocked worlds
        ConfigurationSection unlockedWorldsSection = config.createSection("unlocked-worlds");
        for (Map.Entry<String, Boolean> entry : unlockedWorlds.entrySet()) {
            unlockedWorldsSection.set(entry.getKey(), entry.getValue());
        }
        
        // Save achievements
        ConfigurationSection achievementsSection = config.createSection("achievements");
        for (Map.Entry<String, Integer> entry : achievements.entrySet()) {
            achievementsSection.set(entry.getKey(), entry.getValue());
        }
    }
    
    // Getters and setters
    public Player getPlayer() {
        return player;
    }
    
    public long getCoins() {
        return coins;
    }
    
    public void setCoins(long coins) {
        this.coins = coins;
    }
    
    public void addCoins(long amount) {
        this.coins += amount;
        this.totalCoinsEarned += amount;
    }
    
    public boolean removeCoins(long amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }
    
    public long getGems() {
        return gems;
    }
    
    public void setGems(long gems) {
        this.gems = gems;
    }
    
    public void addGems(long amount) {
        this.gems += amount;
        this.totalGemsEarned += amount;
    }
    
    public boolean removeGems(long amount) {
        if (gems >= amount) {
            gems -= amount;
            return true;
        }
        return false;
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
    
    public void addExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        int requiredExp = level * 100;
        if (experience >= requiredExp) {
            experience -= requiredExp;
            level++;
            checkLevelUp();
        }
    }
    
    public double getCurrentBubbleSize() {
        return currentBubbleSize;
    }
    
    public void setCurrentBubbleSize(double size) {
        this.currentBubbleSize = size;
        if (size > largestBubble) {
            largestBubble = size;
        }
    }
    
    public double getLargestBubble() {
        return largestBubble;
    }
    
    public int getTotalBubblesBlown() {
        return totalBubblesBlown;
    }
    
    public void incrementTotalBubblesBlown() {
        this.totalBubblesBlown++;
    }
    
    public int getTotalCoinsEarned() {
        return totalCoinsEarned;
    }
    
    public int getTotalGemsEarned() {
        return totalGemsEarned;
    }
    
    public String getCurrentGumType() {
        return currentGumType;
    }
    
    public void setCurrentGumType(String gumType) {
        this.currentGumType = gumType;
    }
    
    public Backpack getBackpack() {
        return backpack;
    }
    
    public List<Pet> getPets() {
        return pets;
    }
    
    public void addPet(Pet pet) {
        pets.add(pet);
    }
    
    public List<Pet> getEquippedPets() {
        return equippedPets;
    }
    
    public void equipPet(Pet pet) {
        if (!equippedPets.contains(pet) && pets.contains(pet)) {
            equippedPets.add(pet);
        }
    }
    
    public void unequipPet(Pet pet) {
        equippedPets.remove(pet);
    }
    
    public int getGumMastery(String gumType) {
        return gumMastery.getOrDefault(gumType, 0);
    }
    
    public void increaseGumMastery(String gumType, int amount) {
        int currentMastery = gumMastery.getOrDefault(gumType, 0);
        gumMastery.put(gumType, currentMastery + amount);
    }
    
    public boolean isWorldUnlocked(String worldKey) {
        return unlockedWorlds.getOrDefault(worldKey, false);
    }
    
    public void unlockWorld(String worldKey) {
        unlockedWorlds.put(worldKey, true);
    }
    
    public int getAchievementProgress(String achievementKey) {
        return achievements.getOrDefault(achievementKey, 0);
    }
    
    public void updateAchievement(String achievementKey, int progress) {
        achievements.put(achievementKey, progress);
    }
    
    public double getPetBubbleMultiplier() {
        double multiplier = 1.0;
        for (Pet pet : equippedPets) {
            multiplier += pet.getBubbleMultiplier();
        }
        return multiplier;
    }
    
    public double getPetLuckMultiplier() {
        double multiplier = 1.0;
        for (Pet pet : equippedPets) {
            multiplier += pet.getLuckMultiplier();
        }
        return multiplier;
    }
}
