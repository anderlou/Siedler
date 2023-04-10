package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.util.*;

/**
 * This class defines players of the game, manages their victory points, faction and stock.
 *
 * @author Stackoverflow
 * @version 1.0
 */
public class Player extends Participant {
    private final Faction faction;
    private final Map<Structure, Integer> availableStructures = new HashMap<>();
    private int victoryPoints;

    /**
     * This constructor initializes the player. It sets the resources Map from its super class, the faction of the player
     * and the stock of all available structures.
     *
     * @param faction the faction of the player.
     */
    public Player(Faction faction) {
        super(new HashMap<>(Map.of(
                Resource.LUMBER, 0,
                Resource.BRICK, 0,
                Resource.WOOL, 0,
                Resource.GRAIN, 0,
                Resource.ORE, 0)
        ));

        this.faction = faction;
        victoryPoints = 0;
        availableStructures.put(Structure.CITY, Structure.CITY.getStockPerPlayer());
        availableStructures.put(Structure.ROAD, Structure.ROAD.getStockPerPlayer());
        availableStructures.put(Structure.SETTLEMENT, Structure.SETTLEMENT.getStockPerPlayer());
    }

    /**
     * This function adds a structure back to the stock of the player.
     *
     * @param newStructure defines the type of structure given.
     */
    public void removeFromAvailableStructure(Structure newStructure) {
        int current = availableStructures.get(newStructure);
        availableStructures.put(newStructure, ++current);
    }

    /**
     * This function removes a structure from the stock of the player.
     *
     * @param structureToBeRemoved defines the type of structure given.
     */
    public void addToAvailableStructure(Structure structureToBeRemoved) {
        int current = availableStructures.get(structureToBeRemoved);
        availableStructures.put(structureToBeRemoved, --current);
    }

    /**
     * Gets the players remaining structure count.
     *
     * @param structure Structure which is being looked for in players stock.
     * @return The amount of structures left.
     */
    public int getRemainingStructure(Structure structure) {
        return availableStructures.get(structure);
    }

    /**
     * Checks if player has enough resources to build a certain structure.
     *
     * @param structure is the structure which is being checked.
     * @return returns true if enough resources are available, false if there are not enough resources available.
     */
    public boolean hasEnoughResourcesToBuild(Structure structure) {
        Map<Resource, Long> costs = structure.getCostsAsMap();
        for (Resource resource : costs.keySet()) {
            if (resources.get(resource) < costs.get(resource)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes all resources needed for building a certain structure.
     *
     * @param structure Structure to be built.
     */
    public void removeResourcesForStructure(Structure structure) {
        List<Resource> cost = structure.getCosts();
        for (Resource resource : cost) {
            removeResource(resource, 1);
        }
    }

    /**
     * This method writes all available resources the player has into a map.
     *
     * @return returns all the resources that the player has.
     */
    public Map<Resource, Integer> getAvailableResources() {
        Map<Resource, Integer> availableResources = new HashMap<>();
        for (Resource resource : Resource.values()) {
            if (getResourceCount(resource) > 0) {
                availableResources.put(resource, resources.get(resource));
            }
        }
        return availableResources;
    }

    /**
     * This function adds a certain amount of victory points.
     *
     * @param count defines amount of victory points given.
     */
    public void addVictoryPoints(int count) {
        victoryPoints += count;
    }

    /**
     * This function removes a certain amount of victory points.
     *
     * @param count defines amount of victory points remove.
     */
    public void removeVictoryPoints(int count) {
        victoryPoints -= count;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public Faction getFaction() {
        return faction;
    }
}