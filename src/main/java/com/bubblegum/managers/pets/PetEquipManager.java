
package com.bubblegum.managers.pets;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.entity.Player;

public class PetEquipManager {
    
    private final BubbleGumSimulator plugin;
    
    public PetEquipManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
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
        int maxPets = plugin.getConfig().getInt("settings.max-equipped-pets", 3);
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
