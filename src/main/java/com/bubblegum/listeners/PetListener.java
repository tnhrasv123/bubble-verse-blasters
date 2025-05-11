
package com.bubblegum.listeners;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PetListener implements Listener {
    
    private final BubbleGumSimulator plugin;
    
    public PetListener(BubbleGumSimulator plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null || !plugin.getPetManager().isPetItem(item)) {
            return;
        }
        
        Player player = event.getPlayer();
        BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
        
        // Get pet ID
        String petId = plugin.getPetManager().getPetId(item);
        if (petId == null) {
            return;
        }
        
        // Get pet
        Pet pet = plugin.getPetManager().getPetById(bgPlayer, petId);
        if (pet == null) {
            return;
        }
        
        // Toggle equip
        if (bgPlayer.getEquippedPets().contains(pet)) {
            plugin.getPetManager().unequipPet(player, pet);
        } else {
            plugin.getPetManager().equipPet(player, pet);
        }
        
        event.setCancelled(true);
    }
    
    // Additional pet-related event handlers would go here
}
