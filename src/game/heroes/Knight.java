package game.heroes;

import game.Hero;
import game.Ability;

public final class Knight extends Hero {
    public static final int BASE_HEALTH = 900;
    public static final int HEALTH_PER_LEVEL = 80;
    public static final int STUN_DURATION = 1;
    public static final char GREAT_LAND = 'L';
    public static final char SHORT_NAME = 'K';

    public Knight() {
        this.baseHealth = this.health = BASE_HEALTH;
        this.healthPerLevel = HEALTH_PER_LEVEL;
        this.greatLand = GREAT_LAND;
        this.heroShortName = SHORT_NAME;
    }

    @Override
    public int getAttackDamage(final Hero victim) {
        return Ability.Execute.getDamage(this, victim)
                + Ability.Slam.getDamage(this, victim);
    }

    @Override
    public int getSimulatedDamage(final Hero victim) {
        return Ability.Execute.getUntargettedDamage(this, victim)
                + Ability.Slam.getUntargettedDamage(this, victim);
    }

    @Override
    public void attack(final Hero victim) {
        int damage = getAttackDamage(victim);
        if (DEBUG) {
            System.out.println("Knight:" + this + " attacked " + victim + " with: " + damage);
        }

        victim.setHealth(victim.getHealth() - damage, this);
        victim.setAbilityOverTime(Ability.Stun, STUN_DURATION, this);
    }
}
