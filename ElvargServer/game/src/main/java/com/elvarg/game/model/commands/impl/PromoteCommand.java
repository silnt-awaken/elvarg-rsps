package com.elvarg.game.model.commands.impl;

import com.elvarg.game.World;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.commands.Command;
import com.elvarg.game.model.rights.PlayerRights;

import java.util.Optional;

public class PromoteCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 3) {
            player.getPacketSender().sendMessage("Usage: ::promote <player> <right>");
            player.getPacketSender().sendMessage("Rights: none, moderator, administrator, owner, developer");
            return;
        }

        String targetName = parts[1];
        String rightName = parts[2].toUpperCase();

        Optional<Player> target = World.getPlayerByName(targetName);
        if (!target.isPresent()) {
            player.getPacketSender().sendMessage("Player " + targetName + " is not online.");
            return;
        }

        PlayerRights newRights;
        try {
            newRights = PlayerRights.valueOf(rightName);
        } catch (IllegalArgumentException e) {
            player.getPacketSender().sendMessage("Invalid right: " + rightName);
            player.getPacketSender().sendMessage("Available rights: NONE, MODERATOR, ADMINISTRATOR, OWNER, DEVELOPER");
            return;
        }

        Player targetPlayer = target.get();
        targetPlayer.setRights(newRights);
        targetPlayer.getPacketSender().sendRights();
        
        player.getPacketSender().sendMessage("Promoted " + targetName + " to " + newRights.name());
        targetPlayer.getPacketSender().sendMessage("You have been promoted to " + newRights.name() + " by " + player.getUsername());
    }

    @Override
    public boolean canUse(Player player) {
        return player.getRights() == PlayerRights.OWNER;
    }
}