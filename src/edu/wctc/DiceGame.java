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

    public boolean autoHold(int faceValue) {

        // stream of dice with faceValue
        Stream<Die> faceValueDice =
                dice.stream()
                        .filter(die -> die.getFaceValue() == faceValue);

        // if the stream as any items held/unheld return true
        if (faceValueDice.findAny().isPresent()) {

            // if there are any die that are not being held, hold them.
            if (faceValueDice.anyMatch(die -> !die.isBeingHeld())) {
                faceValueDice
                        .filter(die -> !die.isBeingHeld())
                        .findFirst().
                        ifPresent(Die::holdDie);
            }

            return true;
        } else
            // if the stream found no items
            return false;

    }

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

        // addLoss to the rest of the players and skip the first item
        // (the highest score)
        sortedPlayers.skip(0).forEach(Player::addLoss);

        // returns a string composed by concatenating each Players's toString
        return players.stream()
                .map(Player::toString)
                .collect(Collectors.joining("\n"));
    }

    public boolean isHoldingDie(int faceValue) {
        // get all die that are held
        Optional<Die> heldDie =
                dice.stream()
                        .filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld())
                        .findFirst();
        return heldDie.isPresent();
    }

    public boolean nextPlayer() {
        // find the total number of players and the index of the current player
        int playersLeft = players.size() - (players.indexOf(currentPlayer) + 1);

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

    public void scoreCurrentPlayer() {

        Optional<Die> shipObject =
                dice.stream().filter(die -> die.getFaceValue() == 6 && die.isBeingHeld()).findFirst();

        Optional<Die> captainObject =
                dice.stream().filter(die -> die.getFaceValue() == 6 && die.isBeingHeld()).findFirst();

        Optional<Die> crewObject =
                dice.stream().filter(die -> die.getFaceValue() == 6 && die.isBeingHeld()).findFirst();

        boolean ship =
                dice.stream().anyMatch(die -> die.getFaceValue() == 6 && die.isBeingHeld());

        boolean captain =
                dice.stream().anyMatch(die -> die.getFaceValue() == 5 && die.isBeingHeld());

        boolean crew =
                dice.stream().anyMatch(die -> die.getFaceValue() == 4 && die.isBeingHeld());

        int score = 0;

        if (ship && captain && crew) {
            // try to remove the ship, captain, and crew
            List<Die> listOfDice = dice.stream().toList();
            listOfDice.remove(shipObject.orElse(new Die(6)));
            listOfDice.remove(captainObject.orElse(new Die(6)));
            listOfDice.remove(crewObject.orElse(new Die(6)));

            // calculate the score for the remaining cargo
            for (Die die : listOfDice) {
                score += die.getFaceValue();
            }
        }

        currentPlayer.setScore(currentPlayer.getScore() + score);
    }

    public void startNewGame() {
        // first player in list will be current player
        currentPlayer = players.get(0);

        players.stream().forEach(Player::resetPlayer);
    }

}
