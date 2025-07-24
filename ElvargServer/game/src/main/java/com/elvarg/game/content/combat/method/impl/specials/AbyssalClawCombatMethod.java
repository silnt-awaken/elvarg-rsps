package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.PrayerHandler;
import com.elvarg.game.content.combat.CombatConstants;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.formula.DamageFormulas;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.impl.MeleeCombatMethod;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Graphic;
import com.elvarg.game.model.Priority;
import com.elvarg.util.Misc;

public class AbyssalClawCombatMethod extends MeleeCombatMethod {

    private static final Animation ANIMATION = new Animation(7527, Priority.HIGH);
    private static final Graphic GRAPHIC = new Graphic(1171, Priority.HIGH);

    @Override
    public PendingHit[] hits(Mobile character, Mobile target) {
        PendingHit hit = new PendingHit(character, target, this, true, 6, 0);
        // Abyssal Claws have a unique 6-hit pattern with cascading damage

        // Damage rolls occur from a range based around the max hit
        int maxHit = DamageFormulas.calculateMaxMeleeHit(character);
        if (target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MELEE]) {
            final double damageMultiplier = target.isNpc() ? CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_NPCS :
                                            CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_PLAYERS;
            maxHit *= damageMultiplier;
        }

        final int first, second, third, fourth, fifth, sixth;
        
        // First roll hit
        if (hit.getHits()[0].getDamage() > 0) {
            first = Misc.randomInclusive((int) Math.round(maxHit * 0.6), maxHit);
            second = (int) Math.round(first * 0.6);
            third = (int) Math.round(second * 0.6);
            fourth = (int) Math.round(third * 0.7);
            fifth = (int) Math.round(fourth * 0.7);
            sixth = (int) Math.round(fifth * 0.8) + Misc.random(2);
        }
        // Second roll hit
        else if (hit.getHits()[1].getDamage() > 0) {
            first = 0;
            second = Misc.randomInclusive((int) Math.round(maxHit * 0.4), (int) Math.round(maxHit * 0.9));
            third = (int) Math.round(second * 0.6);
            fourth = (int) Math.round(third * 0.7);
            fifth = (int) Math.round(fourth * 0.7);
            sixth = (int) Math.round(fifth * 0.8) + Misc.random(2);
        }
        // Third roll hit
        else if (hit.getHits()[2].getDamage() > 0) {
            first = 0;
            second = 0;
            third = Misc.randomInclusive((int) Math.round(maxHit * 0.3), (int) Math.round(maxHit * 0.8));
            fourth = (int) Math.round(third * 0.7);
            fifth = (int) Math.round(fourth * 0.7);
            sixth = (int) Math.round(fifth * 0.8) + Misc.random(2);
        }
        // Fourth roll hit
        else if (hit.getHits()[3].getDamage() > 0) {
            first = 0;
            second = 0;
            third = 0;
            fourth = Misc.randomInclusive((int) Math.round(maxHit * 0.25), (int) Math.round(maxHit * 0.75));
            fifth = (int) Math.round(fourth * 0.7);
            sixth = (int) Math.round(fifth * 0.8) + Misc.random(2);
        }
        // Fifth roll hit
        else if (hit.getHits()[4].getDamage() > 0) {
            first = 0;
            second = 0;
            third = 0;
            fourth = 0;
            fifth = Misc.randomInclusive((int) Math.round(maxHit * 0.2), (int) Math.round(maxHit * 0.7));
            sixth = (int) Math.round(fifth * 0.8) + Misc.random(2);
        }
        // Sixth roll hit
        else if (hit.getHits()[5].getDamage() > 0) {
            first = 0;
            second = 0;
            third = 0;
            fourth = 0;
            fifth = 0;
            sixth = Misc.randomInclusive((int) Math.round(maxHit * 0.2), (int) Math.round(maxHit * 1.3));
        }
        // No roll hit
        else {
            first = 0;
            second = 0;
            third = 0;
            fourth = Misc.random(1); // 50% chance to hit 1
            fifth = Misc.random(1); // 50% chance to hit 1
            sixth = Misc.random(2); // 50% chance to hit 1, 50% chance to hit 0
        }

        hit.getHits()[0].setDamage(first);
        hit.getHits()[1].setDamage(second);
        hit.getHits()[2].setDamage(third);
        hit.getHits()[3].setDamage(fourth);
        hit.getHits()[4].setDamage(fifth);
        hit.getHits()[5].setDamage(sixth);
        hit.updateTotalDamage();
        return new PendingHit[] { hit };
    }

    @Override
    public void start(Mobile character, Mobile target) {
        CombatSpecial.drain(character, CombatSpecial.ABYSSAL_CLAWS.getDrainAmount());
        character.performAnimation(ANIMATION);
        character.performGraphic(GRAPHIC);
    }
}