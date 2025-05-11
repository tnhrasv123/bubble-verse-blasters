
package com.bubblegum.config;

import com.bubblegum.BubbleGumSimulator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final BubbleGumSimulator plugin;
    private FileConfiguration config;
    private File configFile;
    
    private final Map<String, CustomConfig> customConfigs = new HashMap<>();
    
    public ConfigManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        setupConfig();
        loadCustomConfigs();
    }
    
    private void setupConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        configFile = new File(plugin.getDataFolder(), "config.yml");
    }
    
    private void loadCustomConfigs() {
        // Create custom configs
        customConfigs.put("pets", new CustomConfig("pets.yml"));
        customConfigs.put("gum", new CustomConfig("gum.yml"));
        customConfigs.put("bubbles", new CustomConfig("bubbles.yml"));
        customConfigs.put("worlds", new CustomConfig("worlds.yml"));
        customConfigs.put("eggs", new CustomConfig("eggs.yml"));
        
        // Load all custom configs
        for (CustomConfig customConfig : customConfigs.values()) {
            customConfig.loadConfig(plugin);
        }
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // Reload all custom configs
        for (CustomConfig customConfig : customConfigs.values()) {
            customConfig.reloadConfig();
        }
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config to " + configFile);
            e.printStackTrace();
        }
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    public FileConfiguration getCustomConfig(String name) {
        CustomConfig customConfig = customConfigs.get(name);
        return customConfig != null ? customConfig.getConfig() : null;
    }
    
    public void saveCustomConfig(String name) {
        CustomConfig customConfig = customConfigs.get(name);
        if (customConfig != null) {
            customConfig.saveConfig();
        }
    }
    
    public class CustomConfig {
        private final String fileName;
        private File configFile;
        private FileConfiguration fileConfiguration;
        
        public CustomConfig(String fileName) {
            this.fileName = fileName;
        }
        
        public void loadConfig(BubbleGumSimulator plugin) {
            if (configFile == null) {
                configFile = new File(plugin.getDataFolder(), fileName);
            }
            
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                plugin.saveResource(fileName, false);
            }
            
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        }
        
        public FileConfiguration getConfig() {
            if (fileConfiguration == null) {
                reloadConfig();
            }
            return fileConfiguration;
        }
        
        public void saveConfig() {
            if (fileConfiguration == null || configFile == null) {
                return;
            }
            
            try {
                getConfig().save(configFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save config to " + configFile);
                e.printStackTrace();
            }
        }
        
        public void reloadConfig() {
            if (configFile == null) {
                configFile = new File(plugin.getDataFolder(), fileName);
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        }
    }
}
