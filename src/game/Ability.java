package game;

import game.heroes.Knight;
import game.heroes.Pyromancer;
import game.heroes.Rogue;
import game.heroes.Wizard;

public enum Ability {
    Fireblast(AbilityType.FLAT, 350, 50),
    Ignite_Active(AbilityType.FLAT, 150, 20),
    Ignite_DoT(AbilityType.FLAT, 50, 30),
    Execute(AbilityType.FLAT, 200, 30),
    Execute_Limit(AbilityType.PERCENT, 20, 1, 40),
    Slam(AbilityType.FLAT, 100, 40),
    Stun(AbilityType.FLAT, 0, 0),
    Drain(AbilityType.FLAT, 20, 5),
    Deflect(AbilityType.PERCENT, 35, 2, 70),
    Backstab(AbilityType.FLAT, 200, 20),
    Paralysis(AbilityType.FLAT, 40, 10);

    enum AbilityType {
        FLAT, PERCENT
    };

    enum Modifier {
        Fireblast(-20, 20, -10, 5),
        Ignite(-20, 20, -10, 5),
        Execute(15, 0, 10, -20),
        Slam(-20, 20, -10, 5),
        Drain(-20, 20, -10, 5),
        Deflect(20, 40, 30, 0),
        Backstab(20, -10, 25, 25),
        Paralysis(-10, -20, 20, 25),
        Land(15, 15, 25, 10);

        private final int vsRogue;
        private final int vsKnight;
        private final int vsPyromancer;
        private final int vsWizard;

        public static final int DEFAULT_DAMAGE_PERCENT = 100;

        Modifier(final int vsRogue, final int vsKnight,
                     final int vsPyromancer, final int vsWizard) {
            this.vsRogue = vsRogue;
            this.vsKnight = vsKnight;
            this.vsPyromancer = vsPyromancer;
            this.vsWizard = vsWizard;
        }

        float getModifier(final Hero victim) {
            float damagePercent = DEFAULT_DAMAGE_PERCENT;
            if (victim instanceof Rogue) {
                damagePercent += vsRogue;
            } else if (victim instanceof Knight) {
                damagePercent += vsKnight;
            } else if (victim instanceof Pyromancer) {
                damagePercent += vsPyromancer;
            } else if (victim instanceof Wizard) {
                damagePercent += vsWizard;
            }

            return (damagePercent / DEFAULT_DAMAGE_PERCENT);
        }
    }

    private final AbilityType type;
    private final int base;
    private final int perLevel;
    private int max = -1;

    public static final float DRAIN_CONSTANT = 0.3f;
    public static final int PERCENT = 100;

    Ability(final AbilityType type, final int base, final int perLevel) {
        this.type = type;
        this.base = base;
        this.perLevel = perLevel;
    }
    Ability(final AbilityType type, final int base, final int perLevel, final int max) {
        this(type, base, perLevel);
        this.max = max;
    }

    public int getBaseDamage(final Hero attacker) {
        int damage = base + perLevel * attacker.getLevel();
        if (max != -1) {
            if (damage > max) {
                damage = max;
            }
        }

        return damage;
    }

    public int getUntargettedDamage(final Hero attacker, final Hero victim) {
        float damage = getBaseDamage(attacker);
        float landModifier = 1f;

        if (attacker.hasLandAdvantage()) {
            landModifier = Modifier.Land.getModifier(attacker);
        }

        switch (this) {
            case Fireblast:
            case Ignite_Active:
            case Ignite_DoT:
            case Slam:
            case Paralysis:
                damage = Math.round(damage * landModifier);
                break;
            case Execute:
                int limit = Ability.Execute_Limit.getDamage(attacker, victim);
                if (limit <= victim.getHealth()) {
                    damage = victim.getHealth();
                }
                damage = Math.round(damage * landModifier);
                break;
            case Backstab:
                damage = Math.round(damage * ((Rogue) attacker).criticalHit()
                        * landModifier);
            default:
        }

        return (int) damage;
    }

    public int getDamage(final Hero attacker, final Hero victim) {
        float damage = getBaseDamage(attacker);
        float landModifier = 1f;

        if (attacker.hasLandAdvantage()) {
            landModifier = Modifier.Land.getModifier(attacker);
        }
        switch (this) {
            case Fireblast:
                damage = Math.round(damage * landModifier * Modifier.Fireblast.getModifier(victim));
                break;
            case Ignite_Active: case Ignite_DoT:
                damage = Math.round(damage * landModifier * Modifier.Ignite.getModifier(victim));
                break;
            case Execute_Limit:
                damage = victim.getMaxHealth() * (damage / PERCENT);
                break;
            case Execute:
                int limit = Ability.Execute_Limit.getDamage(attacker, victim);
                if (limit <= victim.getHealth()) {
                    damage = victim.getHealth();
                }
                damage = Math.round(damage * landModifier * Modifier.Execute.getModifier(victim));
                break;
            case Slam:
                damage = Math.round(damage * landModifier * Modifier.Slam.getModifier(victim));
                break;
            case Drain:
                int source = Math.min((int) (DRAIN_CONSTANT * victim.getMaxHealth()),
                        victim.getHealth());
                damage = source * (damage / PERCENT);
                damage = Math.round(damage * landModifier * Modifier.Drain.getModifier(victim));
                break;
            case Deflect:
                if (victim instanceof Wizard) {
                    damage = 0;
                } else {
                    damage = Math.round(victim.getSimulatedDamage(attacker)
                            * (damage / PERCENT)
                            * landModifier
                            * Modifier.Deflect.getModifier(victim));
                }
                break;
            case Backstab:
                damage = Math.round(damage * ((Rogue) attacker).criticalHit()
                        * landModifier * Modifier.Backstab.getModifier(victim));
                break;
            case Paralysis:
                damage = Math.round(damage * landModifier * Modifier.Paralysis.getModifier(victim));
                break;
            default:
        }

        return (int) damage;
    }
}
