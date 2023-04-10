package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.awt.Point;
import java.util.*;

/**
 * This class performs all actions related to modifying the game state.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class SiedlerGame {
    static final int FOUR_TO_ONE_TRADE_OFFER = 4;
    static final int FOUR_TO_ONE_TRADE_WANT = 1;
    static final int THIEF_DICE_THROW = 7;
    private static final int MAX_NUMBER_OF_PLAYERS = Faction.values().length;
    private static final int MIN_WIN_POINTS = 3;
    private static final int POINTS_FOR_CITY = 2;
    private static final int POINTS_FOR_SETTLEMENT = 1;

    private final int winPoints;
    private final int numberOfPlayers;
    private final SiedlerBoard board;

    private final Bank bank;
    /**
     * This field represents the victim, of which the thief stole a {@link Resource}.
     */
    protected Player victim;
    /**
     * This field represents the {@link Resource} that has been stolen last by the thief.
     */
    protected Resource stolenResource;

    private Point fieldOccupiedByThief;

    private final LinkedList<Player> players;
    private int currentPlayerIndex;

    /**
     * Constructs a SiedlerGame game state object.
     *
     * @param winPoints       the number of points required to win the game
     * @param numberOfPlayers the number of players
     * @throws IllegalArgumentException if winPoints is lower than
     *                                  three or players is not between two and four
     */
    public SiedlerGame(int winPoints, int numberOfPlayers) {
        if (numberOfPlayers < Config.MIN_NUMBER_OF_PLAYERS || numberOfPlayers > MAX_NUMBER_OF_PLAYERS) {
            throw new IllegalArgumentException();
        }
        if (winPoints < MIN_WIN_POINTS) {
            throw new IllegalArgumentException();
        }
        this.winPoints = winPoints;
        this.numberOfPlayers = numberOfPlayers;
        board = new SiedlerBoard();
        players = new LinkedList<>();
        bank = new Bank();
        fieldOccupiedByThief = Config.INITIAL_THIEF_POSITION;

        for (int i = 0; i < numberOfPlayers; i++) {
            Player player = new Player(Faction.values()[i]);
            players.add(player);
        }
    }

    /**
     * Switches to the next player in the defined sequence of players.
     */
    public void switchToNextPlayer() {
        if (currentPlayerIndex == numberOfPlayers - 1) {
            currentPlayerIndex = 0;
        } else {
            currentPlayerIndex++;
        }
    }

    /**
     * Switches to the previous player in the defined sequence of players.
     */
    public void switchToPreviousPlayer() {
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = numberOfPlayers - 1;
        } else {
            currentPlayerIndex--;
        }
    }

    /**
     * Returns the {@link Faction}s of the active players.
     *
     * <p>The order of the player's factions in the list must
     * correspond to the oder in which they play.
     * Hence, the player that sets the first settlement must be
     * at position 0 in the list etc.
     * </p><p>
     * <strong>Important note:</strong> The list must contain the
     * factions of active players only.</p>
     *
     * @return the list with player's factions
     */
    public List<Faction> getPlayerFactions() {
        List<Faction> factions = new LinkedList<>();
        for (Player p : players) {
            factions.add(p.getFaction());
        }
        return factions;
    }

    /**
     * Returns the game board.
     *
     * @return the game board
     */
    public SiedlerBoard getBoard() {
        return board;
    }

    /**
     * Returns the {@link Faction} of the current player.
     *
     * @return the faction of the current player
     */
    public Faction getCurrentPlayerFaction() {
        return players.get(currentPlayerIndex).getFaction();
    }

    /**
     * Returns how many resource cards of the specified type
     * the current player owns.
     *
     * @param resource the resource type
     * @return the number of resource cards of this type
     */
    public int getCurrentPlayerResourceStock(Resource resource) {
        Player player = players.get(currentPlayerIndex);
        return player.getResourceCount(resource);
    }

    /**
     * Places a settlement in the founder's phase (phase II) of the game.
     *
     * <p>The placement does not cost any resource cards. If payout is
     * set to true, for each adjacent resource-producing field, a resource card of the
     * type of the resource produced by the field is taken from the bank (if available) and added to
     * the players' stock of resource cards.</p>
     *
     * @param position the position of the settlement
     * @param payout   if true, the player gets one resource card per adjacent resource-producing field
     * @return true, if the placement was successful
     */
    public boolean placeInitialSettlement(Point position, boolean payout) {
        if (position == null) {
            return false;
        }
        if (initialSettlementCanBeBuilt(position)) {
            board.setCorner(position, getCurrentPlayerFaction().toString());
            players.get(currentPlayerIndex).removeFromAvailableStructure(Structure.SETTLEMENT);
            board.addAllBuildingElementOnBoard(new Settlement(position, Structure.SETTLEMENT, getCurrentPlayerFaction()));
            players.get(currentPlayerIndex).addVictoryPoints(POINTS_FOR_SETTLEMENT);

            if (payout) {
                List<Land> landsForCorner = board.getFields(position);
                for (Land land : landsForCorner) {
                    Resource resource = land.getResource();
                    if (resource != null) {
                        players.get(currentPlayerIndex).addResource(resource, 1);
                        bank.removePayoutForStructure(resource, Structure.SETTLEMENT);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Places a road in the founder's phase (phase II) of the game.
     * The placement does not cost any resource cards.
     *
     * @param roadStart position of the start of the road
     * @param roadEnd   position of the end of the road
     * @return true, if the placement was successful
     */
    public boolean placeInitialRoad(Point roadStart, Point roadEnd) {
        if (roadStart == null || roadEnd == null) {
            return false;
        }
        if (roadCanBeBuilt(roadStart, roadEnd)) {
            List<BuildingElement> allSettlements = board.getAllBuildingElementOnBoard(getCurrentPlayerFaction(), Structure.SETTLEMENT);
            for (BuildingElement settlement : allSettlements) {
                Point position = settlement.getPosition();
                boolean roadStartsAtSettlement = roadStart.equals(position);
                boolean roadEndsAtSettlement = roadEnd.equals(position);
                if (board.getAdjacentEdges(position).isEmpty() && !roadStartsAtSettlement && !roadEndsAtSettlement) {
                    return false;
                }
            }
            board.setEdge(roadStart, roadEnd, getCurrentPlayerFaction().toString());
            board.addAllBuildingElementOnBoard(new Road(roadStart, getCurrentPlayerFaction()));
            players.get(currentPlayerIndex).removeFromAvailableStructure(Structure.ROAD);
            return true;
        }
        return false;
    }

    /**
     * Builds a settlement at the specified position on the board.
     *
     * <p>The settlement can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a settlement to place on the board</li>
     * <li> the specified position meets the build rules for settlements</li>
     * </ul>
     *
     * @param position the position of the settlement
     * @return true, if the placement was successful
     */
    public boolean buildSettlement(Point position) {
        Player player = players.get(currentPlayerIndex);
        if (position == null) {
            return false;
        }

        if (settlementCanBeBuilt(position) && player.hasEnoughResourcesToBuild(Structure.SETTLEMENT) && player.getRemainingStructure(Structure.SETTLEMENT) >= 1) {

            player.removeResourcesForStructure(Structure.SETTLEMENT);
            board.setCorner(position, getCurrentPlayerFaction().toString());
            bank.addResourcesForStructure(Structure.SETTLEMENT);

            player.removeFromAvailableStructure(Structure.SETTLEMENT);

            board.addAllBuildingElementOnBoard(new Settlement(position, Structure.SETTLEMENT, player.getFaction()));

            player.addVictoryPoints(POINTS_FOR_SETTLEMENT);
            return true;
        }
        return false;
    }

    /**
     * Builds a city at the specified position on the board.
     *
     * <p>The city can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a city to place on the board</li>
     * <li> the specified position meets the build rules for cities</li>
     * </ul>
     *
     * @param position the position of the city
     * @return true, if the placement was successful
     */
    public boolean buildCity(Point position) {
        Player player = players.get(currentPlayerIndex);
        if (position == null) {
            return false;
        }

        if (cityCanBeBuilt(position) && player.hasEnoughResourcesToBuild(Structure.CITY) && player.getRemainingStructure(Structure.CITY) >= 1) {

            player.removeResourcesForStructure(Structure.CITY);
            board.setCorner(position, getCurrentPlayerFaction().toString().toUpperCase());
            bank.addResourcesForStructure(Structure.CITY);

            player.addToAvailableStructure(Structure.SETTLEMENT);
            player.removeFromAvailableStructure(Structure.CITY);
            BuildingElement buildingElement = board.getBuildingElement(position, Structure.SETTLEMENT);
            if (buildingElement == null) {
                return false;
            }
            board.removeAllBuildingElementOnBoard(buildingElement);

            board.addAllBuildingElementOnBoard(new City(position, player.getFaction()));

            player.removeVictoryPoints(POINTS_FOR_SETTLEMENT);
            player.addVictoryPoints(POINTS_FOR_CITY);

            return true;
        }
        return false;
    }

    /**
     * Builds a road at the specified position on the board.
     *
     * <p>The road can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a road to place on the board</li>
     * <li> the specified position meets the build rules for roads</li>
     * </ul>
     *
     * @param roadStart the position of the start of the road
     * @param roadEnd   the position of the end of the road
     * @return true, if the placement was successful
     */

    public boolean buildRoad(Point roadStart, Point roadEnd) {
        Player player = players.get(currentPlayerIndex);
        if (roadStart == null || roadEnd == null) {
            return false;
        }
        if (roadCanBeBuilt(roadStart, roadEnd) && player.hasEnoughResourcesToBuild(Structure.ROAD) && player.getRemainingStructure(Structure.ROAD) >= 1) {

            player.removeResourcesForStructure(Structure.ROAD);
            board.setEdge(roadStart, roadEnd, getCurrentPlayerFaction().toString());
            bank.addResourcesForStructure(Structure.ROAD);

            player.removeFromAvailableStructure(Structure.ROAD);

            board.addAllBuildingElementOnBoard(new Road(roadStart, player.getFaction()));

            return true;
        }
        return false;
    }

    /**
     * <p>Trades in {@link #FOUR_TO_ONE_TRADE_OFFER} resource cards of the
     * offered type for {@link #FOUR_TO_ONE_TRADE_WANT} resource cards of the wanted type.
     * </p><p>
     * The trade only works when bank and player possess the resource cards
     * for the trade before the trade is executed.
     * </p>
     *
     * @param offer offered type
     * @param want  wanted type
     * @return true, if the trade was successful
     */
    public boolean tradeWithBankFourToOne(Resource offer, Resource want) {
        Player player = players.get(currentPlayerIndex);
        int amount = player.getResourceCount(offer);
        if (amount >= FOUR_TO_ONE_TRADE_OFFER) {
            player.removeResource(offer, FOUR_TO_ONE_TRADE_OFFER);
            player.addResource(want, FOUR_TO_ONE_TRADE_WANT);
            bank.addResource(offer, FOUR_TO_ONE_TRADE_OFFER);
            return bank.removeResource(want, FOUR_TO_ONE_TRADE_WANT);
        }
        return false;
    }

    /**
     * Returns the winner of the game, if any.
     *
     * @return The winner of the game as type Faction or NULL, if the win points have not been reached yet.
     */
    public Faction getWinner() {
        Player player = players.get(currentPlayerIndex);
        int victoryPoints = player.getVictoryPoints();
        if (victoryPoints >= winPoints) {
            return player.getFaction();
        }

        return null;
    }

    /**
     * This method takes care of actions depending on the dice throw result.
     * <p>
     * A key action is the payout of the resource cards to the players
     * according to the payout rules of the game. This includes the
     * "negative payout" in case a 7 is thrown and a player has more than
     * {@link Config#MAX_CARDS_IN_HAND_NO_DROP} resource cards.
     * </p><p>
     * If a player does not get resource cards, the list for this players'
     * {@link Faction} is <b>an empty list (not null)</b>!.
     * </p><p>
     * The payout rules of the game take into account factors such as, the number
     * of resource cards currently available in the bank, settlement types
     * (settlement or city), and the number of players that should get resource
     * cards of a certain type (relevant if there are not enough left in the bank).
     * </p>
     *
     * @param diceThrow the resource cards that have been distributed to the players
     * @return the resource cards added to the stock of the different players
     */
    public Map<Faction, List<Resource>> throwDice(int diceThrow) {
        Map<Faction, List<Resource>> result = new HashMap<>();

        for (Player player : players) {
            List<Resource> resources = new ArrayList<>();
            result.put(player.getFaction(), resources);
        }

        if (diceThrow == THIEF_DICE_THROW) {
            result = stealCardsFromPlayers();
        } else {
            result = receivePayoutFromLand(diceThrow);
        }
        return result;
    }

    /**
     * Places the thief on the specified field and steals a random resource card (if
     * the player has such cards) from a random player with a settlement at that
     * field (if there is a settlement) and adds it to the resource cards of the
     * current player.
     *
     * @param field the field on which to place the thief
     * @return false, if the specified field is not a field or the thief cannot be
     * placed there (e.g., on water)
     */
    public boolean placeThiefAndStealCard(Point field) {
        if (!board.hasField(field) || board.getField(field) == Land.WATER || fieldOccupiedByThief.equals(field)) {
            return false;
        }

        fieldOccupiedByThief = field;

        List<Player> candidates = new ArrayList<>();

        for (String corner : board.getCornersOfField(field)) {
            for (Faction f : Faction.values()) {

                if (getCurrentPlayerFaction().equals(f)) {
                    continue;
                }
                if (f.toString().equalsIgnoreCase(corner)) {
                    Player player = getPlayerFromFaction(f.toString());
                    candidates.add(player);
                }
            }
        }

        for (int i = 0; i < candidates.size(); i++) {
            if (!candidates.get(i).hasResources()) {
                candidates.remove(i);
                i--;
            }
        }

        if (candidates.isEmpty()) {
            stolenResource = null;
            return true;
        }
        Player current = players.get(currentPlayerIndex);

        victim = candidates.get(new Random().nextInt(candidates.size()));

        List<Resource> resources = new ArrayList<>(victim.getAvailableResources().keySet());
        stolenResource = resources.get(new Random().nextInt(resources.size()));

        victim.removeResource(stolenResource, 1);
        current.addResource(stolenResource, 1);
        return true;
    }

    private Player getPlayerFromFaction(String factionString) {
        Player player = null;
        for (Player p : players) {
            if (p.getFaction().toString().equalsIgnoreCase(factionString)) {
                player = p;
            }
        }
        if (player == null) {
            throw new IllegalArgumentException("No player with faction " + factionString + " found.");
        }
        return player;
    }

    private boolean roadCanBeBuilt(Point roadStart, Point roadEnd) {
        if (!board.hasEdge(roadStart, roadEnd)) {
            return false;
        }
        boolean isAFreeRoad = board.getEdge(roadStart, roadEnd) == null;
        boolean isNextToAnOwnBuilding = getCurrentPlayerFaction().toString().equalsIgnoreCase(board.getCorner(roadStart)) || getCurrentPlayerFaction().toString().equalsIgnoreCase(board.getCorner(roadEnd));
        boolean isNextToAnOwnRoad = board.getAdjacentEdges(roadStart).contains(getCurrentPlayerFaction().toString()) || board.getAdjacentEdges(roadEnd).contains(getCurrentPlayerFaction().toString());
        boolean roadIsBetweenWater = isCornerOnlyNextToWater(roadStart) || isCornerOnlyNextToWater(roadEnd);

        return isAFreeRoad && (isNextToAnOwnRoad || isNextToAnOwnBuilding) && !roadIsBetweenWater;
    }

    private boolean cityCanBeBuilt(Point position) {
        if (!board.hasCorner(position)) {
            return false;
        }

        return getCurrentPlayerFaction().toString().equals(board.getCorner(position));
    }

    private boolean settlementCanBeBuilt(Point location) {
        if (!board.hasCorner(location)) {
            return false;
        }

        boolean cornerIsEmpty = board.getCorner(location) == null;
        boolean cornerHasNoNeighbour = board.getNeighboursOfCorner(location).isEmpty();
        boolean cornerIsOnAnOwnStreet = board.getAdjacentEdges(location).contains(getCurrentPlayerFaction().toString());

        return cornerIsEmpty && cornerHasNoNeighbour && cornerIsOnAnOwnStreet && !isCornerOnlyNextToWater(location);
    }

    private boolean initialSettlementCanBeBuilt(Point location) {
        if (!board.hasCorner(location)) {
            return false;
        }
        boolean cornerIsEmpty = board.getCorner(location) == null;
        boolean cornerHasNoNeighbour = board.getNeighboursOfCorner(location).isEmpty();

        return cornerIsEmpty && cornerHasNoNeighbour && !isCornerOnlyNextToWater(location);
    }

    private boolean isCornerOnlyNextToWater(Point location) {
        boolean cornerIsOnlyNextToWater = true;
        List<Land> landsAdjacentToCorner = board.getFields(location);
        for (Land land : landsAdjacentToCorner) {
            if (land != Land.WATER) {
                cornerIsOnlyNextToWater = false;
                break;
            }
        }
        return cornerIsOnlyNextToWater;
    }

    private Map<Faction, List<Resource>> stealCardsFromPlayers() {
        Map<Faction, List<Resource>> result = new HashMap<>();
        for (Player player : players) {
            List<Resource> resources = new ArrayList<>();
            if (player.getTotalResourceCount() > Config.MAX_CARDS_IN_HAND_NO_DROP) {
                List<Resource> stolen = stealHalfResourcesFromPlayer(player);
                resources.addAll(stolen);
            }
            result.put(player.getFaction(), resources);
        }
        return result;
    }

    private List<Resource> stealHalfResourcesFromPlayer(Player player) {

        List<Resource> inventory = new ArrayList<>();

        Map<Resource, Integer> inventoryMap = player.getAvailableResources();
        for (Resource resource : inventoryMap.keySet()) {
            for (int i = 0; i < inventoryMap.get(resource); i++) {
                inventory.add(resource);
            }
        }

        Collections.shuffle(inventory);

        List<Resource> stolen = new ArrayList<>();

        for (int i = 0; i < Math.floor(inventory.size() / 2.0); i++) {
            Resource resource = inventory.get(i);
            stolen.add(resource);
            player.removeResource(resource, 1);
            bank.addResource(resource, 1);
        }

        return stolen;
    }

    private Map<Faction, List<Resource>> receivePayoutFromLand(int diceThrow) {
        int payoutAmountForCity = 2;
        int payoutAmountForSettlement = 1;
        Map<Faction, List<Resource>> gained = new HashMap<>();
        List<Point> fields = board.getFieldsForDiceValue(diceThrow);

        for (Point field : fields) {
            if (field.equals(fieldOccupiedByThief)) {
                continue;
            }
            Land land = board.getField(field);

            for (Player player : players) {
                List<Resource> resources = new ArrayList<>();

                for (String corner : board.getCornersOfField(field)) {
                    if (corner.equalsIgnoreCase(player.getFaction().toString())) {
                        if (corner.equals(player.getFaction().toString().toUpperCase())) {
                            if (bank.removePayoutForStructure(land.getResource(), Structure.CITY)) {
                                player.addResource(land.getResource(), payoutAmountForCity);
                                resources.add(land.getResource());
                                resources.add(land.getResource());
                            }
                        } else if (bank.removePayoutForStructure(land.getResource(), Structure.SETTLEMENT)) {
                            player.addResource(land.getResource(), payoutAmountForSettlement);
                            resources.add(land.getResource());
                        }
                    }
                }

                if (gained.containsKey(player.getFaction())) {
                    gained.get(player.getFaction()).addAll(resources);
                } else {
                    gained.put(player.getFaction(), resources);
                }
            }
        }
        return gained;
    }

    /**
     * Only used in testing.
     *
     * @return bank.
     */
    Bank getBank() {
        return bank;
    }

    /**
     * Only used in testing.
     *
     * @return fieldOccupiedByThief.
     */
    Point getFieldOccupiedByThief() {
        return fieldOccupiedByThief;
    }

    /**
     * Only used in testing.
     *
     * @return players.
     */
    LinkedList<Player> getPlayers() {
        return players;
    }

    /**
     * Only used in testing.
     *
     * @return Current player.
     */
    Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
}