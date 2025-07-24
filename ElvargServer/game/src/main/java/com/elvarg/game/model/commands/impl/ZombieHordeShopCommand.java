package com.elvarg.game.model.commands.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.commands.Command;
import com.elvarg.game.content.minigames.impl.zombiehorde.ZombieHordeShop;

public class ZombieHordeShopCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        String cmd = command.toLowerCase();
        
        // Main shop menu
        if (cmd.equals("bhshop") || cmd.equals("zombieshop")) {
            ZombieHordeShop.showShopMenu(player);
            return;
        }
        
        // Category browsers
        if (cmd.equals("bhweapons")) {
            ZombieHordeShop.openShop(player, ZombieHordeShop.ShopCategory.WEAPONS);
            return;
        }
        
        if (cmd.equals("bharmor")) {
            ZombieHordeShop.openShop(player, ZombieHordeShop.ShopCategory.ARMOR);
            return;
        }
        
        if (cmd.equals("bhconsumables") || cmd.equals("bhfood")) {
            ZombieHordeShop.openShop(player, ZombieHordeShop.ShopCategory.CONSUMABLES);
            return;
        }
        
        if (cmd.equals("bhupgrades") || cmd.equals("bhaccessories")) {
            ZombieHordeShop.openShop(player, ZombieHordeShop.ShopCategory.UPGRADES);
            return;
        }
        
        // Purchase commands
        if (cmd.equals("bhbuy") || cmd.equals("bhpurchase")) {
            if (parts.length < 3) {
                player.getPacketSender().sendMessage("Usage: ::bhbuy <category> <number>");
                player.getPacketSender().sendMessage("Categories: weapons, armor, consumables, upgrades");
                player.getPacketSender().sendMessage("Example: ::bhbuy weapons 1");
                return;
            }
            
            String categoryStr = parts[1].toLowerCase();
            ZombieHordeShop.ShopCategory category = null;
            
            if (categoryStr.equals("weapons") || categoryStr.equals("weapon")) {
                category = ZombieHordeShop.ShopCategory.WEAPONS;
            } else if (categoryStr.equals("armor") || categoryStr.equals("armour")) {
                category = ZombieHordeShop.ShopCategory.ARMOR;
            } else if (categoryStr.equals("consumables") || categoryStr.equals("food") || categoryStr.equals("supplies")) {
                category = ZombieHordeShop.ShopCategory.CONSUMABLES;
            } else if (categoryStr.equals("upgrades") || categoryStr.equals("accessories")) {
                category = ZombieHordeShop.ShopCategory.UPGRADES;
            } else {
                player.getPacketSender().sendMessage("Invalid category! Use: weapons, armor, consumables, upgrades");
                return;
            }
            
            try {
                int itemNumber = Integer.parseInt(parts[2]);
                ZombieHordeShop.purchaseItem(player, category, itemNumber - 1); // Convert to 0-based index
            } catch (NumberFormatException e) {
                player.getPacketSender().sendMessage("Invalid item number! Use a number from the shop list.");
            }
            return;
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true; // Anyone can try to use the shop (it will check if they're in a session)
    }
}