package edu.wctc;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static int gameCount = 1;

    public static void main(String[] args) {

        DiceGame game = new DiceGame(2,5,3);
        Scanner scanner = new Scanner(System.in);
        String input;



        System.out.print("Enter number of players> ");
        int numberOfPlayers = scanner.nextInt();

        do {

            round(game);

            game.nextPlayer();

            input = scanner.next();

        } while (input.equals("Y"));
    }

    static void round(DiceGame game) {

        // sets up the first player
        game.nextPlayer();

        // prints the game number
        System.out.printf("Game #%d%n", gameCount);

        // will print the current player number and dice roll results if the
        // player more rolls left
        roll(game);
    }

    static void roll(DiceGame game) {
        if (game.currentPlayerCanRoll()) {
            // print the player number
            System.out.printf("Player %d: %n", game.getCurrentPlayerNumber());

            // print dice results
            System.out.println(game.getDiceResults());

            // check if we can hold a ship, captain, and crew
            boolean ship = game.autoHold(6);
            boolean captain = game.autoHold(5);
            boolean crew = game.autoHold(4);

            if (ship && captain && crew) {
                System.out.println("6, 5, and 4 found");
                System.out.println("H to hold, or any key to roll");
                System.out.println("Hold value ");
            }

            // press enter to continue prompt
            pressEnterToContinue();

            // roll the dice so it's ready for next time
            game.rollDice();
        }
    }

    static void pressEnterToContinue(){
        try {
            System.out.print("Press enter to continue...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
