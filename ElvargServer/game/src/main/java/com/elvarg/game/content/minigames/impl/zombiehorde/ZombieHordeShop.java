package com.elvarg.game.content.minigames.impl.zombiehorde;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Item;

/**
 * Handles the Zombie Horde minigame shop system.
 * Players can purchase better equipment using Blood Money earned from waves.
 * 
 * @author Solo Coding
 */
public class ZombieHordeShop {
    
    /**
     * Shop item categories for tiered progression
     */
    public enum ShopCategory {
        WEAPONS,
        ARMOR,
        CONSUMABLES,
        UPGRADES
    }
    
    /**
     * Represents a shop item with its blood money cost
     */
    public static class ShopItem {
        private final Item item;
        private final int bloodMoneyCost;
        private final int waveRequirement;
        private final String description;
        
        public ShopItem(Item item, int bloodMoneyCost, int waveRequirement, String description) {
            this.item = item;
            this.bloodMoneyCost = bloodMoneyCost;
            this.waveRequirement = waveRequirement;
            this.description = description;
        }
        
        public Item getItem() { return item; }
        public int getBloodMoneyCost() { return bloodMoneyCost; }
        public int getWaveRequirement() { return waveRequirement; }
        public String getDescription() { return description; }
    }
    
    // Weapon progression tiers
    private static final ShopItem[] WEAPONS = {
        // Tier 1 - Available from start
        new ShopItem(new Item(1325, 1), 50, 1, "Steel scimitar - Basic upgrade"),
        new ShopItem(new Item(1347, 1), 75, 1, "Steel longsword - Balanced attack"),
        
        // Tier 2 - Wave 3+
        new ShopItem(new Item(1339, 1), 150, 3, "Mithril scimitar - Fast attacks"),
        new ShopItem(new Item(1353, 1), 200, 3, "Mithril longsword - Good damage"),
        
        // Tier 3 - Wave 5+
        new ShopItem(new Item(1345, 1), 350, 5, "Adamant scimitar - High speed"),
        new ShopItem(new Item(1359, 1), 450, 5, "Adamant longsword - High damage"),
        
        // Tier 4 - Wave 8+
        new ShopItem(new Item(1333, 1), 750, 8, "Rune scimitar - Excellent speed"),
        new ShopItem(new Item(1365, 1), 900, 8, "Rune longsword - Excellent damage"),
        
        // Tier 5 - Wave 12+
        new ShopItem(new Item(4151, 1), 1500, 12, "Abyssal whip - Elite weapon"),
        new ShopItem(new Item(1215, 1), 1800, 12, "Dragon dagger - Fast strikes"),
        
        // Tier 6 - Wave 15+
        new ShopItem(new Item(11694, 1), 2500, 15, "Armadyl godsword - Ultimate power"),
        new ShopItem(new Item(4587, 1), 3000, 15, "Dragon scimitar - Legendary weapon")
    };
    
    // Armor progression tiers
    private static final ShopItem[] ARMOR = {
        // Helmets
        new ShopItem(new Item(1159, 1), 40, 1, "Steel full helm"),
        new ShopItem(new Item(1163, 1), 120, 3, "Mithril full helm"),
        new ShopItem(new Item(1167, 1), 280, 5, "Adamant full helm"),
        new ShopItem(new Item(1171, 1), 600, 8, "Rune full helm"),
        new ShopItem(new Item(1149, 1), 1200, 12, "Dragon med helm"),
        
        // Platebodies
        new ShopItem(new Item(1119, 1), 80, 1, "Steel platebody"),
        new ShopItem(new Item(1123, 1), 240, 3, "Mithril platebody"),
        new ShopItem(new Item(1127, 1), 560, 5, "Adamant platebody"),
        new ShopItem(new Item(1131, 1), 1200, 8, "Rune platebody"),
        new ShopItem(new Item(1135, 1), 2400, 12, "Dragon platebody"),
        
        // Platelegs
        new ShopItem(new Item(1071, 1), 60, 1, "Steel platelegs"),
        new ShopItem(new Item(1075, 1), 180, 3, "Mithril platelegs"),
        new ShopItem(new Item(1079, 1), 420, 5, "Adamant platelegs"),
        new ShopItem(new Item(1083, 1), 900, 8, "Rune platelegs"),
        new ShopItem(new Item(4087, 1), 1800, 12, "Dragon platelegs"),
        
        // Boots
        new ShopItem(new Item(1031, 1), 30, 1, "Steel boots"),
        new ShopItem(new Item(1035, 1), 90, 3, "Mithril boots"),
        new ShopItem(new Item(1039, 1), 210, 5, "Adamant boots"),
        new ShopItem(new Item(1043, 1), 450, 8, "Rune boots"),
        new ShopItem(new Item(1061, 1), 900, 12, "Dragon boots")
    };
    
    // Consumables and supplies
    private static final ShopItem[] CONSUMABLES = {
        // Food
        new ShopItem(new Item(385, 5), 25, 1, "5x Sharks - Good healing"),
        new ShopItem(new Item(385, 10), 45, 1, "10x Sharks - Bulk food"),
        new ShopItem(new Item(391, 3), 40, 3, "3x Karambwans - Combo eating"),
        
        // Potions
        new ShopItem(new Item(2434, 3), 30, 1, "3x Prayer potions"),
        new ShopItem(new Item(2440, 3), 35, 1, "3x Super strength"),
        new ShopItem(new Item(2436, 3), 35, 1, "3x Super attack"),
        new ShopItem(new Item(2442, 3), 35, 1, "3x Super defence"),
        new ShopItem(new Item(2452, 1), 60, 5, "1x Super combat potion"),
        
        // Special items
        new ShopItem(new Item(560, 100), 50, 3, "100x Death runes"),
        new ShopItem(new Item(555, 1000), 30, 1, "1000x Air runes"),
        new ShopItem(new Item(892, 100), 15, 1, "100x Arrows")
    };
    
    // Special upgrades and utilities
    private static final ShopItem[] UPGRADES = {
        new ShopItem(new Item(1704, 1), 100, 3, "Amulet of strength"),
        new ShopItem(new Item(1712, 1), 250, 5, "Amulet of power"),
        new ShopItem(new Item(1718, 1), 500, 8, "Amulet of glory"),
        new ShopItem(new Item(6585, 1), 1000, 12, "Amulet of fury"),
        
        new ShopItem(new Item(1540, 1), 150, 3, "Anti-dragon shield"),
        new ShopItem(new Item(1201, 1), 400, 5, "Rune kiteshield"),
        new ShopItem(new Item(1187, 1), 800, 8, "Dragon sq shield"),
        
        new ShopItem(new Item(2412, 1), 200, 5, "Cape of legends"),
        new ShopItem(new Item(6570, 1), 800, 10, "Fire cape")
    };
    
    /**
     * Opens the shop interface for a specific category
     */
    public static void openShop(Player player, ShopCategory category) {
        ZombieHordeSession session = InstanceController.getSession(player);
        if (session == null) {
            player.getPacketSender().sendMessage("You need to be in a Zombie Horde session to access the shop!");
            return;
        }
        
        ShopItem[] items = getItemsForCategory(category);
        if (items == null) {
            player.getPacketSender().sendMessage("Invalid shop category!");
            return;
        }
        
        // Send shop header
        player.getPacketSender().sendMessage("<col=ff6600>=== Zombie Horde " + category.name() + " Shop ===</col>");
        player.getPacketSender().sendMessage("<col=00ff00>Your Blood Money: " + session.getTotalBloodMoneyEarned() + "</col>");
        player.getPacketSender().sendMessage("<col=ffff00>Current Wave: " + session.getCurrentWave() + "</col>");
        player.getPacketSender().sendMessage("");
        
        // List available items
        for (int i = 0; i < items.length; i++) {
            ShopItem shopItem = items[i];
            String status = "";
            
            if (session.getCurrentWave() < shopItem.getWaveRequirement()) {
                status = "<col=ff0000>[Locked - Wave " + shopItem.getWaveRequirement() + "+]</col>";
            } else if (session.getTotalBloodMoneyEarned() < shopItem.getBloodMoneyCost()) {
                status = "<col=ffaa00>[Cannot afford]</col>";
            } else {
                status = "<col=00ff00>[Available]</col>";
            }
            
            player.getPacketSender().sendMessage(
                "<col=ffffff>" + (i + 1) + ". " + shopItem.getDescription() + 
                " - " + shopItem.getBloodMoneyCost() + " BM</col> " + status
            );
        }
        
        player.getPacketSender().sendMessage("");
        player.getPacketSender().sendMessage("<col=ffff00>Use ::bhuy <number> to purchase an item</col>");
    }
    
    /**
     * Attempts to purchase an item from the shop
     */
    public static void purchaseItem(Player player, ShopCategory category, int itemIndex) {
        ZombieHordeSession session = InstanceController.getSession(player);
        if (session == null) {
            player.getPacketSender().sendMessage("You need to be in a Zombie Horde session to purchase items!");
            return;
        }
        
        ShopItem[] items = getItemsForCategory(category);
        if (items == null || itemIndex < 0 || itemIndex >= items.length) {
            player.getPacketSender().sendMessage("Invalid item selection!");
            return;
        }
        
        ShopItem shopItem = items[itemIndex];
        
        // Check wave requirement
        if (session.getCurrentWave() < shopItem.getWaveRequirement()) {
            player.getPacketSender().sendMessage(
                "<col=ff0000>You need to reach wave " + shopItem.getWaveRequirement() + 
                " to purchase this item!</col>"
            );
            return;
        }
        
        // Check blood money cost
        if (session.getTotalBloodMoneyEarned() < shopItem.getBloodMoneyCost()) {
            player.getPacketSender().sendMessage(
                "<col=ff0000>You need " + shopItem.getBloodMoneyCost() + 
                " Blood Money to purchase this item! (You have " + 
                session.getTotalBloodMoneyEarned() + ")</col>"
            );
            return;
        }
        
        // Check inventory space
        if (player.getInventory().isFull()) {
            player.getPacketSender().sendMessage("<col=ff0000>Your inventory is full!</col>");
            return;
        }
        
        // Purchase the item
        session.addBloodMoney(-shopItem.getBloodMoneyCost()); // Deduct cost
        player.getInventory().add(shopItem.getItem());
        
        player.getPacketSender().sendMessage(
            "<col=00ff00>Purchased " + shopItem.getDescription() + 
            " for " + shopItem.getBloodMoneyCost() + " Blood Money!</col>"
        );
        player.getPacketSender().sendMessage(
            "<col=ffff00>Remaining Blood Money: " + session.getTotalBloodMoneyEarned() + "</col>"
        );
    }
    
    /**
     * Gets the items array for a specific category
     */
    private static ShopItem[] getItemsForCategory(ShopCategory category) {
        switch (category) {
            case WEAPONS:
                return WEAPONS;
            case ARMOR:
                return ARMOR;
            case CONSUMABLES:
                return CONSUMABLES;
            case UPGRADES:
                return UPGRADES;
            default:
                return null;
        }
    }
    
    /**
     * Shows all shop categories to the player
     */
    public static void showShopMenu(Player player) {
        ZombieHordeSession session = InstanceController.getSession(player);
        if (session == null) {
            player.getPacketSender().sendMessage("You need to be in a Zombie Horde session to access the shop!");
            return;
        }
        
        player.getPacketSender().sendMessage("<col=ff6600>=== Zombie Horde Shop ===</col>");
        player.getPacketSender().sendMessage("<col=00ff00>Your Blood Money: " + session.getTotalBloodMoneyEarned() + "</col>");
        player.getPacketSender().sendMessage("<col=ffff00>Current Wave: " + session.getCurrentWave() + "</col>");
        player.getPacketSender().sendMessage("");
        player.getPacketSender().sendMessage("<col=ffffff>Shop Categories:</col>");
        player.getPacketSender().sendMessage("<col=ffffff>1. ::bhweapons - Weapons (swords, whips, etc.)</col>");
        player.getPacketSender().sendMessage("<col=ffffff>2. ::bharmor - Armor (helmets, bodies, legs, boots)</col>");
        player.getPacketSender().sendMessage("<col=ffffff>3. ::bhconsumables - Food, potions, and supplies</col>");
        player.getPacketSender().sendMessage("<col=ffffff>4. ::bhupgrades - Accessories and special items</col>");
        player.getPacketSender().sendMessage("");
        player.getPacketSender().sendMessage("<col=ffff00>Use the commands above to browse each category</col>");
    }
}