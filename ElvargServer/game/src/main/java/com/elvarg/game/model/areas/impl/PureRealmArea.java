package com.elvarg.game.model.areas.impl;

import com.elvarg.game.content.combat.CombatFactory.CanAttackResponse;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.magic.CombatSpells;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Boundary;
import com.elvarg.game.model.MagicSpellbook;
import com.elvarg.game.model.areas.Area;

import java.util.Arrays;

public class PureRealmArea extends Area {

    public PureRealmArea() {
        // Define boundaries for Pure's Realm - using an example coordinate range
        super(Arrays.asList(new Boundary(2400, 2500, 3100, 3200)));
    }

    @Override
    public String getName() {
        return "Pure's Realm";
    }

    @Override
    public void postEnter(Mobile character) {
        if (character.isPlayer()) {
            Player player = character.getAsPlayer();
            player.getPacketSender().sendMessage("Welcome to Pure's Realm!");
            player.getPacketSender().sendInteractionOption("Attack", 2, true);
            
            // Set spellbook to normal when entering Pure's Realm
            if (player.getSpellbook() != MagicSpellbook.NORMAL) {
                player.setSpellbook(MagicSpellbook.NORMAL);
                player.getPacketSender().sendTabInterface(6, 1151);
                player.getPacketSender().sendMessage("Your spellbook has been switched to normal.");
            }
        }
    }

    @Override
    public void postLeave(Mobile character, boolean logout) {
        if (character.isPlayer()) {
            Player player = character.getAsPlayer();
            player.getPacketSender().sendInteractionOption("null", 2, true);
            player.getPacketSender().sendMessage("You have left Pure's Realm.");
        }
    }

    @Override
    public CanAttackResponse canAttack(Mobile attacker, Mobile target) {
        // Allow PvP in Pure's Realm
        return CanAttackResponse.CAN_ATTACK;
    }

    @Override
    public boolean isMulti(Mobile character) {
        // Make Pure's Realm multi-combat
        return true;
    }

    @Override
    public void onPlayerDealtDamage(Player player, Mobile target, PendingHit hit) {
        // Check if it's a god spell and boost damage to 40s
        if (hit.getCombatMethod() != null && hit.getCombatMethod().type() == com.elvarg.game.content.combat.CombatType.MAGIC) {
            if (player.getCombat().getSelectedSpell() != null) {
                // Check if it's one of the god spells
                if (player.getCombat().getSelectedSpell() == CombatSpells.SARADOMIN_STRIKE.getSpell() ||
                    player.getCombat().getSelectedSpell() == CombatSpells.CLAWS_OF_GUTHIX.getSpell() ||
                    player.getCombat().getSelectedSpell() == CombatSpells.FLAMES_OF_ZAMORAK.getSpell()) {
                    
                    // Set damage to 40 for god spells in Pure's Realm
                    if (hit.isAccurate()) {
                        hit.getHits()[0].setDamage(40);
                    }
                }
            }
        }
    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean canTeleport(Player player) {
        return true;
    }
}