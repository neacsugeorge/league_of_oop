package game.heroes;

import game.Ability;
import game.Hero;

public final class Pyromancer extends Hero {
    public static final int BASE_HEALTH = 500;
    public static final int HEALTH_PER_LEVEL = 50;
    public static final int IGNITE_DURATION = 2;
    public static final char GREAT_LAND = 'V';
    public static final char SHORT_NAME = 'P';

    public Pyromancer() {
        this.baseHealth = BASE_HEALTH;
        this.healthPerLevel = HEALTH_PER_LEVEL;
        this.greatLand = GREAT_LAND;
        this.heroShortName = SHORT_NAME;
    }

    @Override
    public int getAttackDamage(final Hero victim) {
        return Ability.Fireblast.getDamage(this, victim)
                + Ability.Ignite_Active.getDamage(this, victim);
    }

    @Override
    public void attack(final Hero victim) {
        int damage = getAttackDamage(victim);

        victim.setHealth(victim.getHealth() - damage, this);
        victim.setAbilityOverTime(Ability.Ignite_DoT, IGNITE_DURATION, this);
    }
}
