package com.elvarg.game.content.minigames.impl.zombiehorde;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;
import com.elvarg.util.ItemIdentifiers;

/**
 * Manages rewards for the Zombie Horde Survival minigame.
 * Handles Blood Money distribution and milestone rewards.
 * 
 * @author Elvarg Development Team
 */
public class RewardManager {
    
    // Base Blood Money reward per wave (reduced for economy balance)
    private static final int BASE_BLOOD_MONEY = 15;
    
    // Milestone wave interval (every 15 waves for better balance)
    private static final int MILESTONE_INTERVAL = 15;
    
    // Bonus Blood Money for milestones (reduced significantly)
    private static final int MILESTONE_BONUS = 100;
    
    /**
     * Calculates Blood Money reward for completing a wave.
     * Formula: Base 15 BM + (wave × 3) with diminishing returns after wave 20
     * 
     * @param waveNumber The completed wave number
     * @return The Blood Money amount to award
     */
    public static int calculateWaveReward(int waveNumber) {
        int baseReward = BASE_BLOOD_MONEY + (waveNumber * 3);
        
        // Apply diminishing returns after wave 20 to prevent inflation
        if (waveNumber > 20) {
            int excessWaves = waveNumber - 20;
            double diminishingFactor = 1.0 / (1.0 + (excessWaves * 0.1));
            baseReward = (int) (baseReward * diminishingFactor);
        }
        
        // Cap maximum reward per wave to 150 Blood Money
        return Math.min(baseReward, 150);
    }
    
    /**
     * Awards Blood Money to a player for completing a wave.
     * 
     * @param player The player to reward
     * @param waveNumber The completed wave number
     */
    public static void awardWaveReward(Player player, int waveNumber) {
        int bloodMoney = calculateWaveReward(waveNumber);
        
        // Add Blood Money to player's inventory
        Item bloodMoneyItem = new Item(ItemIdentifiers.BLOOD_MONEY, bloodMoney);
        player.getInventory().add(bloodMoneyItem);
        
        // Send reward message
        player.getPacketSender().sendMessage(
            "<col=ff0000>Wave " + waveNumber + " completed! You earned " + 
            bloodMoney + " Blood Money!</col>"
        );
    }
    
    /**
     * Checks if a wave is a milestone and awards bonus rewards.
     * 
     * @param player The player to reward
     * @param waveNumber The completed wave number
     */
    public static void checkMilestoneReward(Player player, int waveNumber) {
        if (waveNumber % MILESTONE_INTERVAL == 0) {
            awardMilestoneReward(player, waveNumber);
        }
    }
    
    /**
     * Awards milestone rewards for reaching every 10th wave.
     * 
     * @param player The player to reward
     * @param waveNumber The milestone wave number
     */
    private static void awardMilestoneReward(Player player, int waveNumber) {
        // Award bonus Blood Money
        Item bonusBloodMoney = new Item(ItemIdentifiers.BLOOD_MONEY, MILESTONE_BONUS);
        player.getInventory().add(bonusBloodMoney);
        
        // Send milestone message
        player.getPacketSender().sendMessage(
            "<col=ff6600>MILESTONE REACHED! Wave " + waveNumber + 
            " completed! Bonus: " + MILESTONE_BONUS + " Blood Money!</col>"
        );
        
        // Broadcast achievement for significant milestones
        if (waveNumber >= 50 && waveNumber % 25 == 0) {
            String broadcastMessage = player.getUsername() + 
                " has reached wave " + waveNumber + " in Zombie Horde Survival!";
            
            // Send broadcast to all online players
            com.elvarg.game.World.getPlayers().forEach(p -> {
                if (p != null) {
                    p.getPacketSender().sendMessage(
                        "<col=ff0000>[Zombie Horde] " + broadcastMessage + "</col>"
                    );
                }
            });
        }
        
        // Award special items for major milestones
        awardSpecialMilestoneItems(player, waveNumber);
    }
    
    /**
     * Awards special items for major milestone achievements.
     * 
     * @param player The player to reward
     * @param waveNumber The milestone wave number
     */
    private static void awardSpecialMilestoneItems(Player player, int waveNumber) {
        switch (waveNumber) {
            case 30:
                // Award a special item for wave 30
                player.getInventory().add(new Item(ItemIdentifiers.COINS, 50000));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>Special reward: 50,000 coins for reaching wave 30!</col>"
                );
                break;
                
            case 60:
                // Award a rare item for wave 60
                player.getInventory().add(new Item(ItemIdentifiers.DRAGON_BONES, 15));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>Special reward: 15 Dragon bones for reaching wave 60!</col>"
                );
                break;
                
            case 100:
                // Award an extremely rare item for wave 100
                player.getInventory().add(new Item(ItemIdentifiers.BLOOD_MONEY, 1000));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>LEGENDARY REWARD: 1,000 Blood Money for reaching wave 100!</col>"
                );
                break;
                
            default:
                // For other major milestones (every 75 waves after 100)
                if (waveNumber >= 175 && waveNumber % 75 == 0) {
                    int extraBloodMoney = waveNumber * 3; // Reduced scaling bonus
                    player.getInventory().add(new Item(ItemIdentifiers.BLOOD_MONEY, extraBloodMoney));
                    player.getPacketSender().sendMessage(
                        "<col=ffff00>Epic milestone bonus: " + extraBloodMoney + " Blood Money!</col>"
                    );
                }
                break;
        }
    }
    
    /**
     * Awards final rewards when a player's session ends.
     * 
     * @param player The player to reward
     * @param finalWave The highest wave reached
     * @param totalBloodMoney Total Blood Money earned during the session
     */
    public static void awardSessionEndRewards(Player player, int finalWave, int totalBloodMoney) {
        // Send session summary
        player.getPacketSender().sendMessage(
            "<col=ff0000>=== Zombie Horde Session Complete ===</col>"
        );
        player.getPacketSender().sendMessage(
            "<col=ffffff>Highest Wave: " + finalWave + "</col>"
        );
        player.getPacketSender().sendMessage(
            "<col=ffffff>Total Blood Money Earned: " + totalBloodMoney + "</col>"
        );
        
        // Award participation bonus based on performance (reduced for balance)
        int participationBonus = Math.min(finalWave * 2, 250); // Cap at 250
        if (participationBonus > 0) {
            player.getInventory().add(new Item(ItemIdentifiers.BLOOD_MONEY, participationBonus));
            player.getPacketSender().sendMessage(
                "<col=00ff00>Participation bonus: " + participationBonus + " Blood Money!</col>"
            );
        }
    }
    
    /**
     * Gets the base Blood Money reward amount.
     * 
     * @return The base Blood Money amount per wave
     */
    public static int getBaseBloodMoney() {
        return BASE_BLOOD_MONEY;
    }
    
    /**
     * Gets the milestone interval.
     * 
     * @return The number of waves between milestones
     */
    public static int getMilestoneInterval() {
        return MILESTONE_INTERVAL;
    }
    
    /**
     * Gets the milestone bonus amount.
     * 
     * @return The bonus Blood Money for milestones
     */
    public static int getMilestoneBonus() {
        return MILESTONE_BONUS;
    }
}