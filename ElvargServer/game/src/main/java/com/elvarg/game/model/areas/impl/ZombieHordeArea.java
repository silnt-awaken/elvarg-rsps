package com.elvarg.game.model.areas.impl;

import com.elvarg.game.content.minigames.impl.ZombieHordeSurvival;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.Location;
import com.elvarg.game.model.areas.Area;

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
        super(BOUNDARY);
    }
    
    @Override
    public void onPlayerEnter(Player player) {
        // Send area entry message
        player.getPacketSender().sendMessage("You enter the " + AREA_NAME + ".");
        
        // Ensure player has an active session when entering
        if (!ZombieHordeSurvival.hasActiveSession(player)) {
            // If no session exists, start one
            ZombieHordeSurvival.start(player);
        }
    }
    
    @Override
    public void onPlayerExit(Player player) {
        // Send area exit message
        player.getPacketSender().sendMessage("You leave the " + AREA_NAME + ".");
        
        // End the zombie horde session when player leaves
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            ZombieHordeSurvival.endSession(player, "Left area");
        }
    }
    
    @Override
    public void onPlayerLogout(Player player) {
        // Handle logout - end session and clean up
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            ZombieHordeSurvival.endSession(player, "Logout");
        }
    }
    
    @Override
    public void onPlayerDeath(Player player) {
        // Handle death - end session with death reason
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            ZombieHordeSurvival.endSession(player, "Death");
        }
    }
    
    @Override
    public boolean canTeleport(Player player) {
        // Prevent teleporting during active zombie horde session
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            player.getPacketSender().sendMessage("You cannot teleport during a zombie horde session!");
            player.getPacketSender().sendMessage("You must complete or abandon your current session first.");
            return false;
        }
        return true;
    }
    
    @Override
    public boolean canTrade(Player player, Player target) {
        // Prevent trading in zombie horde area
        player.getPacketSender().sendMessage("You cannot trade in the " + AREA_NAME + "!");
        return false;
    }
    
    @Override
    public boolean canEat(Player player, int itemId) {
        // Allow eating food for healing
        return true;
    }
    
    @Override
    public boolean canDrink(Player player, int itemId) {
        // Allow drinking potions
        return true;
    }
    
    @Override
    public boolean dropItemsOnDeath(Player player) {
        // Don't drop items on death in zombie horde (safe death)
        return false;
    }
    
    @Override
    public boolean handleKilledNPC(Player killer, int npcId) {
        // Handle zombie kills for wave progression
        if (ZombieHordeSurvival.hasActiveSession(killer)) {
            ZombieHordeSurvival.onZombieKilled(ZombieHordeSurvival.getSession(killer), npcId);
            return true;
        }
        return false;
    }
    
    @Override
    public void process() {
        // Process any area-specific logic
        // The main processing is handled by ZombieHordeSurvival.process()
    }
    
    @Override
    public boolean canAttackNPC(Player player, int npcId) {
        // Allow attacking zombies in the area
        return true;
    }
    
    @Override
    public boolean canBeAttacked(Player player, Player attacker) {
        // Prevent PvP in zombie horde area
        return false;
    }
    
    @Override
    public boolean canUsePrayer(Player player) {
        // Allow prayer usage
        return true;
    }
    
    @Override
    public boolean canUseMagic(Player player) {
        // Allow magic usage
        return true;
    }
    
    @Override
    public boolean canUseRanged(Player player) {
        // Allow ranged combat
        return true;
    }
    
    @Override
    public boolean canUseMelee(Player player) {
        // Allow melee combat
        return true;
    }
    
    @Override
    public boolean canUseSpecialAttack(Player player) {
        // Allow special attacks
        return true;
    }
    
    @Override
    public boolean canPickupItem(Player player, int itemId) {
        // Allow picking up items (drops from zombies)
        return true;
    }
    
    @Override
    public boolean canDropItem(Player player, int itemId) {
        // Allow dropping items
        return true;
    }
    
    @Override
    public boolean canBank(Player player) {
        // Prevent banking in zombie horde area
        player.getPacketSender().sendMessage("You cannot access your bank during a zombie horde session!");
        return false;
    }
    
    @Override
    public boolean canShop(Player player) {
        // Prevent shopping in zombie horde area
        player.getPacketSender().sendMessage("You cannot access shops during a zombie horde session!");
        return false;
    }
    
    @Override
    public boolean canUseStairs(Player player) {
        // Prevent using stairs to leave the area
        player.getPacketSender().sendMessage("You cannot use stairs during a zombie horde session!");
        return false;
    }
    
    @Override
    public boolean canLogout(Player player) {
        // Allow logout but warn about session ending
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            player.getPacketSender().sendMessage("Logging out will end your zombie horde session!");
        }
        return true;
    }
    
    @Override
    public String getName() {
        return AREA_NAME;
    }
    
    @Override
    public Location getRespawnLocation() {
        // Respawn at the exit location if player dies
        return ZombieHordeSurvival.EXIT;
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
        return ZombieHordeSurvival.ARENA_CENTER;
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
}