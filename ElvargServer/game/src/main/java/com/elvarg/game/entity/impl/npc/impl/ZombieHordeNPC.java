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
        
        // Set the zombie to be aggressive toward the player
        this.setAggressive(true);
        this.setAggressionDistance(15); // Large aggression range
        
        // Set the target player
        if (session != null && session.getPlayer() != null) {
            this.getCombat().attack(session.getPlayer());
        }
    }
    
    @Override
    public void onDeath() {
        super.onDeath();
        
        // Notify the session about the zombie death
        if (session != null && !session.isSessionEnded()) {
            ZombieHordeSurvival.onZombieKilled(session, this.getId());
            
            // Remove from session tracking
            session.removeActiveZombie(this);
            
            // Send kill message to player
            Player player = session.getPlayer();
            if (player != null && player.isRegistered()) {
                player.getPacketSender().sendMessage("<col=ff0000>" + zombieType + " defeated! (" + 
                    (session.getZombiesToSpawnThisWave() - session.getActiveZombies().size()) + "/" + 
                    session.getZombiesToSpawnThisWave() + ")");
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
        if (player == null || !player.isRegistered() || !session.getArea().contains(player)) {
            this.remove();
            return;
        }
        
        // Ensure zombie stays aggressive toward the player
        if (!this.getCombat().isBeingAttacked() && !this.getCombat().isAttacking()) {
            if (this.getLocation().getDistance(player.getLocation()) <= 15) {
                this.getCombat().attack(player);
            }
        }
        
        // Prevent zombie from wandering too far from the arena
        Location arenaCenter = ZombieHordeSurvival.ARENA_CENTER;
        if (this.getLocation().getDistance(arenaCenter) > 25) {
            // Move zombie back toward the arena center
            this.getMovementQueue().walkTo(arenaCenter);
        }
    }
    
    @Override
    public boolean canAttack(Player player) {
        // Only allow attacking the session player
        if (session != null && session.getPlayer() != null) {
            return session.getPlayer().equals(player);
        }
        return false;
    }
    
    @Override
    public boolean canBeAttacked(Player player) {
        // Only allow the session player to attack this zombie
        if (session != null && session.getPlayer() != null) {
            return session.getPlayer().equals(player);
        }
        return false;
    }
    
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
}