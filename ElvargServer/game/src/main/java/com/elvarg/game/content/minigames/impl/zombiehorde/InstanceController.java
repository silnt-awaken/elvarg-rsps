package com.elvarg.game.content.minigames.impl.zombiehorde;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.World;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.model.areas.impl.ZombieHordeArea;
import com.elvarg.game.content.minigames.impl.ZombieHordeSurvival;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Controls instances for the Zombie Horde Survival minigame.
 * Manages session lifecycle, cleanup, and player isolation.
 * 
 * @author Elvarg Development Team
 */
public class InstanceController {
    
    // Map of active sessions by player username
    private static final Map<String, ZombieHordeSession> activeSessions = new ConcurrentHashMap<>();
    
    // Instance area boundaries (Fight Caves area)
    private static final int MIN_X = 2360;
    private static final int MAX_X = 2445;
    private static final int MIN_Y = 5045;
    private static final int MAX_Y = 5125;
    
    // Safe exit location (outside Fight Caves)
    private static final Location EXIT_LOCATION = new Location(2439, 5169);
    
    /**
     * Creates a new zombie horde instance for a player.
     * 
     * @param player The player to create an instance for
     * @return The created session, or null if creation failed
     */
    public static ZombieHordeSession createInstance(Player player) {
        if (player == null) {
            return null;
        }
        
        String username = player.getUsername();
        
        // Check if player already has an active session
        if (activeSessions.containsKey(username)) {
            player.getPacketSender().sendMessage(
                "<col=ff0000>You already have an active Zombie Horde session!</col>"
            );
            return activeSessions.get(username);
        }
        
        try {
            // Create new area instance for the session
            ZombieHordeArea area = new ZombieHordeArea();
            
            // Create new session
            ZombieHordeSession session = new ZombieHordeSession(player, area);
            activeSessions.put(username, session);
            
            // Start the zombie horde session
            ZombieHordeSurvival.start(player);
            
            player.getPacketSender().sendMessage(
                "<col=00ff00>Zombie Horde instance created! Prepare for battle...</col>"
            );
            
            return session;
        } catch (Exception e) {
            System.err.println("Failed to create zombie horde instance for " + username + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets an active session for a player.
     * 
     * @param player The player to get the session for
     * @return The active session, or null if none exists
     */
    public static ZombieHordeSession getSession(Player player) {
        if (player == null) {
            return null;
        }
        return activeSessions.get(player.getUsername());
    }
    
    /**
     * Ends a player's zombie horde session.
     * 
     * @param player The player whose session to end
     * @param reason The reason for ending the session
     */
    public static void endSession(Player player, String reason) {
        if (player == null) {
            return;
        }
        
        String username = player.getUsername();
        ZombieHordeSession session = activeSessions.get(username);
        
        if (session != null) {
            try {
                // Clean up the session
                cleanupSession(session, reason);
                
                // Remove from active sessions
                activeSessions.remove(username);
                
                // Teleport player to safe location
                player.moveTo(EXIT_LOCATION);
                
                player.getPacketSender().sendMessage(
                    "<col=ff6600>Zombie Horde session ended: " + reason + "</col>"
                );
            } catch (Exception e) {
                System.err.println("Error ending session for " + username + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Cleans up a zombie horde session.
     * 
     * @param session The session to clean up
     * @param reason The reason for cleanup
     */
    private static void cleanupSession(ZombieHordeSession session, String reason) {
        if (session == null) {
            return;
        }
        
        try {
            Player player = session.getPlayer();
            
            // Award final rewards
            int finalWave = session.getCurrentWave();
            int totalBloodMoney = session.getTotalBloodMoneyEarned();
            RewardManager.awardSessionEndRewards(player, finalWave, totalBloodMoney);
            
            // Clear active zombies
            session.clearActiveZombies();
            
            // Remove all zombies from the instance area
            removeInstanceZombies();
            
            System.out.println("Cleaned up zombie horde session for " + 
                player.getUsername() + " (" + reason + ") - Wave: " + finalWave);
        } catch (Exception e) {
            System.err.println("Error during session cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Removes all zombies from the instance area.
     */
    private static void removeInstanceZombies() {
        try {
            List<NPC> zombiesToRemove = new ArrayList<>();
            
            // Find all NPCs in the instance area
            for (NPC npc : World.getNpcs()) {
                if (npc != null && isInInstanceArea(npc.getLocation())) {
                    // Check if it's a zombie (you may want to add more specific checks)
                    if (isZombieNPC(npc)) {
                        zombiesToRemove.add(npc);
                    }
                }
            }
            
            // Remove the zombies
            for (NPC zombie : zombiesToRemove) {
                World.getRemoveNPCQueue().add(zombie);
            }
            
            if (!zombiesToRemove.isEmpty()) {
                System.out.println("Removed " + zombiesToRemove.size() + " zombies from instance area");
            }
        } catch (Exception e) {
            System.err.println("Error removing instance zombies: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a location is within the instance area.
     * 
     * @param location The location to check
     * @return True if the location is in the instance area
     */
    private static boolean isInInstanceArea(Location location) {
        if (location == null) {
            return false;
        }
        
        int x = location.getX();
        int y = location.getY();
        
        return x >= MIN_X && x <= MAX_X && y >= MIN_Y && y <= MAX_Y;
    }
    
    /**
     * Checks if an NPC is a zombie type.
     * 
     * @param npc The NPC to check
     * @return True if the NPC is a zombie
     */
    private static boolean isZombieNPC(NPC npc) {
        if (npc == null) {
            return false;
        }
        
        // Check if it's a ZombieHordeNPC
        if (npc instanceof com.elvarg.game.entity.impl.npc.impl.ZombieHordeNPC) {
            return true;
        }
        
        // Check by NPC ID (zombie IDs)
        int npcId = npc.getId();
        return npcId == 76 || npcId == 77 || npcId == 78 || npcId == 79 || 
               npcId == 80 || npcId == 81 || npcId == 82 || npcId == 83;
    }
    
    /**
     * Handles player logout cleanup.
     * 
     * @param player The player who logged out
     */
    public static void handleLogout(Player player) {
        if (player != null && activeSessions.containsKey(player.getUsername())) {
            endSession(player, "Player logged out");
        }
    }
    
    /**
     * Handles player death cleanup.
     * 
     * @param player The player who died
     */
    public static void handleDeath(Player player) {
        if (player != null && activeSessions.containsKey(player.getUsername())) {
            endSession(player, "Player died");
        }
    }
    
    /**
     * Checks if a player has an active zombie horde session.
     * 
     * @param player The player to check
     * @return True if the player has an active session
     */
    public static boolean hasActiveSession(Player player) {
        if (player == null) {
            return false;
        }
        return activeSessions.containsKey(player.getUsername());
    }
    
    /**
     * Gets the number of active sessions.
     * 
     * @return The number of active sessions
     */
    public static int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    /**
     * Performs cleanup for all sessions (used during server shutdown).
     */
    public static void cleanupAllSessions() {
        try {
            for (Map.Entry<String, ZombieHordeSession> entry : activeSessions.entrySet()) {
                ZombieHordeSession session = entry.getValue();
                if (session != null) {
                    cleanupSession(session, "Server shutdown");
                }
            }
            activeSessions.clear();
            removeInstanceZombies();
            
            System.out.println("Cleaned up all zombie horde sessions");
        } catch (Exception e) {
            System.err.println("Error during global session cleanup: " + e.getMessage());
        }
    }
    
    /**
     * Gets the exit location for the instance.
     * 
     * @return The safe exit location
     */
    public static Location getExitLocation() {
        return EXIT_LOCATION;
    }
    
    /**
     * Checks if a location is the instance area.
     * 
     * @param location The location to check
     * @return True if in instance area
     */
    public static boolean isPlayerInInstance(Player player) {
        if (player == null) {
            return false;
        }
        return isInInstanceArea(player.getLocation());
    }
}