
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import com.bubblegum.models.Pet;
import com.bubblegum.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class EventManager {
    
    private final BubbleGumSimulator plugin;
    private final Map<String, Event> events;
    private final Map<String, BukkitTask> eventTasks;
    private final Random random;
    
    public EventManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.events = new HashMap<>();
        this.eventTasks = new HashMap<>();
        this.random = new Random();
        
        registerEvents();
        scheduleRandomEvents();
    }
    
    private void registerEvents() {
        // Register default events
        events.put("bubble_contest", new Event("Bubble Contest", "Blow the biggest bubbles to win prizes!", 600));
        events.put("pet_xp_boost", new Event("Pet XP Boost", "Pets earn double experience!", 900));
        events.put("coin_rain", new Event("Coin Rain", "Bubbles are worth double coins!", 600));
        events.put("egg_sale", new Event("Egg Sale", "Eggs cost 50% less!", 1200));
        events.put("gem_rush", new Event("Gem Rush", "Chance to find gems when blowing bubbles!", 900));
    }
    
    private void scheduleRandomEvents() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Check if any event is currently running
                if (!eventTasks.isEmpty()) {
                    return;
                }
                
                // 25% chance to start an event
                if (random.nextDouble() > 0.25) {
                    return;
                }
                
                // Pick a random event
                List<String> eventKeys = new ArrayList<>(events.keySet());
                if (eventKeys.isEmpty()) {
                    return;
                }
                
                String eventKey = eventKeys.get(random.nextInt(eventKeys.size()));
                startEvent(eventKey);
            }
        }.runTaskTimer(plugin, 6000, 6000); // Check every 5 minutes (6000 ticks)
    }
    
    public void startEvent(String eventKey) {
        Event event = events.get(eventKey);
        if (event == null) {
            return;
        }
        
        // Check if event already running
        if (eventTasks.containsKey(eventKey)) {
            return;
        }
        
        // Announce event start
        Bukkit.broadcastMessage("§b§l⭐ EVENT: §r" + event.getName() + " §b⭐");
        Bukkit.broadcastMessage("§7" + event.getDescription());
        Bukkit.broadcastMessage("§7Event will last for §f" + (event.getDuration() / 60) + " minutes§7!");
        
        // Play sound for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        }
        
        // Start event tasks
        switch (eventKey) {
            case "bubble_contest":
                startBubbleContest();
                break;
            case "pet_xp_boost":
                startPetXpBoost();
                break;
            case "coin_rain":
                startCoinRain();
                break;
            case "egg_sale":
                startEggSale();
                break;
            case "gem_rush":
                startGemRush();
                break;
        }
        
        // Schedule event end
        BukkitTask endTask = new BukkitRunnable() {
            @Override
            public void run() {
                endEvent(eventKey);
            }
        }.runTaskLater(plugin, event.getDuration() * 20L); // Duration is in seconds, convert to ticks
        
        eventTasks.put(eventKey, endTask);
    }
    
    public void endEvent(String eventKey) {
        // Cancel task
        BukkitTask task = eventTasks.remove(eventKey);
        if (task != null) {
            task.cancel();
        }
        
        // Announce event end
        Event event = events.get(eventKey);
        if (event != null) {
            Bukkit.broadcastMessage("§b§lEvent Ended: §r" + event.getName());
            
            // Additional cleanup based on event type
            switch (eventKey) {
                case "coin_rain":
                    // Reset coin multipliers
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.getEconomyManager().setPlayerCoinMultiplier(player, 1.0, 0);
                    }
                    break;
                case "pet_xp_boost":
                    // Nothing to clean up for pet xp boost
                    break;
                case "egg_sale":
                    // Nothing to clean up for egg sale
                    break;
                case "gem_rush":
                    // Nothing to clean up for gem rush
                    break;
                case "bubble_contest":
                    // Determine winner of bubble contest
                    determineBubbleContestWinner();
                    break;
            }
            
            // Play sound for all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }
    
    public boolean isEventActive(String eventKey) {
        return eventTasks.containsKey(eventKey);
    }
    
    private void startBubbleContest() {
        // Clear previous contest data
        for (Player player : Bukkit.getOnlinePlayers()) {
            BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
            bgPlayer.setContestBubbleSize(0);
        }
        
        // Broadcast rules
        Bukkit.broadcastMessage("§e§lBubble Contest Rules:");
        Bukkit.broadcastMessage("§e- Blow the biggest bubble during the event");
        Bukkit.broadcastMessage("§e- The winner gets coins and gems");
        Bukkit.broadcastMessage("§e- Everyone gets rewards based on participation");
    }
    
    private void determineBubbleContestWinner() {
        Player winner = null;
        double largestBubble = 0;
        
        // Find the player with the largest bubble during contest
        for (Player player : Bukkit.getOnlinePlayers()) {
            BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
            double contestBubbleSize = bgPlayer.getContestBubbleSize();
            
            if (contestBubbleSize > largestBubble) {
                winner = player;
                largestBubble = contestBubbleSize;
            }
        }
        
        // Award prizes
        if (winner != null && largestBubble > 0) {
            // Calculate prizes based on bubble size
            long coinPrize = (long) (largestBubble * 1000);
            long gemPrize = (long) (largestBubble * 5);
            
            // Award the winner
            plugin.getEconomyManager().addCoins(winner, coinPrize);
            plugin.getEconomyManager().addGems(winner, gemPrize);
            
            // Announce winner
            Bukkit.broadcastMessage("§6§lBUBBLE CONTEST WINNER: §e" + winner.getName());
            Bukkit.broadcastMessage("§6Largest Bubble: §e" + String.format("%.2f", largestBubble));
            Bukkit.broadcastMessage("§6Prize: §e" + plugin.getEconomyManager().formatCoins(coinPrize) + 
                    " §6and §b" + plugin.getEconomyManager().formatGems(gemPrize));
            
            // Play sound and effect for winner
            winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            
            // Give participation rewards to others
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(winner)) {
                    BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
                    double contestBubbleSize = bgPlayer.getContestBubbleSize();
                    
                    if (contestBubbleSize > 0) {
                        long participationCoins = (long) (contestBubbleSize * 100);
                        plugin.getEconomyManager().addCoins(player, participationCoins);
                        MessageUtils.sendMessage(player, "§6You earned §e" + 
                                plugin.getEconomyManager().formatCoins(participationCoins) + 
                                " §6for your participation!");
                    }
                }
            }
        } else {
            Bukkit.broadcastMessage("§c§lNo one participated in the Bubble Contest!");
        }
    }
    
    private void startPetXpBoost() {
        // Double pet XP for the duration
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isEventActive("pet_xp_boost")) {
                    cancel();
                    return;
                }
                
                // Every 10 seconds, give extra XP to pets
                for (Player player : Bukkit.getOnlinePlayers()) {
                    BGPlayer bgPlayer = plugin.getDataManager().getPlayerData(player);
                    
                    for (Pet pet : bgPlayer.getEquippedPets()) {
                        // Add extra XP (normally would happen when bubbles are blown)
                        plugin.getPetManager().addPetExperience(player, pet, 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 200L, 200L); // Every 10 seconds (200 ticks)
    }
    
    private void startCoinRain() {
        // Double coin value for all players
        for (Player player : Bukkit.getOnlinePlayers()) {
            plugin.getEconomyManager().setPlayerCoinMultiplier(player, 2.0, 0);
            MessageUtils.sendMessage(player, "§6Coin Rain! §eAll coins earned are doubled during this event!");
        }
    }
    
    private void startEggSale() {
        // Nothing to do here - egg costs are checked dynamically 
        // in shop commands using isEventActive("egg_sale")
        Bukkit.broadcastMessage("§d§lEgg Sale started! All eggs are 50% off!");
    }
    
    private void startGemRush() {
        // Setup chance of gem drops when blowing bubbles
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isEventActive("gem_rush")) {
                    cancel();
                    return;
                }
                
                // Random gem drops for active players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (plugin.getBubbleManager().isBlowingBubble(player)) {
                        if (random.nextDouble() < 0.3) { // 30% chance every check
                            int gems = random.nextInt(5) + 1; // 1-5 gems
                            plugin.getEconomyManager().addGems(player, gems);
                            MessageUtils.sendMessage(player, "§b✨ You found " + gems + " gems while blowing bubbles!");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 100L); // Every 5 seconds (100 ticks)
    }
    
    public static class Event {
        private final String name;
        private final String description;
        private final int duration; // in seconds
        
        public Event(String name, String description, int duration) {
            this.name = name;
            this.description = description;
            this.duration = duration;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getDuration() {
            return duration;
        }
    }
}
