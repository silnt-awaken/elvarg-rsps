package com.elvarg.game.content.combat.method.impl.specials;

import com.elvarg.game.content.combat.CombatFactory;
import com.elvarg.game.content.combat.CombatSpecial;
import com.elvarg.game.content.combat.formula.DamageFormulas;
import com.elvarg.game.content.combat.hit.PendingHit;
import com.elvarg.game.content.combat.method.impl.RangedCombatMethod;
import com.elvarg.game.content.combat.ranged.RangedData;
import com.elvarg.game.content.combat.ranged.RangedData.RangedWeapon;
import com.elvarg.game.entity.impl.Mobile;
import com.elvarg.game.entity.impl.player.Player;
import com.elvarg.game.model.Animation;
import com.elvarg.game.model.Priority;
import com.elvarg.game.model.Projectile;
import com.elvarg.util.Misc;

public class AbyssalCrossbowCombatMethod extends RangedCombatMethod {

    private static final Animation ANIMATION = new Animation(4230, Priority.HIGH);
    private static final Projectile PROJECTILE = new Projectile(301, 44, 35, 50, 70);

    @Override
    public PendingHit[] hits(Mobile character, Mobile target) {
        final int distance = character.getLocation().getDistance(target.getLocation());
        
        // Create two hits with slight delay between them
        PendingHit firstHit = new PendingHit(character, target, this, 
                                           RangedData.hitDelay(distance, RangedData.RangedWeaponType.CROSSBOW));
        PendingHit secondHit = new PendingHit(character, target, this, 
                                            RangedData.hitDelay(distance, RangedData.RangedWeaponType.CROSSBOW) + 1);
        
        // Apply critical hit chance (25% chance for each hit to crit)
        enhanceHitWithCriticalChance(firstHit);
        enhanceHitWithCriticalChance(secondHit);
        
        return new PendingHit[] { firstHit, secondHit };
    }
    
    private void enhanceHitWithCriticalChance(PendingHit hit) {
        // 25% chance for critical hit (1.5x damage)
        if (Misc.random(4) == 0) {
            // Get the original damage calculation
            int originalDamage = hit.getTotalDamage();
            if (originalDamage > 0) {
                // Apply 1.5x critical multiplier
                int criticalDamage = (int) Math.round(originalDamage * 1.5);
                hit.getHits()[0].setDamage(criticalDamage);
                hit.updateTotalDamage();
                
                // Add visual effect for critical hit (optional - using same graphic as claws for now)
                // This could be enhanced with a custom critical hit graphic later
            }
        }
    }

    @Override
    public boolean canAttack(Mobile character, Mobile target) {
        if (!character.isPlayer()) {
            return false;
        }
        Player player = character.getAsPlayer();
        
        // Check if player has abyssal crossbow equipped
        if (player.getEquipment().get(3) == null || 
            player.getEquipment().get(3).getId() != 11167) {
            return false;
        }
        
        // Check ammo (need 2 bolts for double shot)
        if (!CombatFactory.checkAmmo(player, 2)) {
            return false;
        }
        
        return true;
    }

    @Override
    public void start(Mobile character, Mobile target) {
        final Player player = character.getAsPlayer();
        
        // Drain special attack energy
        CombatSpecial.drain(player, CombatSpecial.ABYSSAL_CROSSBOW.getDrainAmount());
        
        // Perform animation
        player.performAnimation(ANIMATION);
        
        // Send two projectiles with slight delay
        Projectile.sendProjectile(character, target, PROJECTILE);
        // Second projectile slightly delayed
        Projectile.sendProjectile(character, target, new Projectile(301, 44, 35, 52, 72));
        
        // Consume 2 bolts for the double shot
        CombatFactory.decrementAmmo(player, target.getLocation(), 2);
    }
}