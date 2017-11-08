package game;

import game.heroes.Knight;
import game.heroes.Pyromancer;
import game.heroes.Rogue;
import game.heroes.Wizard;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;

public class Map {
    private ArrayList<Hero> heroes;
    private char[][] landTypes;

    enum Heroes {
        Pyromancer,
        Knight,
        Wizard,
        Rogue;

        char toChar() {
            char type = ' ';

            switch (this) {
                case Pyromancer:
                    type = 'P';
                    break;
                case Knight:
                    type = 'K';
                    break;
                case Wizard:
                    type = 'W';
                    break;
                case Rogue:
                    type = 'R';
                    break;
                default:
            }

            return type;
        }
    };

    public Map(final char[][] landTypes) {
        this.heroes = new ArrayList<Hero>();
        this.landTypes = landTypes;
    }

    public final void addHero(final char type, final int x, final int y) {
        Hero newHero = null;

        if (type == Heroes.Pyromancer.toChar()) {
            newHero = new Pyromancer();
        } else if (type == Heroes.Knight.toChar()) {
            newHero = new Knight();
        } else if (type == Heroes.Wizard.toChar()) {
            newHero = new Wizard();
        } else if (type == Heroes.Rogue.toChar()) {
            newHero = new Rogue();
        }

        newHero.setPosition(new Point(x, y));
        heroes.add(newHero);
    }

    public final Point move(final Point current, final char move) {
        int x = (int) current.getX();
        int y = (int) current.getY();

        switch (move) {
            case 'U':
                x -= 1;
                break;
            case 'D':
                x += 1;
                break;
            case 'L':
                y -= 1;
                break;
            case 'R':
                y += 1;
            default:
        }

        current.setLocation(x, y);
        return current;
    }

    public final void executeRound(final String round) {
        ListIterator<Hero> iterator = heroes.listIterator();
        while (iterator.hasNext()) {
            iterator.next().overTimeAbility();
        }

        String[] moves = round.split("");
        int length = heroes.size();

        while (iterator.hasPrevious()) {
            int idx = iterator.previousIndex();
            Hero current = iterator.previous();
            Point currentPosition = current.getPosition();
            int x = (int) currentPosition.getX(),
                    y = (int) currentPosition.getY();

            if (current.isAlive() && current.canMove()) {
                currentPosition = move(currentPosition, moves[idx].charAt(0));
                current.setPosition(currentPosition, landTypes[x][y]);
            }
        }

        while (iterator.hasNext()) {
            int idx = iterator.nextIndex();
            Hero current = iterator.next();
            Point currentPosition = current.getPosition();

            if (!current.isAlive()) {
                continue;
            }

            ListIterator<Hero> nextIterator = heroes.listIterator(idx + 1);
            while (nextIterator.hasNext()) {
                Hero next = nextIterator.next();
                Point nextPosition = next.getPosition();

                if (!next.isAlive()) {
                    continue;
                }

                if (currentPosition.equals(nextPosition)) {
                    current.attack(next);
                    current.accept(next);
                }
            }
        }

        while (iterator.hasPrevious()) {
            iterator.previous().postRoundHandler();
        }
    }

    public final ArrayList<Hero> getHeroes() {
        return heroes;
    }

}
