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
    
    // Base Blood Money reward per wave
    private static final int BASE_BLOOD_MONEY = 50;
    
    // Milestone wave interval (every 10 waves)
    private static final int MILESTONE_INTERVAL = 10;
    
    // Bonus Blood Money for milestones
    private static final int MILESTONE_BONUS = 500;
    
    /**
     * Calculates Blood Money reward for completing a wave.
     * Formula: Base 50 BM × current wave number
     * 
     * @param waveNumber The completed wave number
     * @return The Blood Money amount to award
     */
    public static int calculateWaveReward(int waveNumber) {
        return BASE_BLOOD_MONEY * waveNumber;
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
            case 25:
                // Award a special item for wave 25
                player.getInventory().add(new Item(ItemIdentifiers.COINS, 100000));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>Special reward: 100,000 coins for reaching wave 25!</col>"
                );
                break;
                
            case 50:
                // Award a rare item for wave 50
                player.getInventory().add(new Item(ItemIdentifiers.DRAGON_BONES, 50));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>Special reward: 50 Dragon bones for reaching wave 50!</col>"
                );
                break;
                
            case 100:
                // Award an extremely rare item for wave 100
                player.getInventory().add(new Item(ItemIdentifiers.RUNE_PLATEBODY, 1));
                player.getPacketSender().sendMessage(
                    "<col=ffff00>LEGENDARY REWARD: Rune platebody for reaching wave 100!</col>"
                );
                break;
                
            default:
                // For other major milestones (every 50 waves after 100)
                if (waveNumber >= 150 && waveNumber % 50 == 0) {
                    int extraBloodMoney = waveNumber * 10; // Scaling bonus
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
        
        // Award participation bonus based on performance
        int participationBonus = Math.min(finalWave * 5, 1000); // Cap at 1000
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