
package com.bubblegum.commands;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PetsCommand implements CommandExecutor {
    
    private final BubbleGumSimulator plugin;
    
    public PetsCommand(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, plugin.getConfig().getString("messages.error.player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bubblegum.pets")) {
            MessageUtils.sendMessage(player, plugin.getConfig().getString("messages.error.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            listPets(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "list":
                listPets(player);
                break;
            case "info":
                if (args.length < 2) {
                    MessageUtils.sendMessage(player, "&cUsage: /pets info <pet-id>");
                    return true;
                }
                showPetInfo(player, args[1]);
                break;
            case "equip":
                if (args.length < 2) {
                    MessageUtils.sendMessage(player, "&cUsage: /pets equip <pet-id>");
                    return true;
                }
                equipPet(player, args[1]);
                break;
            case "unequip":
                if (args.length < 2) {
                    MessageUtils.sendMessage(player, "&cUsage: /pets unequip <pet-id>");
                    return true;
                }
                unequipPet(player, args[1]);
                break;
            default:
                sendHelpMessage(player);
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        MessageUtils.sendMessage(player, "&6=== Pet Commands ===");
        MessageUtils.sendMessage(player, "&e/pets list &7- List all your pets");
        MessageUtils.sendMessage(player, "&e/pets info <pet-id> &7- Show information about a pet");
        MessageUtils.sendMessage(player, "&e/pets equip <pet-id> &7- Equip a pet");
        MessageUtils.sendMessage(player, "&e/pets unequip <pet-id> &7- Unequip a pet");
    }
    
    private void listPets(Player player) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        List<Pet> pets = bgPlayer.getPets();
        List<Pet> equippedPets = bgPlayer.getEquippedPets();
        
        MessageUtils.sendMessage(player, "&6=== Your Pets ===");
        MessageUtils.sendMessage(player, "&7You have &f" + pets.size() + " &7pets &f(" + equippedPets.size() + " equipped)");
        
        if (pets.isEmpty()) {
            MessageUtils.sendMessage(player, "&7You don't have any pets yet!");
            return;
        }
        
        for (Pet pet : pets) {
            boolean equipped = equippedPets.contains(pet);
            String equippedStatus = equipped ? " &a[EQUIPPED]" : "";
            String rarityColor = plugin.getPetManager().getRarityColor(pet.getRarity());
            
            MessageUtils.sendMessage(player, rarityColor + pet.getType() + " &7(Level " + pet.getLevel() + ") - ID: " + pet.getId() + equippedStatus);
        }
        
        MessageUtils.sendMessage(player, "&7Use &f/pets info <pet-id> &7to view pet details");
    }
    
    private void showPetInfo(Player player, String petId) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        Pet pet = plugin.getPetManager().getPetById(bgPlayer, petId);
        
        if (pet == null) {
            MessageUtils.sendMessage(player, "&cPet not found with ID: " + petId);
            return;
        }
        
        String rarityColor = plugin.getPetManager().getRarityColor(pet.getRarity());
        
        MessageUtils.sendMessage(player, "&6=== Pet Info ===");
        MessageUtils.sendMessage(player, "&7Type: " + rarityColor + pet.getType());
        MessageUtils.sendMessage(player, "&7Rarity: " + rarityColor + pet.getRarity());
        MessageUtils.sendMessage(player, "&7Level: &f" + pet.getLevel());
        MessageUtils.sendMessage(player, "&7Experience: &f" + pet.getExperience() + "&7/&f" + (pet.getLevel() * 50));
        MessageUtils.sendMessage(player, "&7Bubble Multiplier: &f" + String.format("%.2fx", pet.getBubbleMultiplier()));
        MessageUtils.sendMessage(player, "&7Luck Multiplier: &f" + String.format("%.2fx", pet.getLuckMultiplier()));
        
        boolean equipped = bgPlayer.getEquippedPets().contains(pet);
        MessageUtils.sendMessage(player, "&7Status: " + (equipped ? "&aEquipped" : "&cUnequipped"));
        
        if (!equipped) {
            MessageUtils.sendMessage(player, "&7Use &f/pets equip " + petId + " &7to equip this pet");
        } else {
            MessageUtils.sendMessage(player, "&7Use &f/pets unequip " + petId + " &7to unequip this pet");
        }
    }
    
    private void equipPet(Player player, String petId) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        Pet pet = plugin.getPetManager().getPetById(bgPlayer, petId);
        
        if (pet == null) {
            MessageUtils.sendMessage(player, "&cPet not found with ID: " + petId);
            return;
        }
        
        plugin.getPetManager().equipPet(player, pet);
    }
    
    private void unequipPet(Player player, String petId) {
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        Pet pet = plugin.getPetManager().getPetById(bgPlayer, petId);
        
        if (pet == null) {
            MessageUtils.sendMessage(player, "&cPet not found with ID: " + petId);
            return;
        }
        
        plugin.getPetManager().unequipPet(player, pet);
    }
}
