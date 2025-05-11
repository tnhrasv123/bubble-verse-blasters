
package com.bubblegum;

import com.bubblegum.commands.*;
import com.bubblegum.config.ConfigManager;
import com.bubblegum.data.DataManager;
import com.bubblegum.listeners.*;
import com.bubblegum.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class BubbleGumSimulator extends JavaPlugin {
    
    private static BubbleGumSimulator instance;
    
    private ConfigManager configManager;
    private DataManager dataManager;
    private BubbleManager bubbleManager;
    private PetManager petManager;
    private EconomyManager economyManager;
    private WorldManager worldManager;
    private GumManager gumManager;
    private BackpackManager backpackManager;
    private EventManager eventManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        configManager = new ConfigManager(this);
        dataManager = new DataManager(this);
        economyManager = new EconomyManager(this);
        bubbleManager = new BubbleManager(this);
        petManager = new PetManager(this);
        worldManager = new WorldManager(this);
        gumManager = new GumManager(this);
        backpackManager = new BackpackManager(this);
        eventManager = new EventManager(this);
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        registerListeners();
        
        // Load data
        loadData();
        
        getLogger().info("Bubble Gum Simulator has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save data
        dataManager.saveAllPlayerData();
        
        getLogger().info("Bubble Gum Simulator has been disabled!");
    }
    
    private void registerCommands() {
        getCommand("bubblegum").setExecutor(new BubbleGumCommand(this));
        getCommand("bubble").setExecutor(new BubbleCommand(this));
        getCommand("pets").setExecutor(new PetsCommand(this));
        getCommand("eggs").setExecutor(new EggsCommand(this));
        getCommand("bgsshop").setExecutor(new ShopCommand(this));
        getCommand("bgsstats").setExecutor(new StatsCommand(this));
        getCommand("bgsworlds").setExecutor(new WorldsCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new BubbleListener(this), this);
        getServer().getPluginManager().registerEvents(new PetListener(this), this);
        getServer().getPluginManager().registerEvents(new GumListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
    }
    
    private void loadData() {
        dataManager.loadAllPlayerData();
    }
    
    public static BubbleGumSimulator getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public BubbleManager getBubbleManager() {
        return bubbleManager;
    }
    
    public PetManager getPetManager() {
        return petManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public WorldManager getWorldManager() {
        return worldManager;
    }
    
    public GumManager getGumManager() {
        return gumManager;
    }
    
    public BackpackManager getBackpackManager() {
        return backpackManager;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
}
