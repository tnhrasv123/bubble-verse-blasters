
package com.bubblegum.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;

public class ColorUtils {
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static Particle.DustOptions getDustOptions(String colorName, float size) {
        Color color;
        
        switch (colorName.toUpperCase()) {
            case "RED":
                color = Color.RED;
                break;
            case "GREEN":
                color = Color.GREEN;
                break;
            case "BLUE":
                color = Color.BLUE;
                break;
            case "YELLOW":
                color = Color.YELLOW;
                break;
            case "AQUA":
                color = Color.AQUA;
                break;
            case "PURPLE":
                color = Color.PURPLE;
                break;
            case "PINK":
                color = Color.fromRGB(255, 105, 180);
                break;
            case "ORANGE":
                color = Color.ORANGE;
                break;
            case "BLACK":
                color = Color.BLACK;
                break;
            case "GRAY":
                color = Color.GRAY;
                break;
            case "LIME":
                color = Color.LIME;
                break;
            case "NAVY":
                color = Color.NAVY;
                break;
            case "OLIVE":
                color = Color.OLIVE;
                break;
            case "SILVER":
                color = Color.SILVER;
                break;
            case "TEAL":
                color = Color.TEAL;
                break;
            case "MAROON":
                color = Color.MAROON;
                break;
            case "LIGHT_PURPLE":
                color = Color.fromRGB(230, 103, 253);
                break;
            case "DARK_PURPLE":
                color = Color.fromRGB(95, 15, 130);
                break;
            case "RAINBOW":
                // For rainbow, use a random color
                int r = (int) (Math.random() * 255);
                int g = (int) (Math.random() * 255);
                int b = (int) (Math.random() * 255);
                color = Color.fromRGB(r, g, b);
                break;
            case "WHITE":
            default:
                color = Color.WHITE;
                break;
        }
        
        return new Particle.DustOptions(color, size);
    }
    
    public static ChatColor getChatColor(String colorName) {
        switch (colorName.toUpperCase()) {
            case "BLACK":
                return ChatColor.BLACK;
            case "DARK_BLUE":
                return ChatColor.DARK_BLUE;
            case "DARK_GREEN":
                return ChatColor.DARK_GREEN;
            case "DARK_AQUA":
            case "TEAL":
                return ChatColor.DARK_AQUA;
            case "DARK_RED":
            case "MAROON":
                return ChatColor.DARK_RED;
            case "DARK_PURPLE":
                return ChatColor.DARK_PURPLE;
            case "GOLD":
            case "ORANGE":
                return ChatColor.GOLD;
            case "GRAY":
                return ChatColor.GRAY;
            case "DARK_GRAY":
                return ChatColor.DARK_GRAY;
            case "BLUE":
            case "NAVY":
                return ChatColor.BLUE;
            case "GREEN":
            case "LIME":
                return ChatColor.GREEN;
            case "AQUA":
                return ChatColor.AQUA;
            case "RED":
                return ChatColor.RED;
            case "LIGHT_PURPLE":
            case "PURPLE":
            case "PINK":
                return ChatColor.LIGHT_PURPLE;
            case "YELLOW":
                return ChatColor.YELLOW;
            case "WHITE":
            default:
                return ChatColor.WHITE;
        }
    }
}
