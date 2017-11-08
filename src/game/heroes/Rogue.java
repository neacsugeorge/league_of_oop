package game.heroes;

import game.Ability;
import game.Hero;

public final class Rogue extends Hero {
    public static final int BASE_HEALTH = 600;
    public static final int HEALTH_PER_LEVEL = 40;
    public static final int PARALYSIS_DURATION_NORMAL = 3;
    public static final int PARALYSIS_DURATION_EXTENDED = 6;
    public static final int CRITICAL_HIT = 3;
    public static final float CRITICAL_MODIFIER = 1.5f;
    public static final char GREAT_LAND = 'W';
    public static final char SHORT_NAME = 'R';

    private int stabs = 0;

    public Rogue() {
        this.baseHealth = this.health = BASE_HEALTH;
        this.healthPerLevel = HEALTH_PER_LEVEL;
        this.greatLand = GREAT_LAND;
        this.heroShortName = SHORT_NAME;
    }

    public float criticalHit() {
        if (this.hasLandAdvantage() && (stabs % CRITICAL_HIT == 0)) {
            return CRITICAL_MODIFIER;
        }

        return 1f;
    }

    @Override
    public int getAttackDamage(final Hero victim) {
        return Ability.Backstab.getDamage(this, victim)
                + Ability.Paralysis.getDamage(this, victim);
    }

    @Override
    public int getSimulatedDamage(final Hero victim) {
        return Ability.Backstab.getUntargettedDamage(this, victim)
                + Ability.Paralysis.getUntargettedDamage(this, victim);
    }

    @Override
    public void attack(final Hero victim) {
        int damage = getAttackDamage(victim);
        if (DEBUG) {
            System.out.println("Rogue:" + this + " attacked " + victim + " with: " + damage);
        }

        int overTimeDuration = PARALYSIS_DURATION_NORMAL;
        if (this.hasLandAdvantage()) {
            overTimeDuration = PARALYSIS_DURATION_EXTENDED;
        }

        victim.setHealth(victim.getHealth() - damage, this);
        victim.setAbilityOverTime(Ability.Paralysis, overTimeDuration, this);

        stabs++;
    }
}
