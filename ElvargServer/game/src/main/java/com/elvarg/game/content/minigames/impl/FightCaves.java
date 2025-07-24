package com.elvarg.game.content.minigames.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;

/**
 * Fight Caves minigame - now redirects to Zombie Horde Survival mode.
 * 
 * The original Fight Caves has been revamped into an infinite wave-based
 * zombie survival mode that provides better replayability and progression.
 * 
 * @author Solo Coding
 */
public class FightCaves {

    public static final Location ENTRANCE = new Location(2413, 5117);
    public static final Location EXIT = new Location(2438, 5168);
    
    /**
     * Starts the Fight Caves experience (now Zombie Horde Survival).
     * 
     * @param player The player starting the minigame
     */
    public static void start(Player player) {
        // Check if player is already in a zombie horde session
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            player.getPacketSender().sendMessage("You are already in a Zombie Horde session!");
            return;
        }
        
        // Send transition message
        player.getPacketSender().sendMessage("<col=ff8000>The Fight Caves have been overrun by zombies!");
        player.getPacketSender().sendMessage("<col=ffffff>Survive infinite waves to earn Blood Money rewards!");
        
        // Start zombie horde survival mode
        ZombieHordeSurvival.start(player);
    }
    
    /**
     * Legacy method for compatibility.
     * Now redirects to zombie horde survival.
     * 
     * @param player The player
     */
    public static void enter(Player player) {
        start(player);
    }
    
    /**
     * Exits the Fight Caves area.
     * 
     * @param player The player exiting
     */
    public static void exit(Player player) {
        // End any active zombie horde session
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            ZombieHordeSurvival.endSession(player, "Manual exit");
        }
        
        // Move player to exit location
        player.moveTo(EXIT);
    }
}
