
package com.bubblegum.managers;

import com.bubblegum.BubbleGumSimulator;
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
        plugin.getServer().broadcastMessage("§b§lEvent: §r" + event.getName());
        plugin.getServer().broadcastMessage("§7" + event.getDescription());
        plugin.getServer().broadcastMessage("§7Event will last for §f" + (event.getDuration() / 60) + " minutes§7!");
        
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
            plugin.getServer().broadcastMessage("§b§lEvent Ended: §r" + event.getName());
        }
    }
    
    public boolean isEventActive(String eventKey) {
        return eventTasks.containsKey(eventKey);
    }
    
    private void startBubbleContest() {
        // Implementation would be added here
    }
    
    private void startPetXpBoost() {
        // Implementation would be added here
    }
    
    private void startCoinRain() {
        // Implementation would be added here
    }
    
    private void startEggSale() {
        // Implementation would be added here
    }
    
    private void startGemRush() {
        // Implementation would be added here
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
