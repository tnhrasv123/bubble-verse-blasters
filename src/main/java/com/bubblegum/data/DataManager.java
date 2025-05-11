
package com.bubblegum.data;

import com.bubblegum.BubbleGumSimulator;
import com.bubblegum.models.BGPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    
    private final BubbleGumSimulator plugin;
    private final Map<UUID, BGPlayer> playerData = new HashMap<>();
    private final File playerDataFolder;
    
    public DataManager(BubbleGumSimulator plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }
    
    public BGPlayer getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerData.containsKey(uuid)) {
            loadPlayerData(player);
        }
        return playerData.get(uuid);
    }
    
    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(playerDataFolder, uuid + ".yml");
        
        if (!playerFile.exists()) {
            // Create default player data
            BGPlayer bgPlayer = new BGPlayer(player);
            playerData.put(uuid, bgPlayer);
            savePlayerData(player);
            return;
        }
        
        // Load existing player data
        FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        BGPlayer bgPlayer = BGPlayer.fromConfig(player, playerConfig);
        playerData.put(uuid, bgPlayer);
    }
    
    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerData.containsKey(uuid)) {
            return;
        }
        
        BGPlayer bgPlayer = playerData.get(uuid);
        File playerFile = new File(playerDataFolder, uuid + ".yml");
        FileConfiguration playerConfig = new YamlConfiguration();
        
        // Save player data
        bgPlayer.saveToConfig(playerConfig);
        
        try {
            playerConfig.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player data for " + player.getName());
            e.printStackTrace();
        }
    }
    
    public void saveAllPlayerData() {
        for (Map.Entry<UUID, BGPlayer> entry : playerData.entrySet()) {
            UUID uuid = entry.getKey();
            BGPlayer bgPlayer = entry.getValue();
            
            File playerFile = new File(playerDataFolder, uuid + ".yml");
            FileConfiguration playerConfig = new YamlConfiguration();
            
            // Save player data
            bgPlayer.saveToConfig(playerConfig);
            
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save player data for UUID: " + uuid);
                e.printStackTrace();
            }
        }
    }
    
    public void loadAllPlayerData() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            loadPlayerData(player);
        }
    }
    
    public void removePlayerData(Player player) {
        playerData.remove(player.getUniqueId());
    }
    
    public void resetPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(playerDataFolder, uuid + ".yml");
        
        if (playerFile.exists()) {
            playerFile.delete();
        }
        
        // Create new player data
        BGPlayer bgPlayer = new BGPlayer(player);
        playerData.put(uuid, bgPlayer);
        savePlayerData(player);
    }
}
