package com.elvarg.game.content.minigames.impl.zombiehorde;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.areas.impl.ZombieHordeArea;
import com.elvarg.game.entity.impl.npc.NPC;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an individual player's Zombie Horde Survival session.
 * Tracks progress, spawned zombies, rewards, and session state.
 * 
 * @author Solo Coding
 */
public class ZombieHordeSession {
    
    private final Player player;
    private final ZombieHordeArea area;
    private final long sessionStartTime;
    
    // Wave progression
    private int currentWave;
    private int totalKills;
    private int waveKills;
    private int totalBloodMoneyEarned;
    
    // Wave management
    private boolean waveActive;
    private boolean countdownActive;
    private int countdownSeconds;
    private long lastCountdownUpdate;
    
    // Spawned zombies for current wave
    private final List<NPC> activeZombies;
    private int zombiesSpawnedThisWave;
    private int zombiesToSpawnThisWave;
    
    // Session state
    private boolean sessionEnded;
    private long lastActivity;
    
    // Statistics
    private int highestWaveReached;
    private long totalSessionTime;
    
    /**
     * Creates a new zombie horde session.
     * 
     * @param player The player participating in the session
     * @param area The private area instance for this session
     */
    public ZombieHordeSession(Player player, ZombieHordeArea area) {
        this.player = player;
        this.area = area;
        this.sessionStartTime = System.currentTimeMillis();
        
        // Initialize wave data
        this.currentWave = 0; // Will be incremented to 1 when first wave starts
        this.totalKills = 0;
        this.waveKills = 0;
        this.totalBloodMoneyEarned = 0;
        
        // Initialize wave state
        this.waveActive = false;
        this.countdownActive = false;
        this.countdownSeconds = 0;
        this.lastCountdownUpdate = 0;
        
        // Initialize zombie tracking
        this.activeZombies = new CopyOnWriteArrayList<>();
        this.zombiesSpawnedThisWave = 0;
        this.zombiesToSpawnThisWave = 0;
        
        // Initialize session state
        this.sessionEnded = false;
        this.lastActivity = System.currentTimeMillis();
        
        // Initialize statistics
        this.highestWaveReached = 0;
        this.totalSessionTime = 0;
    }
    
    /**
     * Updates the last activity timestamp.
     */
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }
    
    /**
     * Increments the current wave number.
     */
    public void incrementWave() {
        this.currentWave++;
        this.waveKills = 0;
        this.zombiesSpawnedThisWave = 0;
        this.zombiesToSpawnThisWave = 0;
        
        if (this.currentWave > this.highestWaveReached) {
            this.highestWaveReached = this.currentWave;
        }
        
        updateActivity();
    }
    
    /**
     * Increments the kill count for current wave and total.
     */
    public void incrementKills() {
        this.waveKills++;
        this.totalKills++;
        updateActivity();
    }
    
    /**
     * Adds blood money to the total earned.
     * 
     * @param amount The amount of blood money to add
     */
    public void addBloodMoney(int amount) {
        this.totalBloodMoneyEarned += amount;
        updateActivity();
    }
    
    /**
     * Gets the total Blood Money earned this session.
     * 
     * @return The total Blood Money earned
     */
    public int getTotalBloodMoneyEarned() {
        return totalBloodMoneyEarned;
    }
    
    /**
     * Completes the current wave and starts the next one.
     */
    public void completeWave() {
        if (sessionEnded) {
            return;
        }
        
        try {
            // Award wave rewards
            RewardManager.awardWaveReward(player, currentWave);
            
            // Track total Blood Money earned
            int waveReward = RewardManager.calculateWaveReward(currentWave);
            totalBloodMoneyEarned += waveReward;
            
            // Check for milestone rewards
            RewardManager.checkMilestoneReward(player, currentWave);
            
            player.getPacketSender().sendMessage(
                "<col=00ff00>Wave " + currentWave + " completed!</col>"
            );
            
            updateActivity();
            
        } catch (Exception e) {
            System.err.println("Error completing wave: " + e.getMessage());
        }
    }
    
    /**
     * Adds a zombie to the active zombies list.
     * 
     * @param zombie The zombie NPC to add
     */
    public void addActiveZombie(NPC zombie) {
        this.activeZombies.add(zombie);
        this.zombiesSpawnedThisWave++;
        updateActivity();
    }
    
    /**
     * Removes a zombie from the active zombies list.
     * 
     * @param zombie The zombie NPC to remove
     */
    public void removeActiveZombie(NPC zombie) {
        this.activeZombies.remove(zombie);
        updateActivity();
    }
    
    /**
     * Clears all active zombies.
     */
    public void clearActiveZombies() {
        // Remove all zombies from the world
        for (NPC zombie : activeZombies) {
            if (zombie != null && zombie.isRegistered()) {
                zombie.remove();
            }
        }
        activeZombies.clear();
        updateActivity();
    }
    
    /**
     * Starts the wave countdown.
     * 
     * @param seconds The number of seconds for the countdown
     */
    public void startCountdown(int seconds) {
        this.countdownActive = true;
        this.countdownSeconds = seconds;
        this.lastCountdownUpdate = System.currentTimeMillis();
        updateActivity();
    }
    
    /**
     * Updates the countdown timer.
     * 
     * @return True if countdown is still active, false if finished
     */
    public boolean updateCountdown() {
        if (!countdownActive) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCountdownUpdate >= 1000) { // 1 second passed
            countdownSeconds--;
            lastCountdownUpdate = currentTime;
            
            if (countdownSeconds <= 0) {
                countdownActive = false;
                return false;
            }
            
            // Send countdown message to player
            if (countdownSeconds <= 5 || countdownSeconds % 5 == 0) {
                player.getPacketSender().sendMessage("Next wave in " + countdownSeconds + " seconds...");
            }
        }
        
        return true;
    }
    
    /**
     * Checks if the session should end due to inactivity or other conditions.
     * 
     * @return True if the session should end
     */
    public boolean shouldEnd() {
        // Check if player is offline
        if (!player.isRegistered()) {
            return true;
        }
        
        // Check if player left the area
        if (!area.contains(player)) {
            return true;
        }
        
        // Check for inactivity (30 minutes)
        long inactiveTime = System.currentTimeMillis() - lastActivity;
        if (inactiveTime > 30 * 60 * 1000) { // 30 minutes
            return true;
        }
        
        return sessionEnded;
    }
    
    /**
     * Marks the session as ended.
     */
    public void endSession() {
        this.sessionEnded = true;
        this.totalSessionTime = System.currentTimeMillis() - sessionStartTime;
        clearActiveZombies();
    }
    
    /**
     * Ends the zombie horde session with a reason.
     * 
     * @param reason The reason for ending the session
     */
    public void endSession(String reason) {
        if (sessionEnded) {
            return;
        }
        
        try {
            endSession();
            
            // Let InstanceController handle the cleanup and rewards
            InstanceController.endSession(player, reason);
            
        } catch (Exception e) {
            System.err.println("Error ending session: " + e.getMessage());
        }
    }
    
    // Getters and setters
    
    public Player getPlayer() {
        return player;
    }
    
    public ZombieHordeArea getArea() {
        return area;
    }
    
    public int getCurrentWave() {
        return currentWave;
    }
    
    public int getTotalKills() {
        return totalKills;
    }
    
    public int getWaveKills() {
        return waveKills;
    }
    
    public int getTotalBloodMoneyEarned() {
        return totalBloodMoneyEarned;
    }
    
    public boolean isWaveActive() {
        return waveActive;
    }
    
    public void setWaveActive(boolean waveActive) {
        this.waveActive = waveActive;
        updateActivity();
    }
    
    public boolean isCountdownActive() {
        return countdownActive;
    }
    
    public int getCountdownSeconds() {
        return countdownSeconds;
    }
    
    public List<NPC> getActiveZombies() {
        return new ArrayList<>(activeZombies);
    }
    
    public int getZombiesSpawnedThisWave() {
        return zombiesSpawnedThisWave;
    }
    
    public int getZombiesToSpawnThisWave() {
        return zombiesToSpawnThisWave;
    }
    
    public void setZombiesToSpawnThisWave(int zombiesToSpawnThisWave) {
        this.zombiesToSpawnThisWave = zombiesToSpawnThisWave;
    }
    
    public boolean isSessionEnded() {
        return sessionEnded;
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    public int getHighestWaveReached() {
        return highestWaveReached;
    }
    
    public long getTotalSessionTime() {
        if (sessionEnded) {
            return totalSessionTime;
        }
        return System.currentTimeMillis() - sessionStartTime;
    }
    
    public long getSessionStartTime() {
        return sessionStartTime;
    }
}