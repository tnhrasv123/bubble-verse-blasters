
package com.bubblegum.utils;

import com.bubblegum.BubbleGumSimulator;
import org.bukkit.command.CommandSender;

public class MessageUtils {
    
    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        
        String prefix = BubbleGumSimulator.getInstance().getConfig().getString("messages.prefix", "&b&lBubbleGum &8» &r");
        sender.sendMessage(ColorUtils.colorize(prefix + message));
    }
    
    public static String formatMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        
        String prefix = BubbleGumSimulator.getInstance().getConfig().getString("messages.prefix", "&b&lBubbleGum &8» &r");
        return ColorUtils.colorize(prefix + message);
    }
}
