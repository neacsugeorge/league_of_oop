package game.heroes;

import game.Hero;
import game.Ability;

public final class Wizard extends Hero {
    public static final int BASE_HEALTH = 400;
    public static final int HEALTH_PER_LEVEL = 30;
    public static final char GREAT_LAND = 'D';
    public static final char SHORT_NAME = 'W';

    public Wizard() {
        this.baseHealth = this.health = BASE_HEALTH;
        this.healthPerLevel = HEALTH_PER_LEVEL;
        this.greatLand = GREAT_LAND;
        this.heroShortName = SHORT_NAME;
    }

    @Override
    public int getAttackDamage(final Hero victim) {
        return Ability.Drain.getDamage(this, victim)
                + Ability.Deflect.getDamage(this, victim);
    }

    @Override
    public int getSimulatedDamage(final Hero victim) {
        return Ability.Drain.getUntargettedDamage(this, victim)
                + Ability.Deflect.getUntargettedDamage(this, victim);
    }

    @Override
    public void attack(final Hero victim) {
        int damage = getAttackDamage(victim);
        if (DEBUG) {
            System.out.println("Wizard:" + this + " attacked " + victim + " with: " + damage);
        }

        victim.setHealth(victim.getHealth() - damage, this);

        this.postAttackHandler();
    }
}
