
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.managers.pets.PetEquipManager;
import com.bubblegum.managers.pets.PetHatchingManager;
import com.bubblegum.managers.pets.PetItemManager;
import com.bubblegum.managers.pets.PetTypeManager;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PetManager {
    
    private final BubbleGumSimulator plugin;
    private final PetTypeManager typeManager;
    private final PetHatchingManager hatchingManager;
    private final PetItemManager itemManager;
    private final PetEquipManager equipManager;
    
    public PetManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.typeManager = new PetTypeManager(plugin);
        this.itemManager = new PetItemManager(plugin);
        this.hatchingManager = new PetHatchingManager(plugin, typeManager);
        this.equipManager = new PetEquipManager(plugin);
    }
    
    // Delegate methods to the appropriate managers
    
    public Pet hatchEgg(Player player, String eggType) {
        return hatchingManager.hatchEgg(player, eggType);
    }
    
    public ItemStack createPetItem(Pet pet) {
        return itemManager.createPetItem(pet);
    }
    
    public boolean isPetItem(ItemStack item) {
        return itemManager.isPetItem(item);
    }
    
    public String getPetId(ItemStack item) {
        return itemManager.getPetId(item);
    }
    
    public String getRarityColor(String rarity) {
        return itemManager.getRarityColor(rarity);
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
        equipManager.equipPet(player, pet);
    }
    
    public void unequipPet(Player player, Pet pet) {
        equipManager.unequipPet(player, pet);
    }
    
    public void addPetExperience(Player player, Pet pet, int amount) {
        equipManager.addPetExperience(player, pet, amount);
    }
}
