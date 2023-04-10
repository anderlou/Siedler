package ch.zhaw.catan;

import ch.zhaw.catan.games.ThreePlayerStandard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import static ch.zhaw.catan.Config.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link SiedlerGame} class.<br>
 *
 * <br>This test class has the following equivalence classes:<br>
 *
 * <br>01. SettlementCanBeBuilt: Tests if the settlement can be build under specific conditions.
 * <br>02. SettlementCantBeBuilt: Tests if the settlement can't be build under specific conditions.
 * <br>03. CityCanBeBuilt: Tests if the city can be build under specific conditions.
 * <br>04. CityCantBeBuilt: Tests if the city can't be build under specific conditions.
 * <br>06. RoadCanBeBuilt: Tests if the road can be build under specific conditions.
 * <br>07. RoadCantBeBuilt: Tests if the road can't be build under specific conditions.
 * <br>08. TradeWorks: Tests if the trading works under specific conditions.
 * <br>09. TradeDoesNotWorks: Tests if the trading does not work under specific conditions.
 * <br>10. VictoryPoints: Are the VictoryPoints wright counted?
 * <br>11. ThereIsAWinner: Is the winner wright detected?
 * <br>12. ThereIsNotAWinner: Is the winner wright detected? When the points are under limit.
 * <br>13. ResourceChangeBank: Has the bank more new resources after a transaction?
 * <br>14. ResourceChangePlayer: Has the player more new resources after a transaction?
 * <br>15. SiedlerGameCanBeInitialized: Tests valid input for the constructor of SiedlerGame.
 * <br>16. SiedlerGameCantBeInitialized: Tests invalid input for the constructor of SiedlerGame.
 * <br>17. CanSwitchToNextPlayer: Tests the switch to the next Player.
 * <br>18. CanSwitchToPreviousPlayer: Tests the switch to the previous player.
 * <br>19. CanGetPlayerFaction: Tests the faction of the players.
 * <br>20. CanGetBoard: Tests the board of SiedlerGame.
 * <br>21. CanGetCurrentPlayerFaction: Tests the faction of the current player.
 * <br>22. CanGetCurrentPlayerResourceStock: Tests the resource stock of the current player.
 * <br>23. CanPlaceInitialSettlement: Tests the placement of an initial settlement for valid positions.
 * <br>24. CantPlaceInitialSettlement: Tests the placement of an initial settlement for invalid positions.
 * <br>25. CanPlaceInitialRoad: Tests the placement of an initial road for valid positions.
 * <br>26. CantPlaceInitialRoad: Tests the placement of an initial road for invalid positions.
 * <br>27. throwDiceNonThief: Tests the throwDice method for numbers not associated with the thief.
 * <br>28. throwDiceThief: Tests the throwDice method for numbers associated with the thief.
 * <br>29. ThiefWorks: The thief could be successfully placed.
 * <br>30. ThiefDoesNotWork: The thief could not be placed on the selected field.
 *
 * @author StackOverflow
 * @version 1.0
 */
class SiedlerGameTest {

    private SiedlerGame siedlerGame;
    private final int winPoints = 7;

    /**
     * This test tests the {@link SiedlerGame#SiedlerGame(int, int)} method. It checks that for the correct values of
     * winPoints and numberOfPlayers no exception is being thrown. This is a positive test and of the equivalence class 15.
     */
    @Test
    public void constructorTest() {
        assertDoesNotThrow(() -> new SiedlerGame(winPoints, 2));
        assertDoesNotThrow(() -> new SiedlerGame(winPoints, 3));
        assertDoesNotThrow(() -> new SiedlerGame(winPoints, 4));

        assertDoesNotThrow(() -> new SiedlerGame(3, 2));
    }

    /**
     * This test tests the {@link SiedlerGame#SiedlerGame(int, int)} method. It checks that for invalid values of
     * numberOfPlayers an {@link IllegalArgumentException} is being thrown.
     * This is a negative test and of the equivalence class 16.
     */
    @Test
    public void constructorInvalidNumberOfPlayersTest(){
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(winPoints, -1));
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(winPoints, 0));
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(winPoints, 1));
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(winPoints, 5));
    }

    /**
     * This test tests the {@link SiedlerGame#SiedlerGame(int, int)} method. It checks that for invalid values of
     * winPoints an {@link IllegalArgumentException} is being thrown.
     * This is a negative test and of the equivalence class 16.
     */
    @Test
    public void constructorInvalidNumberOfWinPointsTest(){
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(2, 2));
    }

    /**
     * This test tests the {@link SiedlerGame#switchToNextPlayer()} method. It checks that the order of the displayed faction is correct and
     * that after the last player it starts again with the first player. This test is being performed for every amount of participants allowed.
     * This is a positive test and of the equivalence class 17.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void switchToNextPlayerTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        List<Faction> playerFactions = siedlerGame.getPlayerFactions();
        for (int i = 0; i < numberOfPlayers; i++) {
            assertEquals(playerFactions.get(i), siedlerGame.getCurrentPlayerFaction());
            siedlerGame.switchToNextPlayer();
        }
    }

    /**
     * This test tests the {@link SiedlerGame#switchToPreviousPlayer()} method. It checks that the reversed order of the displayed factions is correct
     * and that after the first player it starts again with the last player. This test is being performed for every amount of participants allowed.
     * This is a positive test of the equivalence class 18.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void switchToPreviousPlayer(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        List<Faction> playerFactions = siedlerGame.getPlayerFactions();

        assertEquals(playerFactions.get(0), siedlerGame.getCurrentPlayerFaction());

        for (int i = 1; i < numberOfPlayers; i++) {
            siedlerGame.switchToPreviousPlayer();
            assertEquals(playerFactions.get(numberOfPlayers - i), siedlerGame.getCurrentPlayerFaction());
        }
    }

    /**
     * Tests the {@link SiedlerGame#getPlayerFactions()} method and checks that
     * the colors are defined correctly. The expected behaviour is that the factions of the players match the predefined factions
     * from the {@link Config} class. This is a positive test and of the equivalence class 19.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void getPlayerFactionsTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        List<Faction> allFactions = List.of(Faction.RED, Faction.BLUE, Faction.GREEN, Faction.YELLOW);
        List<Faction> expected = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            expected.add(allFactions.get(i));
        }
        assertEquals(expected, siedlerGame.getPlayerFactions());
    }

    /**
     * Tests the {@link SiedlerGame#getBoard()} method. This test checks that the board is properly initialized.
     * This is a positive test and of the equivalence class 20.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void getBoardTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        assertNotNull(siedlerGame.getBoard());
    }

    /**
     * Tests the {@link SiedlerGame#getCurrentPlayerFaction()} method. It checks that after switching to the next player,
     * the correct faction is associated to the correct player. This is a positive test and of the equivalence class 21.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void getCurrentPlayerFactionTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        List<Faction> allFactions = siedlerGame.getPlayerFactions();

        for (int i = 0; i < numberOfPlayers; i++) {
            assertSame(siedlerGame.getCurrentPlayerFaction(), allFactions.get(i));
            siedlerGame.switchToNextPlayer();
        }

        assertSame(siedlerGame.getCurrentPlayerFaction(), allFactions.get(0));
    }

    /**
     * Tests the {@link SiedlerGame#getCurrentPlayerResourceStock(Resource)} method. This test checks that every
     * player's stock is empty in the beginning and therefore the method returns 0 for every resource and every player.
     * This is a positive test of the equivalence class 22.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void getCurrentPlayerResourceStockEmptyStockTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        for (int i = 0; i < numberOfPlayers; i++) {
            for (Resource resource : Resource.values()) {
                assertEquals(0, siedlerGame.getCurrentPlayerResourceStock(resource));
            }
            siedlerGame.switchToNextPlayer();
        }
    }

    /**
     * Tests the {@link SiedlerGame#getCurrentPlayerResourceStock(Resource)} method. This test checks that for the
     * predefined game scenario {@link ThreePlayerStandard#INITIAL_PLAYER_CARD_STOCK} the resource stock is returned correctly.
     * This is a positive test of the equivalence class 22.
     */
    @Test
    public void getCurrentPlayerResourceStockInitialStockTest() {
        Map<Faction, Map<Resource, Integer>> playerCardStockExpected1 = ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK;
        SiedlerGame afterSetupPhase = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        LinkedList<Player> players = afterSetupPhase.getPlayers();
        Map<Faction, Map<Resource, Integer>> playerCardStockActual1 = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Map<Resource, Integer> resourceStock = new HashMap<>();
            for (Resource resource : Resource.values()) {
                resourceStock.put(resource, afterSetupPhase.getCurrentPlayerResourceStock(resource));
            }
            playerCardStockActual1.put(afterSetupPhase.getCurrentPlayerFaction(), resourceStock);
            afterSetupPhase.switchToNextPlayer();
        }
        assertEquals(playerCardStockExpected1, playerCardStockActual1);

    }

    /**
     * Tests the {@link SiedlerGame#getCurrentPlayerResourceStock(Resource)} method. This test checks that for the
     * predefined game scenario {@link ThreePlayerStandard#BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK} the resource stock is returned correctly.
     * This is a positive test of the equivalence class 22.
     */
    @Test
    public void getCurrentPlayerResourceStockAlmostEmptyBankTest() {
        Map<Faction, Map<Resource, Integer>> playerCardStockExpected2 = ThreePlayerStandard.BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK;
        SiedlerGame afterSetupPhaseAlmostEmptyBank = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = afterSetupPhaseAlmostEmptyBank.getPlayers();
        Map<Faction, Map<Resource, Integer>> playerCardStockActual2 = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Map<Resource, Integer> resourceStock = new HashMap<>();
            for (Resource resource : Resource.values()) {
                resourceStock.put(resource, afterSetupPhaseAlmostEmptyBank.getCurrentPlayerResourceStock(resource));
            }
            playerCardStockActual2.put(afterSetupPhaseAlmostEmptyBank.getCurrentPlayerFaction(), resourceStock);
            afterSetupPhaseAlmostEmptyBank.switchToNextPlayer();
        }
        assertEquals(playerCardStockExpected2, playerCardStockActual2);
    }

    /**
     * Tests the {@link SiedlerGame#getCurrentPlayerResourceStock(Resource)} method. This test checks that for the
     * predefined game scenario {@link ThreePlayerStandard#PLAYER_ONE_READY_TO_BUILD_FIFTH_SETTLEMENT_RESOURCE_CARD_STOCK}
     * the resource stock is returned correctly.
     * This is a positive test of the equivalence class 22.
     */
    @Test
    public void getCurrentPlayerResourceStockToBuildFifthSettlementTest() {

        Map<Faction, Map<Resource, Integer>> playerCardStockExpected3 = ThreePlayerStandard.PLAYER_ONE_READY_TO_BUILD_FIFTH_SETTLEMENT_RESOURCE_CARD_STOCK;
        SiedlerGame playerOneReadyToBuildFifthSettlement = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(winPoints);
        LinkedList<Player> players = playerOneReadyToBuildFifthSettlement.getPlayers();
        Map<Faction, Map<Resource, Integer>> playerCardStockActual3 = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Map<Resource, Integer> resourceStock = new HashMap<>();
            for (Resource resource : Resource.values()) {
                resourceStock.put(resource, playerOneReadyToBuildFifthSettlement.getCurrentPlayerResourceStock(resource));
            }
            playerCardStockActual3.put(playerOneReadyToBuildFifthSettlement.getCurrentPlayerFaction(), resourceStock);
            playerOneReadyToBuildFifthSettlement.switchToNextPlayer();
        }
        assertEquals(playerCardStockExpected3, playerCardStockActual3);
    }

    /**
     * Tests the {@link SiedlerGame#placeInitialSettlement(Point, boolean)} method. This test checks that for a valid
     * board position the method returns true. Additionally, this test checks that the resource stock only is affected,
     * when the boolean is set to true.
     * This is a positive test of the equivalence class 23.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void placeInitialSettlementTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);
        LinkedList<Player> players = siedlerGame.getPlayers();
        Point pointSurroundedByAllResourceLands = new Point(7, 7);
        List<Resource> resourceForSettlementOn7_7 = List.of(Resource.WOOL, Resource.ORE, Resource.GRAIN);
        Point pointAdjacentToDesert = new Point(8, 10);
        List<Resource> resourcesForSettlement8_10 = List.of(Resource.GRAIN, Resource.ORE);

        Map<Resource, Integer> resourceStockExpected = new HashMap<>();
        for (Resource resource : Resource.values()) {
            resourceStockExpected.put(resource, 0);
        }
        for (Resource resource : resourceForSettlementOn7_7) {
            resourceStockExpected.put(resource, resourceStockExpected.get(resource) + 1);
        }
        assertTrue(siedlerGame.placeInitialSettlement(pointSurroundedByAllResourceLands, true));
        assertEquals(resourceStockExpected, players.get(0).getResources());

        for (Resource resource : resourcesForSettlement8_10) {
            resourceStockExpected.put(resource, resourceStockExpected.get(resource) + 1);
        }
        assertTrue(siedlerGame.placeInitialSettlement(pointAdjacentToDesert, true));
        assertEquals(resourceStockExpected, players.get(0).getResources());
    }

    /**
     * Tests the {@link SiedlerGame#placeInitialSettlement(Point, boolean)} method. This test checks that not two
     * settlements can be placed on the same position. Neither by the same player, nor by another player. Furthermore,
     * it checks that when the placement was not successful the payout does not take place even though the boolean was
     * set to true. Additionally, it checks that there cannot be two settlements placed directly next to each other.
     * This is a negative test of the equivalence class 24.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void placeInitialSettlementEdgeCasesTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        Point pointWithOnlyOneNonWaterLand = new Point(3, 7);

        assertTrue(siedlerGame.placeInitialSettlement(pointWithOnlyOneNonWaterLand, false));
        assertFalse(siedlerGame.placeInitialSettlement(pointWithOnlyOneNonWaterLand, false));
        siedlerGame.switchToNextPlayer();
        assertFalse(siedlerGame.placeInitialSettlement(pointWithOnlyOneNonWaterLand, false));

        Map<Resource, Integer> currentPlayerStockExpected = new HashMap<>();
        for (Resource resource : Resource.values()) {
            currentPlayerStockExpected.put(resource, siedlerGame.getCurrentPlayerResourceStock(resource));
        }
        assertFalse(siedlerGame.placeInitialSettlement(pointWithOnlyOneNonWaterLand, true));
        Map<Resource, Integer> currentPlayerStockActual = new HashMap<>();
        for (Resource resource : Resource.values()) {
            currentPlayerStockActual.put(resource, siedlerGame.getCurrentPlayerResourceStock(resource));
        }
        assertEquals(currentPlayerStockExpected, currentPlayerStockActual);

        Point onNeighbouringCorner = new Point(3, 9);

        assertFalse(siedlerGame.placeInitialSettlement(onNeighbouringCorner, false));

        assertEquals(currentPlayerStockExpected, currentPlayerStockActual);

        assertFalse(siedlerGame.placeInitialSettlement(onNeighbouringCorner, true));

        assertEquals(currentPlayerStockExpected, currentPlayerStockActual);
    }

    /**
     * Tests the {@link SiedlerGame#placeInitialSettlement(Point, boolean)} method. This test checks no settlement can
     * be placed on a corner that is only surrounded by water or is not even on the board. Additionally, it checks that
     * no payout takes place for invalid settlement positions.
     * This is a negative test of the equivalence class 24.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void placeInitialSettlementInvalidCasesTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        Point pointOnlySurroundedByWater = new Point(5, 1);

        assertFalse(siedlerGame.placeInitialSettlement(pointOnlySurroundedByWater, false));
        assertFalse(siedlerGame.placeInitialSettlement(pointOnlySurroundedByWater, true));

        Point pointOffBoard = new Point(1, 4);

        assertFalse(siedlerGame.placeInitialSettlement(pointOffBoard, true));
        assertFalse(siedlerGame.placeInitialSettlement(pointOffBoard, false));

        for (Resource resource : Resource.values()) {
            assertEquals(0, siedlerGame.getCurrentPlayerResourceStock(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#placeInitialRoad(Point, Point)} method.
     * This is a positive test of the equivalence class 25.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void placeInitialRoadTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        Point settlementOnBoarder1 = new Point(8, 4);

        siedlerGame.placeInitialSettlement(settlementOnBoarder1, false);
        siedlerGame.placeInitialRoad(new Point(8, 4), new Point(8, 6));
    }

    /**
     * Tests the {@link SiedlerGame#placeInitialRoad(Point, Point)} method. This test checks that if no settlement is
     * placed also no road can be placed. Furthermore, it checks that in the initial building phase every road is placed next
     * to the previously built settlement and therefore no settlement will be standing alone. Additionally, this test
     * checks that roads can only be placed between two valid points and hence no road can lead into the sea.
     * On top of that the test checks that the start point of the road is next to the end point of the road.
     * This is a negative test of the equivalence class 26.
     *
     * @param numberOfPlayers the number of players playing the game.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    public void placeInitialRoadInvalidCasesTest(int numberOfPlayers) {
        siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        assertFalse(siedlerGame.placeInitialRoad(new Point(8, 4), new Point(8, 6)));

        Point settlementOnBoarder1 = new Point(8, 4);
        Point settlementOnBoarder2 = new Point(3, 15);

        Point firstPointAdjacentToSettlementOnBoarder1 = new Point(8,6);
        Point secondPointAdjacentToSettlementOnBoarder1 = new Point(7,3);

        assertTrue(siedlerGame.placeInitialSettlement(settlementOnBoarder1, false));
        assertTrue(siedlerGame.placeInitialRoad(settlementOnBoarder1, firstPointAdjacentToSettlementOnBoarder1));
        assertTrue(siedlerGame.placeInitialSettlement(settlementOnBoarder2, false));
        assertFalse(siedlerGame.placeInitialRoad(settlementOnBoarder1, secondPointAdjacentToSettlementOnBoarder1));


        Point validPoint = new Point(3, 15);
        Point invalidPoint = new Point(2, 16);
        assertFalse(siedlerGame.placeInitialRoad(validPoint, invalidPoint));

        Point invalidPoint1 = new Point(2,4);
        Point invalidPoint2 = new Point(2,2);
        assertFalse(siedlerGame.placeInitialRoad(invalidPoint1, invalidPoint2));

        Point notAdjacent = new Point(3,19);
        assertFalse(siedlerGame.placeInitialRoad(settlementOnBoarder2, notAdjacent));
    }

    /**
     * Tests the {@link SiedlerGame#throwDice(int)} method. This test check that when no player has any settlements or
     * cities, none of the players gain any resources, no matter what dice number has been thrown.
     * This is a positive test of the equivalence class 27.
     *
     * @param dice all dice values which do not invoke the thief.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12})
    public void throwDiceEmptyBoardTest(int dice) {
        siedlerGame = new SiedlerGame(winPoints, ThreePlayerStandard.NUMBER_OF_PLAYERS);
        for (int i = 0; i < siedlerGame.getPlayerFactions().size(); i++) {
            assertTrue(siedlerGame.throwDice(dice).get(siedlerGame.getCurrentPlayerFaction()).isEmpty());
            siedlerGame.switchToNextPlayer();
        }

        // checks throwDice after initial setUp.
        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        assertEquals(ThreePlayerStandard.INITIAL_DICE_THROW_PAYOUT.get(dice), siedlerGame.throwDice(dice));
    }

    /**
     * Tests the {@link SiedlerGame#throwDice(int)} method. This test checks that the correct players receive the
     * correct amount of resources after the {@link ThreePlayerStandard#getAfterSetupPhase(int)}.
     * This is a positive test of the equivalence class 27.
     * @param dice all dice values which do not invoke the thief.
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12})
    public void throwDiceAfterInitialSetUpTest(int dice) {
        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        assertEquals(ThreePlayerStandard.INITIAL_DICE_THROW_PAYOUT.get(dice), siedlerGame.throwDice(dice));
    }

    /**
     * Tests the {@link SiedlerGame#throwDice(int)} method. This method checks that for a city the payout is double the
     * amount it is for a settlement.
     * This is a positive test of the equivalence class 27.
     */
    @Test
    public void throwDiceSingleCityTest() {
        siedlerGame = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(7);

        assertTrue(siedlerGame.buildCity(new Point(5,7)));

        Map<Faction, List<Resource>> expectedReturn = Map.of(
                Faction.RED, List.of(Resource.ORE, Resource.ORE),
                Faction.BLUE, new ArrayList<>(),
                Faction.GREEN, List.of(Resource.BRICK)
        );

        Map<Faction, List<Resource>> result = siedlerGame.throwDice(4);
        assertEquals(expectedReturn, result);
    }

    /**
     * Tests the {@link SiedlerGame#throwDice(int)} method. This test checks that an {@link IllegalArgumentException} is
     * being thrown when the parameter represents an invalid dice number.
     * This is a negative test of the equivalence class 27.
     *
     * @param dice all dice values which do not invoke the thief.
     */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 13})
    public void throwDiceInvalidDiceTest(int dice) {
        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        for (int i = 0; i < siedlerGame.getPlayerFactions().size(); i++) {
            assertThrows(IllegalArgumentException.class, () -> siedlerGame.throwDice(dice).get(siedlerGame.getCurrentPlayerFaction()));
            siedlerGame.switchToNextPlayer();
        }
    }

    /**
     * Tests the {@link SiedlerGame#throwDice(int)} method. This test checks that when the
     * {@link SiedlerGame#THIEF_DICE_THROW} is thrown the amount of resources a player has is being halved if the amount
     * is bigger than {@link Config#MAX_CARDS_IN_HAND_NO_DROP}.
     * This is a positive test of the equivalence class 28.
     */
    @Test
    public void throwDiceThief() {
        int thiefDiceNumber = 7;
        siedlerGame = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(winPoints);
        assertEquals(5, siedlerGame.throwDice(thiefDiceNumber).get(Faction.RED).size());
        assertEquals(0, siedlerGame.throwDice(thiefDiceNumber).get(Faction.RED).size());
    }

    /**
     * This method places a city at a desired point without checking if the resources are available or whether the position is valid.
     * Only allowed to use during testing.
     *
     * @param position, the desired position of the city
     * @param player,   the player which would like to place a city
     */

    private void placeCity(Point position, Player player) {
        SiedlerBoard board = siedlerGame.getBoard();
        board.setCorner(position, player.getFaction().toString().toUpperCase());

        player.addToAvailableStructure(Structure.SETTLEMENT);
        player.removeFromAvailableStructure(Structure.CITY);
        board.addAllBuildingElementOnBoard(new City(position, player.getFaction()));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is next to a settlement with the same faction. The expected behaviour is that
     * the road can be built. This is a positive test. The equivalence class is 06.
     */
    @Test
    public void roadPositionNextToSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertTrue(model.buildRoad(new Point(5, 7), new Point(5, 9)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method doesn't work, when the position is next to a settlement from another faction. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void roadPositionNextToUnknownSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertFalse(model.buildRoad(new Point(3, 9), new Point(2, 10)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is next to a city with the same faction. The expected behaviour is that
     * the road can be built. This is a positive test. The equivalence class is 06.
     */
    @Test
    public void roadPositionNextToCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        model.buildCity(new Point(5, 7));
        assertTrue(model.buildRoad(new Point(5, 7), new Point(5, 9)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method doesn't work, when the position is next to a city from another faction. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void roadPositionNextToUnknownCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        model.switchToNextPlayer();
        model.buildCity(new Point(8, 4));
        model.switchToPreviousPlayer();

        assertFalse(model.buildRoad(new Point(8, 4), new Point(7, 3)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is next to a street with the same faction. The expected behaviour is that
     * the road can be built. This is a positive test. The equivalence class is 06.
     */
    @Test
    public void roadPositionNextToStreet() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is next to a street with another faction. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void roadPositionNextToUnknownStreet() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertFalse(model.buildRoad(new Point(8, 6), new Point(7, 7)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is next to nothing. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void roadPositionNextToNothing() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertFalse(model.buildRoad(new Point(9, 7), new Point(9, 9)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the position is null, invalid or not next to each other. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void roadPositionInvalid() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);

        assertFalse(model.buildRoad(new Point(18, 1), new Point(0, 9)));
        assertFalse(model.buildRoad(new Point(5, 3), new Point(8, 6)));
        assertFalse(model.buildRoad(null, new Point(0, 9)));

        model.switchToNextPlayer();
        model.buildRoad(new Point(9, 3), new Point(8, 4));
        assertFalse(model.buildRoad(new Point(9, 3), new Point(9, 1)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works, when the player has enough resources to pay for a road. The expected behaviour is that
     * the road can be build. This is a positive test. The equivalence class is 06.
     */
    @Test
    public void playerHasEnoughResourcesToBuildRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        List<Resource> costs = Structure.ROAD.getCosts();
        Player player1 = players.get(0);
        Map<Resource, Integer> playerResourceStock = player1.getResources();
        for (Resource resource : costs) {
            int count = playerResourceStock.get(resource) - 1;
            assertTrue(count >= 0);
        }
        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method doesn't work, when the player has not enough resources to pay for a road. The expected behaviour is that
     * the road can't be built. This is a negative test. The equivalence class is 07.
     */
    @Test
    public void playerHasNotEnoughResourcesToBuildRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(winPoints);

        for (int i = 0; i < model.getPlayerFactions().size(); i++) {
            assertFalse(model.buildRoad(new Point(6, 4), new Point(6, 6)));
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method works, when the has enough resources to pay for a city. The expected behaviour is that
     * the city can be build. This is a positive test. The equivalence class is 03.
     */
    @Test
    public void playerHasEnoughResourcesToBuildCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        List<Resource> costs = Structure.CITY.getCosts();
        Player player = players.get(0);
        Map<Resource, Integer> playerResourceStock = player.getResources();
        for (Resource resource : costs) {
            int count = playerResourceStock.get(resource) - 1;
            assertTrue(count >= 0);
        }
        assertTrue(model.buildCity(new Point(5, 7)));
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method doesn't work, when the player hasn't enough resources. The expected behaviour is that
     * the city can't be built. This is a negative test. The equivalence class is 04.
     */
    @Test
    public void playerHasNotEnoughResourcesToBuildCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(winPoints);

        for (int i = 0; i < model.getPlayerFactions().size(); i++) {
            assertFalse(model.buildCity(new Point(5, 7)));
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method works, when at the position exists a settlement. The expected behaviour is that
     * the city can be built. This is a positive test. The equivalence class is 03.
     */
    @Test
    public void cityPositionHasASettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Point point = new Point(5, 7);
        assertNotNull(model.getBoard().getCorner(point));
        assertEquals(players.get(0).getFaction().toString(), model.getBoard().getCorner(point));
        assertTrue(model.buildCity(point));
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method doesn't work, when the position is invalid, as empty or null. The expected behaviour is that
     * the city can be built. This is a negative test. The equivalence class is 04.
     */
    @Test
    public void cityPositionInvalid() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertFalse(model.buildCity(null));
        assertFalse(model.buildCity(new Point()));
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method doesn't work, when the settlement at the position is unknown. The expected behaviour is that
     * the city can be built. This is a negative test. The equivalence class is 04.
     */
    @Test
    public void cityPositionHasAUnknownSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Point point = new Point(8, 4);
        assertNotNull(model.getBoard().getCorner(point));
        assertNotEquals(players.get(0).getFaction().toString(), model.getBoard().getCorner(point));
        assertFalse(model.buildCity(point));
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method doesn't work, when at the position is nothing placed. The expected behaviour is that
     * the city can be built. This is a negative test. The equivalence class is 04.
     */
    @Test
    public void cityPositionIsEmpty() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Point positionWithNoElement = new Point(7, 7);
        assertNull(model.getBoard().getCorner(positionWithNoElement));
        assertFalse(model.buildCity(positionWithNoElement));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method works, when the player has enough resources. The expected behaviour is that
     * the settlement can be built. This is a positive test. The equivalence class is 01.
     */
    @Test
    public void playerHasEnoughResourcesToBuildSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        assertFalse(model.buildCity(new Point(6, 4)));
        assertTrue(model.buildSettlement(new Point(6, 4)));
        assertTrue(model.buildCity(new Point(6, 4)));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method doesn't work, when the player hasn't enough resources. The expected behaviour is that
     * the settlement can't be built. This is a positive test. The equivalence class is 02.
     */
    @Test
    public void playerHasNotEnoughResourcesToBuildSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        List<Resource> costs = Structure.SETTLEMENT.getCosts();
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Map<Resource, Integer> playerResourceStock = player1.getResources();

        for (Resource resource : costs) {
            int count = playerResourceStock.get(resource) - 1;
            assertTrue(count >= 0);
        }

        Point positionNextToRoad = new Point(6, 4);
        assertFalse(model.buildCity(positionNextToRoad));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method doesn't work, when the position is invalid, as null, not on the board or in the water. The expected behaviour is that
     * the settlement can't be built. This is a negative test. The equivalence class is 02.
     */
    @Test
    public void settlementPositionInvalid() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        assertFalse(model.buildSettlement(null));
        Point positionOutOfTheBoard = new Point(2, 1);
        Point positionOutOfTheBoard2 = new Point(9, 1);
        assertFalse(model.buildSettlement(positionOutOfTheBoard));
        assertFalse(model.buildSettlement(positionOutOfTheBoard2));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method doesn't work, when the new position has neighbors from the same or a different faction. The expected behaviour is that
     * the settlement can't be built. This is a negative test. The equivalence class is 02.
     */
    @Test
    public void settlementPositionHasNeighbors() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Point positionWithNeighbors = new Point(3, 13);
        assertFalse(model.buildSettlement(positionWithNeighbors));

        Point positionWihNeighbors2 = new Point(5, 9);
        assertFalse(model.buildSettlement(positionWihNeighbors2));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method works, when the position is next to an own road. The expected behaviour is that
     * the settlement can be built. This is a positive test. The equivalence class is 01.
     */
    @Test
    public void settlementNextToRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        SiedlerBoard board = model.getBoard();
        model.switchToNextPlayer();
        model.switchToNextPlayer();

        assertTrue(model.buildRoad(new Point(2, 10), new Point(3, 9)));
        assertTrue(board.getAdjacentEdges(new Point(3, 9)).contains(model.getCurrentPlayerFaction().toString()));

        players.get(2).addResource(Resource.WOOL,1);
        players.get(2).addResource(Resource.GRAIN,1);
        assertTrue(model.buildSettlement(new Point(3, 9)));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method doesn't work, when the position is next to a road from another faction. The expected behaviour is that
     * the settlement can't be built. This is a negative test. The equivalence class is 02.
     */
    @Test
    public void settlementNotNextToRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        SiedlerBoard board = model.getBoard();
        Point point = new Point(3, 13);

        assertFalse(board.getAdjacentEdges(point).contains(model.getCurrentPlayerFaction().toString()));
        assertFalse(model.buildSettlement(point));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)}, {@link SiedlerGame#buildRoad(Point, Point)} and
     * * {@link SiedlerGame#buildCity(Point)} method and checks if the buildings are added
     * to the board. The expected behaviour is that the buildings exist on the board.
     * This is a positive test. The equivalence class is 01, 03 and 06.
     */
    @Test
    public void afterBuildingPhaseAddedToBoard() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        SiedlerBoard board = model.getBoard();
        int countRoad = board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.ROAD);
        int countSettlement = board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.SETTLEMENT);
        int countCity = board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.CITY);

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        assertTrue(model.buildSettlement(new Point(6, 4)));
        assertEquals(countRoad + 1, board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.ROAD));
        assertEquals(countSettlement + 1, board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.SETTLEMENT));

        assertTrue(model.buildCity(new Point(6, 4)));
        assertEquals(countSettlement, board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.SETTLEMENT));
        assertEquals(countCity + 1, board.receiveAllBuildingElementOnBoard(model.getCurrentPlayerFaction(), Structure.CITY));
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)}, {@link SiedlerGame#buildRoad(Point, Point)} and
     * {@link SiedlerGame#buildCity(Point)} method and checks if victoryPoints are added correctly.
     * The expected behaviour is that no points where added after a road, one after a settlement and one after a city.
     * This is a positive test. The equivalence class is 01, 03 and 06.
     */
    @Test
    public void playerVictoryPoints() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        int pointsAfterStart = 2;

        assertEquals(pointsAfterStart, player1.getVictoryPoints());

        model.buildRoad(new Point(6, 4), new Point(6, 6));
        assertEquals(pointsAfterStart, player1.getVictoryPoints());

        model.buildSettlement(new Point(6, 4));
        assertEquals(pointsAfterStart + 1, player1.getVictoryPoints());

        model.buildCity(new Point(6, 4));
        assertEquals(pointsAfterStart + 2, player1.getVictoryPoints());
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works and costs where removed after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the player. This is a positive test. The equivalence class is 04.
     */
    @Test
    public void removeCostsForRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Map<Resource, Integer> playerResource = players.get(0).getResources();
        List<Resource> costsForRoad = Structure.ROAD.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForRoad) {
            int count = players.get(0).getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected - 1);
            } else {
                expectedResources.put(resource, count - 1);
            }
        }

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        for (Resource resource : costsForRoad) {
            assertEquals(expectedResources.get(resource), playerResource.get(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildRoad(Point, Point)} method and checks that
     * the method works and costs where added after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the bank. This is a positive test. The equivalence class is 04.
     */
    @Test
    public void addCostsForRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();
        Map<Resource, Integer> bankResource = bank.getResources();
        List<Resource> costsForRoad = Structure.ROAD.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForRoad) {
            int count = bank.getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected + 1);
            } else {
                expectedResources.put(resource, count + 1);
            }
        }

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        for (Resource resource : costsForRoad) {
            assertEquals(expectedResources.get(resource), bankResource.get(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method works and costs where removed after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the player. This is a positive test. The equivalence class is 01.
     */
    @Test
    public void removeCostsForSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        Map<Resource, Integer> playerResource = players.get(0).getResources();
        List<Resource> costsForSettlement = Structure.SETTLEMENT.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForSettlement) {
            int count = players.get(0).getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected - 1);
            } else {
                expectedResources.put(resource, count - 1);
            }
        }
        assertTrue(model.buildSettlement(new Point(6, 4)));

        for (Resource resource : costsForSettlement) {
            assertEquals(expectedResources.get(resource), playerResource.get(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildSettlement(Point)} method and checks that
     * the method works and costs where added after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the bank. This is a positive test. The equivalence class is 01.
     */
    @Test
    public void addCostsForSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));

        Map<Resource, Integer> bankResource = bank.getResources();
        List<Resource> costsForSettlement = Structure.SETTLEMENT.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForSettlement) {
            int count = bank.getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected + 1);
            } else {
                expectedResources.put(resource, count + 1);
            }
        }

        assertTrue(model.buildSettlement(new Point(6, 4)));

        for (Resource resource : costsForSettlement) {
            assertEquals(expectedResources.get(resource), bankResource.get(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method works and costs where removed after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the player. This is a positive test. The equivalence class is 03.
     */
    @Test
    public void removeCostsForCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));
        assertTrue(model.buildSettlement(new Point(6, 4)));

        Map<Resource, Integer> playerResource = players.get(0).getResources();
        List<Resource> costsForCity = Structure.CITY.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForCity) {
            int count = players.get(0).getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected - 1);
            } else {
                expectedResources.put(resource, count - 1);
            }
        }
        assertTrue(model.buildCity(new Point(6, 4)));

        for (Resource resource : costsForCity) {
            assertEquals(expectedResources.get(resource), playerResource.get(resource));
        }
    }

    /**
     * Tests the {@link SiedlerGame#buildCity(Point)} method and checks that
     * the method works and costs where added after it. The expected behaviour is that
     * the expectedResources is the same as the resources from the bank. This is a positive test. The equivalence class is 03.
     */
    @Test
    public void addCostsForCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();

        assertTrue(model.buildRoad(new Point(6, 4), new Point(6, 6)));
        assertTrue(model.buildSettlement(new Point(6, 4)));

        Map<Resource, Integer> bankResources = bank.getResources();
        List<Resource> costsForCity = Structure.CITY.getCosts();
        HashMap<Resource, Integer> expectedResources = new HashMap<>();

        for (Resource resource : costsForCity) {
            int count = bank.getResourceCount(resource);
            if (expectedResources.containsKey(resource)) {
                int countExpected = expectedResources.get(resource);
                expectedResources.put(resource, countExpected + 1);
            } else {
                expectedResources.put(resource, count + 1);
            }
        }
        assertTrue(model.buildCity(new Point(6, 4)));

        for (Resource resource : costsForCity) {
            assertEquals(expectedResources.get(resource), bankResources.get(resource));
        }
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#throwDice(int)} method and checks that
     * the method works for the bank. The expected behaviour is that
     * the expectedResources is the same as the resources from the bank. This is a positive test. The equivalence class is 13 and 14.
     */
    @Test
    public void receivePayoutFromLandBank() {

        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        LinkedList<Player> players = siedlerGame.getPlayers();
        Player player1 = players.get(0);
        Bank bank =siedlerGame.getBank();

        int countLumberBank = bank.getResourceCount(Resource.LUMBER);
        int countGrainBank = bank.getResourceCount(Resource.GRAIN);
        int countOreBank = bank.getResourceCount(Resource.ORE);

        siedlerGame.throwDice(6);
        siedlerGame.throwDice(2);
        siedlerGame.throwDice(2);

        assertEquals(countLumberBank - 1, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countGrainBank - 2, bank.getResourceCount(Resource.GRAIN));
        assertEquals(countOreBank, bank.getResourceCount(Resource.ORE));

        placeCity(new Point(5, 7), player1);
        siedlerGame.throwDice(6);
        siedlerGame.throwDice(2);
        siedlerGame.throwDice(2);

        assertEquals(countLumberBank - 3, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countGrainBank - 6, bank.getResourceCount(Resource.GRAIN));
        assertEquals(countOreBank, bank.getResourceCount(Resource.ORE));
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#throwDice(int)} method and checks that
     * the method works when the player has one settlement at the field. The expected behaviour is that
     * the resource where removed by the bank and added by the player. This is a positive test.
     * The equivalence class is 13 and 14.
     */
    @Test
    public void receivePayoutFromLandWithOneSettlement() {
        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        LinkedList<Player> players = siedlerGame.getPlayers();
        Player player1 = players.get(0);
        Bank bank = siedlerGame.getBank();

        int countLumberBank = bank.getResourceCount(Resource.LUMBER);
        int countGrainBank = bank.getResourceCount(Resource.GRAIN);
        int countOreBank = bank.getResourceCount(Resource.ORE);

        int countLumber = player1.getResourceCount(Resource.LUMBER);
        int countGrain = player1.getResourceCount(Resource.GRAIN);
        int countOre = player1.getResourceCount(Resource.ORE);

        siedlerGame.throwDice(6);
        siedlerGame.throwDice(2);
        siedlerGame.throwDice(2);

        assertEquals(countLumber + 1, player1.getResourceCount(Resource.LUMBER));
        assertEquals(countGrain + 2, player1.getResourceCount(Resource.GRAIN));
        assertEquals(countOre, player1.getResourceCount(Resource.ORE));

        assertEquals(countLumberBank - 1, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countGrainBank - 2, bank.getResourceCount(Resource.GRAIN));
        assertEquals(countOreBank, bank.getResourceCount(Resource.ORE));
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#throwDice(int)} method and checks that
     * the method works when the player has one City at the field. The expected behaviour is that
     * the resource where removed by the bank and added by the player. This is a positive test.
     * The equivalence class is 13 and 14.
     */
    @Test
    public void receivePayoutFromLandOneCity() {
        //Siedlung
        siedlerGame = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        LinkedList<Player> players = siedlerGame.getPlayers();
        Player player1 = players.get(0);
        Bank bank = siedlerGame.getBank();

        int countLumber = player1.getResourceCount(Resource.LUMBER);
        int countGrain = player1.getResourceCount(Resource.GRAIN);
        int countOre = player1.getResourceCount(Resource.ORE);

        int countLumberBank = bank.getResourceCount(Resource.LUMBER);
        int countGrainBank = bank.getResourceCount(Resource.GRAIN);
        int countOreBank = bank.getResourceCount(Resource.ORE);

        placeCity(new Point(5, 7), player1);
        siedlerGame.throwDice(6);
        siedlerGame.throwDice(2);
        siedlerGame.throwDice(2);

        assertEquals(countLumber + 2, player1.getResourceCount(Resource.LUMBER));
        assertEquals(countGrain + 4, player1.getResourceCount(Resource.GRAIN));
        assertEquals(countOre, player1.getResourceCount(Resource.ORE));

        assertEquals(countLumberBank - 2, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countGrainBank - 4, bank.getResourceCount(Resource.GRAIN));
        assertEquals(countOreBank, bank.getResourceCount(Resource.ORE));
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#throwDice(int)} method and checks that
     * the method works when the player has two settlements at the field. The expected behaviour is that
     * the resource where removed by the bank and added by the player. This is a positive test.
     * The equivalence class is 13 and 14.
     */
    @Test
    public void receivePayoutFromLandTwoSettlement() {
        siedlerGame = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);

        Player current = siedlerGame.getCurrentPlayer();
        Bank bank = siedlerGame.getBank();

        assertTrue(siedlerGame.buildRoad(new Point(9, 15), new Point(8, 16)));
        assertTrue(siedlerGame.buildSettlement(new Point(8, 16)));

        int countLumberBank = bank.getResourceCount(Resource.LUMBER);
        int countBrickBank = bank.getResourceCount(Resource.BRICK);
        int countOreBank = bank.getResourceCount(Resource.ORE);

        int countLumber = current.getResourceCount(Resource.LUMBER);
        int countBrick = current.getResourceCount(Resource.BRICK);
        int countOre = current.getResourceCount(Resource.ORE);

        siedlerGame.throwDice(3);
        siedlerGame.throwDice(4);
        siedlerGame.throwDice(4);

        assertEquals(countLumber + 1, current.getResourceCount(Resource.LUMBER));
        assertEquals(countBrick + 2, current.getResourceCount(Resource.BRICK));
        assertEquals(countOre + 2, current.getResourceCount(Resource.ORE));

        assertEquals(countLumberBank - 1, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countBrickBank - 4, bank.getResourceCount(Resource.BRICK));
        assertEquals(countOreBank - 2, bank.getResourceCount(Resource.ORE));
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#throwDice(int)} method and checks that
     * the method works when the player has two cities at the field. The expected behaviour is that
     * the resource where removed by the bank and added by the player. This is a positive test.
     * The equivalence class is 13 and 14.
     */
    @Test
    public void receivePayoutFromLandTwoCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);

        assertTrue(model.buildRoad(new Point(9, 15), new Point(8, 16)));
        assertTrue(model.buildSettlement(new Point(8, 16)));
        assertTrue(model.buildCity(new Point(8, 16)));

        int countLumber = player1.getResourceCount(Resource.LUMBER);
        int countBrick = player1.getResourceCount(Resource.BRICK);
        int countOre = player1.getResourceCount(Resource.ORE);

        model.throwDice(3);
        countLumber += 2;
        model.throwDice(4);
        countBrick += 2;
        countOre++;
        model.throwDice(4);
        countOre++;
        assertEquals(countLumber, player1.getResourceCount(Resource.LUMBER));
        assertEquals(countBrick, player1.getResourceCount(Resource.BRICK));
        assertEquals(countOre, player1.getResourceCount(Resource.ORE));
    }

    /**
     * Tests the receivePayoutFromLand in {@link SiedlerGame#tradeWithBankFourToOne(Resource, Resource)} method and checks that
     * the method works when everybody has enough resources. The expected behaviour is that
     * the resource where removed by the bank and added by the player. This is a positive test.
     * The equivalence class is 8.
     */
    @Test
    public void tradeWithBankPositiv() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();

        int playerBrickStockBefore = model.getCurrentPlayerResourceStock(Resource.BRICK);
        int bankBrickStockBefore = bank.getResourceCount(Resource.BRICK);

        assertFalse(model.tradeWithBankFourToOne(Resource.LUMBER, Resource.WOOL));
        assertTrue(model.tradeWithBankFourToOne(Resource.BRICK, Resource.LUMBER));
        assertEquals(playerBrickStockBefore - 4, model.getCurrentPlayerResourceStock(Resource.BRICK));
        assertEquals(bankBrickStockBefore + 4, bank.getResourceCount(Config.Resource.BRICK));
    }

    /**
     * Tests the {@link SiedlerGame#tradeWithBankFourToOne(Resource, Resource)} method and checks that
     * the trade doesn't work without resources. The expected behaviour is that after getAfterSetupPhaseAlmostEmptyBank
     * The bank has no wool and can't trade. This is a negative test. The equivalence class is 09.
     */
    @Test
    public void bankHasNoResourceToTrade() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();

        assertEquals(0, bank.getResourceCount(Resource.WOOL));
        assertFalse(model.tradeWithBankFourToOne(Resource.LUMBER, Resource.WOOL));
        assertEquals(0, bank.getResourceCount(Resource.WOOL));
    }

    /**
     * Tests the {@link SiedlerGame#tradeWithBankFourToOne(Resource, Resource)} method and checks that
     * the trade doesn't work without resources. The expected behaviour is that after getAfterSetupPhaseAlmostEmptyBank
     * The player has only one resource. This is a negative test. The equivalence class is 09.
     */
    @Test
    public void playerHasNoResourceToTrade() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);

        assertEquals(1, player1.getResourceCount(Resource.WOOL));
        assertEquals(1, player1.getResourceCount(Resource.BRICK));
        assertFalse(model.tradeWithBankFourToOne(Resource.LUMBER, Resource.WOOL));
        assertEquals(1, player1.getResourceCount(Resource.WOOL));
        assertEquals(1, player1.getResourceCount(Resource.BRICK));
    }

    /**
     * Tests the {@link SiedlerGame#tradeWithBankFourToOne(Resource, Resource)} method and checks that
     * the trade works with resources. The expected behaviour is that after getAfterSetupPhaseAlmostEmptyBank
     * one brick has been removed and 4 lumbers added by the bank. This is a positive test. The equivalence class is 08.
     */
    @Test
    public void testBankResourceCountAfterTrade() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Bank bank = model.getBank();
        int countBrick = bank.getResourceCount(Resource.BRICK);
        int countLumber = bank.getResourceCount(Resource.LUMBER);

        assertTrue(model.tradeWithBankFourToOne(Resource.LUMBER, Resource.BRICK));
        assertEquals(countLumber + 4, bank.getResourceCount(Resource.LUMBER));
        assertEquals(countBrick - 1, bank.getResourceCount(Resource.BRICK));
    }

    /**
     * Tests the {@link SiedlerGame#tradeWithBankFourToOne(Resource, Resource)} method and checks that
     * the trade works with resources. The expected behaviour is that after getAfterSetupPhaseAlmostEmptyBank
     * one brick has been added and 4 lumbers removed by the player. This is a positive test. The equivalence class is 08.
     */
    @Test
    public void testPlayerResourceCountAfterTrade() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player = players.get(0);
        int countBrick = player.getResourceCount(Resource.BRICK);
        int countLumber = player.getResourceCount(Resource.LUMBER);

        assertTrue(model.tradeWithBankFourToOne(Resource.LUMBER, Resource.BRICK));
        assertEquals(countLumber - 4, player.getResourceCount(Resource.LUMBER));
        assertEquals(countBrick + 1, player.getResourceCount(Resource.BRICK));
    }

    /**
     * Tests the {@link SiedlerGame#getWinner()} method and checks that it doesn't work under the winPoints.
     * The expected behaviour is after getPlayerOneReadyToBuildFifthSettlement that the player1 hasn't enough victoryPoints to win.
     * This is a negative test. The equivalence class is 12.
     */
    @Test
    public void getWinnerUnderLimit() {
        SiedlerGame model = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(winPoints);

        assertNull(model.getWinner());

        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        player1.addVictoryPoints(1);

        assertEquals(winPoints - 2, player1.getVictoryPoints());
        assertNull(model.getWinner());
    }

    /**
     * Tests the {@link SiedlerGame#getWinner()} method and checks that it returns the right faction.
     * The expected behaviour is that after getPlayerOneReadyToBuildFifthSettlement the player1 wins with exactly the winPoints and with more.
     * This is a positive test. The equivalence class is 11.
     */
    @Test
    public void getWinnerOnAndOverLimit() {
        SiedlerGame model = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(winPoints);
        LinkedList<Player> players = model.getPlayers();
        assertNull(model.getWinner());
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        player1.addVictoryPoints(3);

        assertEquals(winPoints, player1.getVictoryPoints());
        assertEquals(model.getWinner(), player1.getFaction());
        assertNotEquals(model.getWinner(), player2.getFaction());
        assertNotEquals(model.getWinner(), player3.getFaction());

        player1.addVictoryPoints(3);

        assertEquals(winPoints + 3, player1.getVictoryPoints());
        assertEquals(model.getWinner(), player1.getFaction());
        assertNotEquals(model.getWinner(), player2.getFaction());
        assertNotEquals(model.getWinner(), player3.getFaction());
    }

    /**
     * Tests the {@link SiedlerGame#placeThiefAndStealCard(Point)} method and checks that it returns true if thief was successfully placed.
     * The expected behaviour is that after placing the thief, no one has a resource removed, thief can be placed.
     * This is a positive test. The equivalence class is 29.
     */
    @Test
    public void placeThiefAndStealCardTestOnEmptyField() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertTrue(model.placeThiefAndStealCard(new Point(5, 11)));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertEquals(model.getFieldOccupiedByThief(), new Point(5, 11));
        assertEquals(countResourcesBeforeP1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2, countResourcesAfterP2);
        assertEquals(countResourcesBeforeP3, countResourcesAfterP3);
    }

    /**
     * Tests the {@link SiedlerGame#placeThiefAndStealCard(Point)} method and checks that it returns true if thief was successfully placed.
     * The expected behaviour is that after placing the thief, a random player has a resource removed and the player who played the thief gains one resource.
     * This is a positive test. The equivalence class is 29.
     */
    @Test
    public void placeThiefAndStealCardTestOnFieldWithStructures() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        siedlerGame = model;
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        placeCity( new Point(9, 13), player3);

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertTrue(model.placeThiefAndStealCard(new Point(10, 14)));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertEquals(model.getFieldOccupiedByThief(), new Point(10, 14));
        assertEquals(countResourcesBeforeP1 + 1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2 + countResourcesBeforeP3 - 1, countResourcesAfterP2 + countResourcesAfterP3);
        assertEquals(countResourcesBeforeP1 + countResourcesBeforeP2 + countResourcesBeforeP3, countResourcesAfterP1 + countResourcesAfterP2 + countResourcesAfterP3);
    }

    /**
     * Tests the {@link SiedlerGame#placeThiefAndStealCard(Point)} method and checks that it returns true if thief was successfully placed.
     * The expected behaviour is that the thief can not be placed on the same field and the method returns false, count of resources over all players stays the same.
     * This is a negative test. The equivalence class is 30.
     */
    @Test
    public void placeThiefAndStealCardTestOnSameField() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        model.placeThiefAndStealCard(new Point(10, 14));

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertFalse(model.placeThiefAndStealCard(new Point(10, 14)));
        assertEquals(model.getFieldOccupiedByThief(), new Point(10, 14));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertEquals(countResourcesBeforeP1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2, countResourcesAfterP2);
        assertEquals(countResourcesBeforeP3, countResourcesAfterP3);
    }

    /**
     * Tests the {@link SiedlerGame#placeThiefAndStealCard(Point)} method and checks that it returns true if thief was successfully placed
     * The expected behaviour is that the thief can not be placed on water and the method returns false, count of resources over all players stays the same.
     * This is a negative test. The equivalence class is 30.
     */
    @Test
    public void placeThiefAndStealCardTestInOcean() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        Point fieldOccupiedByThief = model.getFieldOccupiedByThief();
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertEquals(fieldOccupiedByThief, Config.INITIAL_THIEF_POSITION);
        assertFalse(model.placeThiefAndStealCard(new Point(2, 2)));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertNotEquals(model.getFieldOccupiedByThief(), new Point(2, 2));
        assertEquals(countResourcesBeforeP1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2, countResourcesAfterP2);
        assertEquals(countResourcesBeforeP3, countResourcesAfterP3);
    }

    /**
     * Tests the card removal component in case of both potential victims not having any resources left and {@link SiedlerGame#placeThiefAndStealCard(Point)} checks that it returns true if thief was successfully placed.
     * The expected outcome is that the thief can be placed but no resource card is stolen.
     * This is a positive test. The equivalence class is 29.
     */
    @Test
    public void placeThiefAndStealCardNoCardRemovedIfBothVictimsHaveNone() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        LinkedList<Player> players = model.getPlayers();
        siedlerGame = model;
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);
        placeCity( new Point(9, 13), player3);
        for(Resource resource : Config.Resource.values()){
            player2.removeResource(resource, player2.getResourceCount(resource));
        }
        for(Resource resource : Config.Resource.values()){
            player3.removeResource(resource, player3.getResourceCount(resource));
        }

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertTrue(model.placeThiefAndStealCard(new Point(10,14)));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertEquals(model.getFieldOccupiedByThief(), new Point(10,14));
        assertEquals(countResourcesBeforeP1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2, countResourcesAfterP2);
        assertEquals(countResourcesBeforeP3, countResourcesAfterP3);
    }

    /**
     * Tests the card removal component in case one of the potential victims not having any resources left  {@link SiedlerGame#placeThiefAndStealCard(Point)} checks that it returns true if thief was successfully placed.
     * The expected outcome is that the thief can be placed and resource card is stolen from the one victim with resources available.
     * This is a positive test. The equivalence class is 29.
     */
    @Test
    public void placeThiefAndStealCardNoCardRemovedIfOneVictimHasNone() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(winPoints);
        siedlerGame = model;
        LinkedList<Player> players = model.getPlayers();
        Player player1 = players.get(0);
        Player player2 = players.get(1);
        Player player3 = players.get(2);

        placeCity( new Point(9, 13), player3);
        for(Resource resource : Config.Resource.values()){
            player2.removeResource(resource, player2.getResourceCount(resource));
        }

        int countResourcesBeforeP1 = player1.getTotalResourceCount();
        int countResourcesBeforeP2 = player2.getTotalResourceCount();
        int countResourcesBeforeP3 = player3.getTotalResourceCount();

        assertTrue(model.placeThiefAndStealCard(new Point(10,14)));

        int countResourcesAfterP1 = player1.getTotalResourceCount();
        int countResourcesAfterP2 = player2.getTotalResourceCount();
        int countResourcesAfterP3 = player3.getTotalResourceCount();

        assertEquals(model.getFieldOccupiedByThief(), new Point(10,14));
        assertEquals(countResourcesBeforeP1 + 1, countResourcesAfterP1);
        assertEquals(countResourcesBeforeP2, countResourcesAfterP2);
        assertEquals(countResourcesBeforeP3 - 1, countResourcesAfterP3);
    }
}