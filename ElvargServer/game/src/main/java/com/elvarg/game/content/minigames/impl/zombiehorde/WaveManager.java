package com.elvarg.game.content.minigames.impl.zombiehorde;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.npc.NPC;
import com.elvarg.game.entity.impl.npc.impl.ZombieHordeNPC;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Location;
import com.elvarg.game.content.minigames.impl.ZombieHordeSurvival;
import com.elvarg.util.NpcIdentifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages wave progression, zombie spawning, and difficulty scaling
 * for the Zombie Horde Survival minigame.
 * 
 * @author Solo Coding
 */
public class WaveManager {
    
    private static final Random random = new Random();
    
    // Spawn locations around the arena (relative to center)
    private static final Location[] SPAWN_OFFSETS = {
        new Location(-15, -15), new Location(0, -15), new Location(15, -15),
        new Location(-15, 0), new Location(15, 0),
        new Location(-15, 15), new Location(0, 15), new Location(15, 15)
    };
    
    // Zombie NPC IDs with their relative strength
    private static final ZombieType[] ZOMBIE_TYPES = {
        new ZombieType(NpcIdentifiers.ZOMBIE, 1, "Zombie"),
        new ZombieType(NpcIdentifiers.ZOMBIE_2, 2, "Zombie"),
        new ZombieType(NpcIdentifiers.ZOMBIE_3, 3, "Zombie"),
        new ZombieType(NpcIdentifiers.ZOMBIE_4, 4, "Zombie"),
        new ZombieType(NpcIdentifiers.ZOMBIE_5, 5, "Zombie"),
        new ZombieType(NpcIdentifiers.ZOMBIE_6, 6, "Zombie Warrior"),
        new ZombieType(NpcIdentifiers.ZOMBIE_7, 7, "Zombie Warrior"),
        new ZombieType(NpcIdentifiers.ZOMBIE_8, 8, "Zombie Brute"),
        new ZombieType(NpcIdentifiers.ZOMBIE_9, 9, "Zombie Brute"),
        new ZombieType(NpcIdentifiers.ZOMBIE_10, 10, "Elite Zombie")
    };
    
    // Wave configuration constants
    private static final int BASE_ZOMBIES_PER_WAVE = 3;
    private static final int MAX_ZOMBIES_PER_WAVE = 25;
    private static final double ZOMBIE_COUNT_SCALING = 0.5;
    private static final double HP_SCALING_PER_WAVE = 0.1;
    private static final double DAMAGE_SCALING_PER_WAVE = 0.05;
    
    /**
     * Starts a new wave for the given session.
     * 
     * @param session The zombie horde session
     * @param waveNumber The wave number to start
     */
    public void startWave(ZombieHordeSession session, int waveNumber) {
        Player player = session.getPlayer();
        
        // Clear any remaining zombies from previous wave
        session.clearActiveZombies();
        
        // Calculate zombies for this wave
        int zombieCount = calculateZombieCount(waveNumber);
        session.setZombiesToSpawnThisWave(zombieCount);
        session.setWaveActive(true);
        
        // Send wave start message
        player.getPacketSender().sendMessage("<col=ff0000>Wave " + waveNumber + " begins! " + zombieCount + " zombies incoming!");
        
        // Spawn zombies with a slight delay between each
        spawnWaveZombies(session, waveNumber);
    }
    
    /**
     * Starts the countdown before the next wave.
     * 
     * @param session The zombie horde session
     */
    public void startWaveCountdown(ZombieHordeSession session) {
        session.setWaveActive(false);
        session.startCountdown(ZombieHordeSurvival.WAVE_COUNTDOWN_SECONDS);
        
        Player player = session.getPlayer();
        player.getPacketSender().sendMessage("<col=00ff00>Wave " + session.getCurrentWave() + " completed!");
        player.getPacketSender().sendMessage("Next wave starts in " + ZombieHordeSurvival.WAVE_COUNTDOWN_SECONDS + " seconds...");
    }
    
    /**
     * Processes a zombie horde session each game tick.
     * 
     * @param session The session to process
     */
    public void processSession(ZombieHordeSession session) {
        // Update countdown if active
        if (session.isCountdownActive()) {
            if (!session.updateCountdown()) {
                // Countdown finished, start next wave
                startWave(session, session.getCurrentWave() + 1);
            }
        }
        
        // Clean up dead zombies
        cleanupDeadZombies(session);
    }
    
    /**
     * Checks if the current wave is completed.
     * 
     * @param session The zombie horde session
     * @return True if all zombies are defeated
     */
    public boolean isWaveCompleted(ZombieHordeSession session) {
        if (!session.isWaveActive()) {
            return false;
        }
        
        // Check if all zombies for this wave have been spawned and killed
        boolean allSpawned = session.getZombiesSpawnedThisWave() >= session.getZombiesToSpawnThisWave();
        boolean allKilled = session.getActiveZombies().isEmpty();
        
        return allSpawned && allKilled;
    }
    
    /**
     * Spawns zombies for the current wave.
     * 
     * @param session The zombie horde session
     * @param waveNumber The current wave number
     */
    private void spawnWaveZombies(ZombieHordeSession session, int waveNumber) {
        int zombiesToSpawn = session.getZombiesToSpawnThisWave();
        
        for (int i = 0; i < zombiesToSpawn; i++) {
            // Determine zombie type based on wave number
            ZombieType zombieType = selectZombieType(waveNumber);
            
            // Get spawn location
            Location spawnLocation = getRandomSpawnLocation(session);
            
            // Create and configure zombie
            NPC zombie = createZombie(zombieType, spawnLocation, waveNumber, session);
            
            if (zombie != null) {
                // Add to session tracking
                session.addActiveZombie(zombie);
                
                // Set combat target to player
                zombie.getCombat().attack(session.getPlayer());
            }
        }
    }
    
    /**
     * Creates a zombie NPC with scaled stats.
     * 
     * @param zombieType The type of zombie to create
     * @param location The spawn location
     * @param waveNumber The current wave number
     * @param session The zombie horde session
     * @return The created zombie NPC
     */
    private NPC createZombie(ZombieType zombieType, Location location, int waveNumber, ZombieHordeSession session) {
        try {
            // Create zombie horde NPC with session integration
            ZombieHordeNPC zombie = new ZombieHordeNPC(
                zombieType.getId(), 
                location, 
                session, 
                waveNumber, 
                zombieType.getName()
            );
            
            // Scale stats based on wave number
            scaleZombieStats(zombie, zombieType, waveNumber);
            
            // Register zombie in the world
            World.getAddNPCQueue().add(zombie);
            
            return zombie;
        } catch (Exception e) {
            System.err.println("Failed to create zombie: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Scales zombie stats based on wave number and zombie type.
     * 
     * @param zombie The zombie NPC
     * @param zombieType The zombie type
     * @param waveNumber The current wave number
     */
    private void scaleZombieStats(NPC zombie, ZombieType zombieType, int waveNumber) {
        // Base stats from NPC definition
        int baseHp = zombie.getDefinition().getHitpoints();
        int baseDamage = zombie.getDefinition().getMaxHit();
        
        // Apply wave scaling
        double hpMultiplier = 1.0 + (waveNumber * HP_SCALING_PER_WAVE);
        double damageMultiplier = 1.0 + (waveNumber * DAMAGE_SCALING_PER_WAVE);
        
        // Apply zombie type scaling
        hpMultiplier *= zombieType.getStrength();
        damageMultiplier *= zombieType.getStrength();
        
        // Set scaled stats
        int scaledHp = (int) (baseHp * hpMultiplier);
        int scaledDamage = (int) (baseDamage * damageMultiplier);
        
        zombie.setHitpoints(scaledHp);
        zombie.getDefinition().setMaxHit(scaledDamage);
        
        // Set constitution to match hitpoints
        zombie.setConstitution(scaledHp);
    }
    
    /**
     * Selects an appropriate zombie type for the given wave.
     * 
     * @param waveNumber The current wave number
     * @return The selected zombie type
     */
    private ZombieType selectZombieType(int waveNumber) {
        // Early waves: basic zombies only
        if (waveNumber <= 5) {
            return ZOMBIE_TYPES[random.nextInt(Math.min(3, ZOMBIE_TYPES.length))];
        }
        
        // Mid waves: mix of basic and stronger zombies
        if (waveNumber <= 15) {
            return ZOMBIE_TYPES[random.nextInt(Math.min(6, ZOMBIE_TYPES.length))];
        }
        
        // Late waves: all zombie types with bias toward stronger ones
        if (waveNumber <= 30) {
            int index = random.nextInt(ZOMBIE_TYPES.length);
            // 60% chance to pick from stronger half
            if (random.nextDouble() < 0.6) {
                index = ZOMBIE_TYPES.length / 2 + random.nextInt(ZOMBIE_TYPES.length / 2);
            }
            return ZOMBIE_TYPES[index];
        }
        
        // Very late waves: mostly elite zombies
        int index = ZOMBIE_TYPES.length - 1 - random.nextInt(3);
        return ZOMBIE_TYPES[Math.max(0, index)];
    }
    
    /**
     * Gets a random spawn location around the arena.
     * 
     * @param session The zombie horde session
     * @return A random spawn location
     */
    private Location getRandomSpawnLocation(ZombieHordeSession session) {
        Location center = ZombieHordeSurvival.ARENA_CENTER;
        Location offset = SPAWN_OFFSETS[random.nextInt(SPAWN_OFFSETS.length)];
        
        return new Location(
            center.getX() + offset.getX(),
            center.getY() + offset.getY(),
            center.getZ()
        );
    }
    
    /**
     * Calculates the number of zombies for a given wave.
     * 
     * @param waveNumber The wave number
     * @return The number of zombies to spawn
     */
    private int calculateZombieCount(int waveNumber) {
        double count = BASE_ZOMBIES_PER_WAVE + (waveNumber * ZOMBIE_COUNT_SCALING);
        return Math.min((int) Math.ceil(count), MAX_ZOMBIES_PER_WAVE);
    }
    
    /**
     * Removes dead zombies from the session tracking.
     * 
     * @param session The zombie horde session
     */
    private void cleanupDeadZombies(ZombieHordeSession session) {
        List<NPC> toRemove = new ArrayList<>();
        
        for (NPC zombie : session.getActiveZombies()) {
            if (zombie == null || !zombie.isRegistered() || zombie.isDying() || zombie.getHitpoints() <= 0) {
                toRemove.add(zombie);
            }
        }
        
        for (NPC zombie : toRemove) {
            session.removeActiveZombie(zombie);
        }
    }
    
    /**
     * Represents a type of zombie with its properties.
     */
    private static class ZombieType {
        private final int id;
        private final double strength;
        private final String name;
        
        public ZombieType(int id, double strength, String name) {
            this.id = id;
            this.strength = strength;
            this.name = name;
        }
        
        public int getId() {
            return id;
        }
        
        public double getStrength() {
            return strength;
        }
        
        public String getName() {
            return name;
        }
    }
}