package com.elvarg.game.entity.impl.npc.impl;

import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.content.minigames.impl.ZombieHordeSurvival;
import com.elvarg.game.content.minigames.impl.zombiehorde.ZombieHordeSession;

/**
 * Represents a zombie NPC in the Zombie Horde Survival minigame.
 * Handles special behavior, death events, and integration with the wave system.
 * 
 * @author Solo Coding
 */
public class ZombieHordeNPC extends NPC {
    
    private final ZombieHordeSession session;
    private final int waveNumber;
    private final String zombieType;
    
    /**
     * Creates a new zombie horde NPC.
     * 
     * @param id The NPC ID
     * @param position The spawn position
     * @param session The zombie horde session this zombie belongs to
     * @param waveNumber The wave number this zombie was spawned in
     * @param zombieType The type/name of the zombie
     */
    public ZombieHordeNPC(int id, Location position, ZombieHordeSession session, int waveNumber, String zombieType) {
        super(id, position);
        this.session = session;
        this.waveNumber = waveNumber;
        this.zombieType = zombieType;
        
        // Aggression is handled by overriding isAggressiveTo() and aggressionDistance() methods
        
        // Set the target player
        if (session != null && session.getPlayer() != null) {
            this.getCombat().attack(session.getPlayer());
        }
    }
    
    @Override
    public void appendDeath() {
        super.appendDeath();
        
        // Notify the session about the zombie death
        if (session != null && !session.isSessionEnded()) {
            // Remove from session tracking
            session.removeActiveZombie(this);
            
            // Award blood money and increment kill count
            session.incrementKills();
            
            // Calculate blood money reward based on wave and zombie type
            int bloodMoneyReward = calculateBloodMoneyReward();
            session.addBloodMoney(bloodMoneyReward);
            
            // Send kill message to player
            Player player = session.getPlayer();
            if (player != null && player.isRegistered()) {
                player.getPacketSender().sendMessage(
                    "<col=ff0000>" + zombieType + " defeated! </col>" +
                    "<col=00ff00>+" + bloodMoneyReward + " Blood Money</col>"
                );
            }
        }
    }
    
    @Override
    public void process() {
        super.process();
        
        // Check if session is still valid
        if (session == null || session.isSessionEnded()) {
            this.remove();
            return;
        }
        
        // Check if player is still in the area
        Player player = session.getPlayer();
        if (player == null || !player.isRegistered()) {
            this.remove();
            return;
        }
        
        // Ensure zombie stays aggressive toward the player
        if (this.getLocation().getDistance(player.getLocation()) <= 15) {
            this.getCombat().attack(player);
        }
    }
    
    // Custom methods for zombie behavior (not overrides)
    
    @Override
    public void onAdd() {
        super.onAdd();
        
        // Send spawn message to player
        if (session != null && session.getPlayer() != null) {
            Player player = session.getPlayer();
            if (session.getZombiesSpawnedThisWave() == 1) {
                // First zombie of the wave
                player.getPacketSender().sendMessage("<col=ff8000>Wave " + waveNumber + " zombies are spawning!");
            }
        }
    }
    
    @Override
    public void remove() {
        // Remove from session tracking when removed
        if (session != null) {
            session.removeActiveZombie(this);
        }
        super.remove();
    }
    
    /**
     * Gets the zombie horde session this zombie belongs to.
     * 
     * @return The zombie horde session
     */
    public ZombieHordeSession getSession() {
        return session;
    }
    
    /**
     * Gets the wave number this zombie was spawned in.
     * 
     * @return The wave number
     */
    public int getWaveNumber() {
        return waveNumber;
    }
    
    /**
     * Gets the type/name of this zombie.
     * 
     * @return The zombie type
     */
    public String getZombieType() {
        return zombieType;
    }
    
    /**
     * Checks if this zombie is still part of an active session.
     * 
     * @return True if the session is active
     */
    public boolean isSessionActive() {
        return session != null && !session.isSessionEnded() && 
               session.getPlayer() != null && session.getPlayer().isRegistered();
    }
    
    /**
     * Forces the zombie to target the session player.
     */
    public void targetSessionPlayer() {
        if (session != null && session.getPlayer() != null && session.getPlayer().isRegistered()) {
            this.getCombat().attack(session.getPlayer());
        }
    }
    
    /**
     * Gets the distance to the session player.
     * 
     * @return The distance to the player, or -1 if no valid player
     */
    public int getDistanceToPlayer() {
        if (session != null && session.getPlayer() != null) {
            return this.getLocation().getDistance(session.getPlayer().getLocation());
        }
        return -1;
    }
    
    /**
     * Calculates the blood money reward for killing this zombie.
     * 
     * @return The blood money reward amount
     */
    private int calculateBloodMoneyReward() {
        if (session == null) {
            return 0;
        }
        
        int currentWave = session.getCurrentWave();
        int baseReward = 0;
        
        // Base reward depends on zombie type
        switch (zombieType.toLowerCase()) {
            case "basic zombie":
                baseReward = 8;
                break;
            case "fast zombie":
                baseReward = 12;
                break;
            case "strong zombie":
                baseReward = 15;
                break;
            case "boss zombie":
                baseReward = 25;
                break;
            case "elite zombie":
                baseReward = 35;
                break;
            default:
                baseReward = 10; // Default reward
                break;
        }
        
        // Scale reward by wave (1.5x per wave after wave 1)
        double waveMultiplier = 1.0 + (currentWave - 1) * 0.15;
        int finalReward = (int) Math.round(baseReward * waveMultiplier);
        
        // Cap the reward to prevent inflation
        return Math.min(finalReward, baseReward * 5); // Max 5x base reward
    }
    
    /**
     * Checks if the zombie should despawn due to inactivity or distance.
     * 
     * @return True if the zombie should despawn
     */
    public boolean shouldDespawn() {
        // Despawn if session is invalid
        if (!isSessionActive()) {
            return true;
        }
        
        // Despawn if too far from player for too long
        int distanceToPlayer = getDistanceToPlayer();
        if (distanceToPlayer > 30) {
            return true;
        }
        
        // Despawn if player is not in the area
        if (!session.getArea().contains(session.getPlayer())) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isAggressiveTo(Player player) {
        // Zombies are aggressive only toward their session player
        if (session != null && session.getPlayer() != null) {
            return session.getPlayer().equals(player);
        }
        return false;
    }
    
    @Override
    public int aggressionDistance() {
        // Large aggression range for zombies
        return 15;
    }
}