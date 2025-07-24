package com.elvarg.game.content.minigames.impl;

import com.elvarg.game.World;
import com.elvarg.game.content.minigames.Minigame;
import com.elvarg.game.entity.impl.object.GameObject;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.impl.ZombieHordeArea;
import com.elvarg.game.content.minigames.impl.zombiehorde.InstanceController;
import com.elvarg.game.content.minigames.impl.zombiehorde.WaveManager;
import com.elvarg.game.content.minigames.impl.zombiehorde.RewardManager;
import com.elvarg.game.content.minigames.impl.zombiehorde.ZombieHordeSession;
import com.elvarg.util.ItemIdentifiers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Zombie Horde Survival Minigame
 * 
 * An infinite wave-based survival mode where players fight increasingly difficult
 * waves of zombies to earn Blood Money rewards. Features private instancing,
 * progressive difficulty scaling, and milestone rewards.
 * 
 * @author Solo Coding
 */
public class ZombieHordeSurvival implements Minigame {

    // Teleport locations
    public static final Location ENTRANCE = new Location(2413, 5117);
    public static final Location EXIT = new Location(2438, 5168);
    public static final Location ARENA_CENTER = new Location(2401, 5088);
    
    // Configuration constants
    public static final int BASE_BLOOD_MONEY_REWARD = 50;
    public static final int MILESTONE_WAVE_INTERVAL = 10;
    public static final int WAVE_COUNTDOWN_SECONDS = 15;
    
    // Active sessions tracking (now managed by InstanceController)
    // private static final Map<Player, ZombieHordeSession> activeSessions = new ConcurrentHashMap<>();
    
    // Component references (now using static methods)
    // private static final InstanceController instanceController = new InstanceController();
    // private static final WaveManager waveManager = new WaveManager();
    // private static final RewardManager rewardManager = new RewardManager();
    
    /**
     * Starts a new zombie horde survival session for the player.
     * Creates a private instance and begins wave 1.
     * 
     * @param player The player starting the minigame
     */
    public static void start(Player player) {
        if (player == null) {
            return;
        }
        
        try {
            // Teleport player to the arena first
            player.moveTo(ENTRANCE);
            
            // Create instance through InstanceController
            ZombieHordeSession session = InstanceController.createInstance(player);
            
            if (session != null) {
                // Session is already created and ready
                
                player.getPacketSender().sendMessage(
                    "<col=00ff00>Welcome to Zombie Horde Survival! Survive as long as you can!</col>"
                );
            } else {
                player.getPacketSender().sendMessage(
                    "<col=ff0000>Failed to create Zombie Horde instance. Please try again.</col>"
                );
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start Zombie Horde session for " + player.getUsername() + ": " + e.getMessage());
            player.getPacketSender().sendMessage("Failed to start Zombie Horde session. Please try again.");
        }
    }
    
    /**
     * Ends a player's zombie horde session.
     * Awards earned rewards and cleans up the instance.
     * 
     * @param player The player whose session is ending
     * @param reason The reason for ending (death, logout, manual exit)
     */
    public static void endSession(Player player, String reason) {
        InstanceController.endSession(player, reason);
    }
    
    /**
     * Gets the active session for a player.
     * 
     * @param player The player
     * @return The active session or null if none exists
     */
    public static ZombieHordeSession getSession(Player player) {
        return InstanceController.getSession(player);
    }
    
    /**
     * Checks if a player has an active session.
     * 
     * @param player The player to check
     * @return True if the player has an active session
     */
    public static boolean hasActiveSession(Player player) {
        return InstanceController.hasActiveSession(player);
    }
    
    /**
     * Gets the number of active sessions.
     * 
     * @return The number of active sessions
     */
    public static int getActiveSessionCount() {
        return InstanceController.getActiveSessionCount();
    }
    
    @Override
    public boolean firstClickObject(Player player, GameObject object) {
        // Handle any special objects in the zombie horde area
        return false;
    }
    
    @Override
    public boolean handleButtonClick(Player player, int button) {
        // Handle any UI button clicks related to zombie horde
        return false;
    }
    
    @Override
    public void process() {
        // Processing is handled by individual sessions
        // No global processing needed as sessions manage themselves
    }
    
    @Override
    public void init() {
        System.out.println("Zombie Horde Survival minigame initialized.");
    }
    
    /**
     * Handles player logout cleanup.
     * 
     * @param player The player who logged out
     */
    public static void handleLogout(Player player) {
        InstanceController.handleLogout(player);
    }
    
    /**
     * Handles player death cleanup.
     * 
     * @param player The player who died
     */
    public static void handleDeath(Player player) {
        InstanceController.handleDeath(player);
    }
}