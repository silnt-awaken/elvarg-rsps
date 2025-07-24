package com.elvarg.game.model.areas.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.Area;
import java.util.Arrays;

/**
 * Represents a private area instance for the Zombie Horde Survival minigame.
 * Each player gets their own instance to prevent interference between sessions.
 * 
 * @author Solo Coding
 */
public class ZombieHordeArea extends Area {
    
    // Area boundaries (reusing Fight Caves coordinates)
    private static final Boundary BOUNDARY = new Boundary(2360, 2445, 5045, 5125);
    
    // Area name for identification
    private static final String AREA_NAME = "Zombie Horde Arena";
    
    /**
     * Creates a new zombie horde area instance.
     */
    public ZombieHordeArea() {
        super(Arrays.asList(BOUNDARY));
    }
    
    /**
     * Checks if a location is within the zombie horde arena.
     * 
     * @param location The location to check
     * @return True if the location is within the arena
     */
    public static boolean isInZombieHordeArea(Location location) {
        return BOUNDARY.inside(location);
    }
    
    /**
     * Gets the area boundary.
     * 
     * @return The boundary of the zombie horde area
     */
    public static Boundary getBoundary() {
        return BOUNDARY;
    }
    
    /**
     * Gets a safe location within the arena for spawning.
     * 
     * @return A safe spawn location
     */
    public Location getSafeSpawnLocation() {
        return new Location(2398, 5087); // Center of area
    }
    
    /**
     * Checks if the area is currently active (has players).
     * 
     * @return True if the area has active players
     */
    public boolean isActive() {
        return !getPlayers().isEmpty();
    }
    
    /**
     * Gets the number of active players in this area instance.
     * 
     * @return The number of players
     */
    public int getPlayerCount() {
        return getPlayers().size();
    }
    
    /**
     * Checks if a player is in this area.
     * 
     * @param player The player to check
     * @return True if the player is in this area
     */
    public boolean contains(Player player) {
        if (player == null) {
            return false;
        }
        return BOUNDARY.inside(player.getLocation());
    }
}