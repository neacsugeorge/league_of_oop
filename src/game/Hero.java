package game;

import java.awt.Point;

public abstract class Hero {
    protected int health;
    protected int healthPerLevel;
    protected int baseHealth;

    private int xp = 0;
    private int level = 0;
    private static final int MAX_LEVEL_DIFFERENCE = 200;
    private static final int MAX_PER_LEVEL_DIFFERENCE = 40;
    private static final int BASE_LEVEL_REQUIRED_XP = 250;
    private static final int PER_LEVEL_REQUIRED_XP = 50;

    public static final boolean DEBUG = false;

    private Ability abilityOverTime;
    private Hero overTimeAttacker;
    private int abilityOverTimeDamage = 0;
    private int overTimeDuration = 0;
    private boolean newOverTimeAbility = false;

    private Point position;
    protected char landType;
    protected char greatLand;
    private boolean foughtThisRound = false;

    protected char heroShortName;

    public final Point getPosition() {
        return position;
    }

    public final char getLandType() {
        return landType;
    }

    public final int getLevel() {
        return level;
    }

    public final int getXP() {
        return xp;
    }

    public final int getHealth() {
        return health;
    }

    public final int getMaxHealth() {
        return baseHealth + healthPerLevel * level;
    }

    public final char getShortName() {
        return heroShortName;
    }

    public final boolean isAlive() {
        return health > 0;
    }

    public final void setPosition(final Point newPosition) {
        position = newPosition;
    }

    public final void setPosition(final Point newPosition, final char newLandType) {
        position = newPosition;
        landType = newLandType;
    }

    public final void setAbilityOverTime(final Ability ability,
                                   final int duration,
                                   final Hero attacker) {
        this.abilityOverTime = ability;
        this.abilityOverTimeDamage = ability.getDamage(attacker, this);
        this.overTimeDuration = duration;
        this.overTimeAttacker = attacker;
        this.newOverTimeAbility = true;
    }

    public final void setHealth(final int newHealth, final Hero attacker) {
        if (newHealth <= 0) {
            attacker.killed(this);
        }

        health = newHealth;
    }

    public final void killed(final Hero victim) {
        int earnedXP = Math.max(0,
                MAX_LEVEL_DIFFERENCE - (level - victim.getLevel()) * MAX_PER_LEVEL_DIFFERENCE);

        xp += earnedXP;
    }

    public final void levelUp() {
        level++;
        health = baseHealth + healthPerLevel * level;
    }

    public final void overTimeAbility() {
        if (overTimeDuration <= 0) {
            return;
        }

        health -= abilityOverTimeDamage;
    }

    public final boolean hasLandAdvantage() {
        return greatLand == landType;
    }

    public final boolean canMove() {
        if (overTimeDuration > 0) {
            if (abilityOverTime == Ability.Stun
                    || abilityOverTime == Ability.Paralysis) {
                return false;
            }
        }

        return true;
    }

    public final boolean canAttack() {
        if (!foughtThisRound) {
            return true;
        }

        return false;
    }

    public final void finishRound() {
        int neededXP = BASE_LEVEL_REQUIRED_XP + level * PER_LEVEL_REQUIRED_XP;
        while (xp >= neededXP) {
            this.levelUp();
            neededXP = BASE_LEVEL_REQUIRED_XP + level * PER_LEVEL_REQUIRED_XP;
        }

        if (newOverTimeAbility) {
            newOverTimeAbility = false;
        } else {
            overTimeDuration--;
        }

        foughtThisRound = false;
        postRoundHandler();
    }

    public void postRoundHandler() {
        // Nothing
    }

    public void postAttackHandler() {
        foughtThisRound = true;
    }

    public abstract int getAttackDamage(Hero victim);
    public abstract int getSimulatedDamage(Hero victim);
    public abstract void attack(Hero victim);

    public final void accept(final Hero attacker) {
        attacker.attack(this);
    }
}
