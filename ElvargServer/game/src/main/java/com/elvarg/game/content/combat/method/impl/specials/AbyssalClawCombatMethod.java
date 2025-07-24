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
        PendingHit hit = new PendingHit(character, target, this, true, 4, 0);
        // Abyssal Claws have enhanced 4-hit pattern with higher damage potential

        // Damage rolls occur from a range based around the max hit
        int maxHit = DamageFormulas.calculateMaxMeleeHit(character);
        if (target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MELEE]) {
            final double damageMultiplier = target.isNpc() ? CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_NPCS :
                                            CombatConstants.PRAYER_DAMAGE_REDUCTION_AGAINST_PLAYERS;
            maxHit *= damageMultiplier;
        }

        final int first, second, third, fourth;
        
        // First roll hit - Enhanced damage compared to dragon claws
        if (hit.getHits()[0].getDamage() > 0) {
            first = Misc.randomInclusive((int) Math.round(maxHit * 0.6), (int) Math.round(maxHit * 1.1));
            second = (int) Math.round(first * 0.65);
            third = (int) Math.round(second * 0.65);
            fourth = (int) Math.round(third * 0.75) + Misc.random(3);
        }
        // Second roll hit - Enhanced damage
        else if (hit.getHits()[1].getDamage() > 0) {
            first = 0;
            second = Misc.randomInclusive((int) Math.round(maxHit * 0.45), (int) Math.round(maxHit * 1.0));
            third = (int) Math.round(second * 0.65);
            fourth = (int) Math.round(third * 0.75) + Misc.random(3);
        }
        // Third roll hit - Enhanced damage
        else if (hit.getHits()[2].getDamage() > 0) {
            first = 0;
            second = 0;
            third = Misc.randomInclusive((int) Math.round(maxHit * 0.35), (int) Math.round(maxHit * 0.9));
            fourth = (int) Math.round(third * 0.75) + Misc.random(3);
        }
        // Fourth roll hit - Enhanced damage
        else if (hit.getHits()[3].getDamage() > 0) {
            first = 0;
            second = 0;
            third = 0;
            fourth = Misc.randomInclusive((int) Math.round(maxHit * 0.3), (int) Math.round(maxHit * 1.4));
        }
        // No roll hit - Enhanced minimum damage
        else {
            first = 0;
            second = 0;
            third = Misc.random(2); // 50% chance to hit 1
            fourth = Misc.random(2); // 50% chance to hit 1
        }

        hit.getHits()[0].setDamage(first);
        hit.getHits()[1].setDamage(second);
        hit.getHits()[2].setDamage(third);
        hit.getHits()[3].setDamage(fourth);
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