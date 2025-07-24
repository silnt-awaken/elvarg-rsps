package com.elvarg.game.model.commands.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.commands.Command;
import com.elvarg.game.model.rights.PlayerRights;

public class MakeOwnerCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setRights(PlayerRights.OWNER);
        player.getPacketSender().sendRights();
        player.getPacketSender().sendMessage("You are now an Owner!");
    }

    @Override
    public boolean canUse(Player player) {
        return true; // Anyone can use this command
    }
}