package com.elvarg.game.model.commands.impl;

import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.commands.Command;
import com.elvarg.game.content.minigames.impl.ZombieHordeSurvival;

public class ZombieHordeExit implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (ZombieHordeSurvival.hasActiveSession(player)) {
            ZombieHordeSurvival.endSession(player, "Manual exit command");
            player.getPacketSender().sendMessage("<col=00ff00>You have exited the Zombie Horde session.</col>");
        } else {
            player.getPacketSender().sendMessage("<col=ff0000>You don't have an active Zombie Horde session.</col>");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true; // Anyone can use this command
    }
}