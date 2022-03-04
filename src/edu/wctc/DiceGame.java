package edu.wctc;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DiceGame {

    private final List<Player> players;
    private final List<Die> dice;
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) {

        players = Collections.nCopies(countPlayers, new Player());

        dice = new ArrayList<>();
        for (int i = 0; i < countDice; i++) dice.add(new Die(6));

        this.maxRolls = maxRolls;

        if (players.size() < 2)
            throw new IllegalArgumentException("A minimum of two players are " +
                    "required to play this game.");
    }

    private boolean allDiceHeld() {
        return dice.stream().allMatch(Die::isBeingHeld);
    }

    // todo
//    public boolean autoHold(int faceValue) {
//        // find dice with the given face value
//        Optional<Die> dieHeld =
//                dice.stream().filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld()).findFirst();
//
//        Optional<Die> dieNotHeld =
//                dice.stream().filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld()).findFirst();
//    }

    public boolean currentPlayerCanRoll() {
        return currentPlayer.getRollsUsed() < maxRolls || !allDiceHeld();
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
        return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return dice.stream()
                .map(Die::toString)
                .collect(Collectors.joining("\n"));
    }

    public String getFinalWinner() {
        return players.stream().max(Comparator.comparingInt(Player::getWins)).toString();
    }

    public String getGameResults() {
        // sorts players from highest score to lowest score
        Stream<Player> sortedPlayers =
                players.stream()
                        .sorted(Comparator.comparingInt(Player::getScore).reversed());

        // find the winner object
        Player winner =
                sortedPlayers
                        .max(Comparator.comparingInt(Player::getScore))
                        .orElse(new Player());

        // search the players list for the winner object and addWin()
        players.get(players.indexOf(winner)).addWin();

        // addLoss to the rest of the players
        sortedPlayers.forEach(Player::addLoss);

        // returns a string composed by concatenating each Players's toString
        return players.stream()
                .map(Player::toString)
                .collect(Collectors.joining("\n"));
    }

    public boolean isHoldingDie(int faceValue) {
        // get all die that are held
        List<Die> heldDice =
                dice.stream()
                        .filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld())
                        .toList();
        return heldDice.size() > 0;
    }

    public boolean nextPlayer() {
        // find the total number of players and the index of the current player
        int playersLeft = players.size() - players.indexOf(currentPlayer);

        if (playersLeft > 0) {
            currentPlayer = players.get(players.indexOf(currentPlayer) + 1);
            return true;
        } else {
            return false;
        }
    }

    public void playerHold(char dieNum) {
        dice.stream()
                .filter(die -> die.getDieNum() == dieNum)
                .findFirst()
                .ifPresent(Die::holdDie);
    }

    public void resetDice() {
        dice.stream()
                .forEach(Die::resetDie);
    }

    public void resetPlayers() {
        players.stream()
                .forEach(Player::resetPlayer);
    }

    public void rollDice() {
        currentPlayer.roll();
        dice.stream()
                .forEach(Die::rollDie);
    }

//    public void scoreCurrentPlayer() {
//
//        int score = 0;
//
//        // list to keep all dice face numbers
//        List<Integer> faces = new ArrayList<>();
//
//        // copy the face numbers to the list
//        for (Die die : dice) faces.add(die.getFaceValue());
//
//        // stores each unique element as the key and the frequency as the value
//        // https://www.techiedelight.com/count-frequency-elements-list-java/
//        Map<Integer, Long> frequencyMap =
//                faces.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
//        // iterate through the hashmap to find if the player has a 6,5,4
//        if (frequencyMap.containsKey(6) && frequencyMap.containsKey(5) & frequencyMap.containsKey(4)) {
//            long numOfSix = frequencyMap.get(6);
//            long numOfFive = frequencyMap.get(5);
//            long numOfFour = frequencyMap.get(4);
//
//            if (numOfSix && numOfFive && numOfFour == 1)
//        }
//
//    }

    public void startNewGame() {
        // first player in list will be current player
        currentPlayer = players.get(0);

        players.stream().forEach(Player::resetPlayer);
    }

}
