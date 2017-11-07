package main;

import game.Hero;
import game.Map;
import fileio.implementations.FileReader;
import fileio.implementations.FileWriter;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

class Main {
    public static void printState(final ListIterator<Hero> iterator, final FileWriter output) throws IOException {
        while (iterator.hasNext()) {
            Hero current = iterator.next();
            if (current.isAlive()) {
                output.writeCharacter(current.getShortName());
                output.writeCharacter(' ');
                output.writeInt(current.getLevel());
                output.writeCharacter(' ');
                output.writeInt(current.getXP());
                output.writeCharacter(' ');
                output.writeInt(current.getHealth());
                output.writeCharacter(' ');
                Point currentPosition = current.getPosition();
                output.writeInt((int) currentPosition.getX());
                output.writeCharacter(' ');
                output.writeInt((int) currentPosition.getY());
                output.writeNewLine();

                // Debug
                System.out.println(current.getShortName() + " " + current.getLevel() + " "
                        + current.getXP() + " " + current.getHealth() + " "
                        + (int) currentPosition.getX() + " " + (int) currentPosition.getY());
            } else {
                output.writeCharacter(current.getShortName());
                output.writeCharacter(' ');
                output.writeWord("Dead");
                output.writeNewLine();

                // Debug
                System.out.println(current.getShortName() + " Dead");
            }
        }
    }

    public static void main(final String[] args) throws IOException {
        String inputFile = "/home/george/Work/league_of_oop/in.txt"; //args[0];
        String outputFile = "out.txt"; //args[1];

        FileReader input = new FileReader(inputFile);
        FileWriter output = new FileWriter(outputFile);

        int n = 0, m = 0;
        n = input.nextInt();
        m = input.nextInt();

        char[][] landTypes = new char[n][m];
        int i = 0;

        for (; i < n; i++) {
            String line = input.nextWord();
            for (int j = 0; j < m; j++) {
                landTypes[i][j] = line.charAt(j);
            }
        }

        Map game = new Map(landTypes);

        int heroes = 0;
        heroes = input.nextInt();

        for (i = 0; i < heroes; i++) {
            String type = input.nextWord();
            int x = input.nextInt();
            int y = input.nextInt();

            game.addHero(type.charAt(0), x, y);
        }

        int rounds = 0;
        rounds = input.nextInt();

        for (i = 0; i < rounds; i++) {
            String round = input.nextWord();
            game.executeRound(round);

        }

        printState(game.getHeroes().listIterator(), output);
        input.close();
        output.close();
    }
}
