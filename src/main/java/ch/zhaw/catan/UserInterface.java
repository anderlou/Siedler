package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;
import org.beryx.textio.*;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for the communication with the player.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class UserInterface {
    private final TextIO textIO;
    private final TextTerminal<?> textTerminal;

    /**
     * This constructor initializes the textIO.
     */
    public UserInterface() {
        textIO = TextIoFactory.getTextIO();
        textTerminal = textIO.getTextTerminal();
    }

    private final int MAX_X_COORDINATE_BOARD = 14;
    private final int MAX_Y_COORDINATE_BOARD = 22;
    private final int MIN_COORDINATE_BOARD = 0;

    /**
     * This method prints out the welcome text when the game is being started.
     */
    public void printWelcomeText() {
        textTerminal.println("Welcome to our Siedler game. We wish you a lot of fun!");
    }

    /**
     * This method asks the user how many players would like to play the game.
     *
     * @return the amount of players.
     */
    public int getNumberOfPlayers() {
        int MAX_NUMBER_OF_PLAYERS = Faction.values().length;
        return textIO.newIntInputReader().withMinVal(Config.MIN_NUMBER_OF_PLAYERS).withMaxVal(MAX_NUMBER_OF_PLAYERS).read("How many players would like to play the game?");
    }

    /**
     * This method announces which players turn it currently is.
     *
     * @param faction The player's faction, by which the players are defined.
     */
    public void announceCurrentPlayer(Faction faction) {
        textTerminal.println(convertFaction(faction) + ": It's your turn!");
    }

    /**
     * This method announces which number has been rolled by the dice.
     *
     * @param diceValue is the number generated from two dice.
     */
    public void announceDice(int diceValue) {
        textTerminal.println("You have thrown a " + diceValue);
    }

    /**
     * This method prints out the gameBoard in its current state.
     *
     * @param board defines which board is being printed.
     */
    public void printBoard(SiedlerBoardTextView board) {
        textTerminal.println(board.toString());
    }

    /**
     * This method tells lets you choose a structure to build.
     *
     * @return it returns the type of the structure chosen.
     */
    public Structure chooseStructure() {
        return textIO.newEnumInputReader(Structure.class).read("What would you like to build?");
    }

    /**
     * This method lets you choose a settlement to upgrade.
     *
     * @return it returns the point of the settlement which is being upgraded.
     */
    public Point chooseSettlementToUpgrade() {
        textTerminal.println("Which Settlement would you like to upgrade to a city?");
        int xCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_X_COORDINATE_BOARD).read("Enter the horizontal coordinate of where you would like to place your structure.");
        int yCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_Y_COORDINATE_BOARD).read("Enter the vertical coordinate of where you would like to place your structure.");
        return new Point(xCoordinate, yCoordinate);
    }

    /**
     * This method lets you choose where you want to place a settlement.
     *
     * @return it returns the point of the corner on which you want to place your settlement.
     */
    public Point chooseSettlementPlacement() {
        textTerminal.println("Where would you like to place your Settlement?");
        int xCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_X_COORDINATE_BOARD).read("Enter the horizontal coordinate of where you would like to place your structure.");
        int yCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_Y_COORDINATE_BOARD).read("Enter the vertical coordinate of where you would like to place your structure.");
        return new Point(xCoordinate, yCoordinate);
    }

    /**
     * This method lets you choose where you want to place a road.
     *
     * @return it returns the two corner coordinates in between which you want to place your road.
     */
    public Point[] chooseRoadPlacement() {
        textTerminal.println("Where would you like to place your Road?");
        Point[] road = new Point[2];
        int xCoordinate1 = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_X_COORDINATE_BOARD).read("Enter the horizontal coordinate of the starting Point of your Road.");
        int yCoordinate1 = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_Y_COORDINATE_BOARD).read("Enter the vertical coordinate of the starting Point of your Road.");
        road[0] = new Point(xCoordinate1, yCoordinate1);

        int xCoordinate2 = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_X_COORDINATE_BOARD).read("Enter the horizontal coordinate of the ending Point of your Road.");
        int yCoordinate2 = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_Y_COORDINATE_BOARD).read("Enter the vertical coordinate of the ending Point of your Road.");
        road[1] = new Point(xCoordinate2, yCoordinate2);

        return road;
    }

    /**
     * This method prints out the winner of the game.
     *
     * @param faction is the faction of the winning player.
     */
    public void declareWinner(Faction faction) {
        textTerminal.println("The winner is: Player " + convertFaction(faction));
    }

    /**
     * This method ask the user to choose an action of the {@link Action} enum.
     *
     * @return the chosen action.
     */
    public Action getAction() {
        return textIO.newEnumInputReader(Action.class).read("Which action would you like to perform?");
    }

    /**
     * Ends the Siedler game program.
     */
    public void endProgram() {
        textIO.dispose();
    }

    /**
     * This method asks the player if he wants to end the program.
     *
     * @return true, if the player wants to end the program.
     */
    public boolean exitRequest() {
        return textIO.newBooleanInputReader().read("Do you want to exit the programm?");
    }

    /**
     * Prints out the current inventory of a player.
     *
     * @param inventory is a map which tells you which resources exist in which amounts.
     */
    public void printInfo(Map<Resource, Integer> inventory) {
        textTerminal.println("Your inventory consists of the following resources:");
        for (Resource resource : inventory.keySet()) {
            textTerminal.println(" * " + resource + ": " + inventory.get(resource));
        }
    }

    /**
     * This method lets you choose a resource you want to trade with the Bank.
     *
     * @return it returns the resource the player chose.
     */
    public Resource chooseResourceToTrade() {
        return textIO.newEnumInputReader(Resource.class).read("Which resource would you like to trade of?");
    }

    /**
     * This method lets you resource you want to receive for your trade with the bank.
     *
     * @return it returns the resource the player chose.
     */
    public Resource chooseResourceToGet() {
        return textIO.newEnumInputReader(Resource.class).read("Which resource would you like to get?");
    }

    /**
     * This method tells the player that the trade he wanted to do failed.
     */
    public void announceTradingFailed() {
        textTerminal.println("The trading failed.");
    }

    /**
     * This method tells the player that the structure he wanted to build couldn't be built.
     *
     * @param structure is the type of structure the player wanted to build.
     */
    public void announceBuildingFailed(Structure structure) {
        textTerminal.println("The construction of your " + structure + " failed");
    }

    /**
     * Method used to choose the placement of the thief.
     *
     * @return returns the point chosen.
     */
    public Point chooseThiefPlacement() {
        textTerminal.println("Where would you like to place the thief?");
        int xCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_X_COORDINATE_BOARD).read("Enter the x coordinate:");
        int yCoordinate = textIO.newIntInputReader().withMinVal(MIN_COORDINATE_BOARD).withMaxVal(MAX_Y_COORDINATE_BOARD).read("Enter the y coordinate:");
        return new Point(xCoordinate, yCoordinate);
    }

    /**
     * This method announces that the thief has stolen a players resources.
     *
     * @param resourcesStolen are the resources that have been stolen from the player.
     */
    public void announceThiefStoleResources(Map<Faction, List<Resource>> resourcesStolen) {
        for (Faction faction : resourcesStolen.keySet()) {
            if (!resourcesStolen.get(faction).isEmpty()) {
                textTerminal.println("Player " + convertFaction(faction) + ", following of your resources have been stolen by the thief:");
                for (Resource resource : resourcesStolen.get(faction)) {
                    textTerminal.println(resource.toString());
                }
            }
        }
    }

    /**
     * This method announces which resources the player gained and had added to his stock.
     *
     * @param resourcesGained is the type of resource which the player gained.
     */
    public void announceResourcesGained(Map<Faction, List<Resource>> resourcesGained) {
        for (Faction faction : resourcesGained.keySet()) {
            if (!resourcesGained.get(faction).isEmpty()) {
                textTerminal.println("Player " + convertFaction(faction) + ", you gained following resources:");
                for (Resource resource : resourcesGained.get(faction)) {
                    textTerminal.println(resource.toString());
                }
            }
        }
    }

    /**
     * Method lets the player know that the thief couldn't be placed.
     */
    public void announceThiefPlacementFailed() {
        textTerminal.println("The thief could not be placed.");
    }

    /**
     * Method lets the player know who was affected by the thief.
     *
     * @param victim         is the player whose cards have been stolen.
     * @param stolenResource is the resource that has been stolen.
     */
    public void announcePlacedThiefStoleResources(Faction victim, String stolenResource) {
        textTerminal.println("Player " + convertFaction(victim) + ", following resource has been stolen: " + stolenResource);
    }

    /**
     * This method prints that no resource was stolen, since the thief is on a field where nobody built a settlement or a city.
     */
    public void announceNothingGotStolen() {
        textTerminal.println("No player is on the field you placed the thief on. Therefore nothing got stolen.");
    }

    private String convertFaction(Faction faction) {
        return switch (faction) {
            case BLUE -> "blue";
            case RED -> "red";
            case GREEN -> "green";
            case YELLOW -> "yellow";
        };
    }
}