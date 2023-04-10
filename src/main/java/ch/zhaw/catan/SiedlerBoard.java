package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;
import ch.zhaw.hexboard.HexBoard;
import ch.zhaw.hexboard.Label;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class defines the game board which the game plays on.
 * It is a subclass to HexBoard.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class SiedlerBoard extends HexBoard<Land, String, String, String> {

    private final Map<Point, Label> labelMap;
    private final List<BuildingElement> allBuildingElementOnBoard = new ArrayList<>();

    /**
     * This constructor initializes the SiedlerBoard and its labelMap.
     */
    public SiedlerBoard() {
        labelMap = new HashMap<>();

        for (Point point : Config.getStandardLandPlacement().keySet()) {
            this.addField(point, Config.getStandardLandPlacement().get(point));
        }

        for (Map.Entry<Point, Integer> e : Config.getStandardDiceNumberPlacement().entrySet()) {
            Label label;
            if (e.getValue() < 10) {
                label = new Label('0', Character.forDigit(e.getValue(), 10));
            } else {
                label = new Label('1', Character.forDigit(e.getValue() - 10, 10));
            }
            labelMap.put(e.getKey(), label);
        }
    }

    /**
     * Returns the map of points and labels.
     *
     * @return A map of points and labels.
     */
    public Map<Point, Label> getLabelMap() {
        return labelMap;
    }

    /**
     * This method returns all building elements of the desired faction and structure.
     *
     * @param faction   the desired faction of the building element.
     * @param structure the desired structure of the building element.
     * @return a list with all the desired building elements.
     */
    public List<BuildingElement> getAllBuildingElementOnBoard(Faction faction, Structure structure) {
        List<BuildingElement> listOfElements = new ArrayList<>();
        for (BuildingElement element : allBuildingElementOnBoard) {
            if (element.getStructureType().equals(structure) && element.getFaction().equals(faction)) {
                listOfElements.add(element);
            }
        }
        return listOfElements;
    }

    /**
     * This method evaluates the number of building elements of a desired faction and structure.
     *
     * @param faction   the desired faction of the building elements.
     * @param structure the desired structure of the building elements.
     * @return the number of building elements.
     */
    public int receiveAllBuildingElementOnBoard(Faction faction, Structure structure) {
        return getAllBuildingElementOnBoard(faction, structure).size();
    }

    /**
     * Adds all Building elements to board.
     *
     * @param element The element added to the board.
     */
    public void addAllBuildingElementOnBoard(BuildingElement element) {
        allBuildingElementOnBoard.add(element);
    }

    /**
     * This method removes one buildingElement from the List {@link SiedlerBoard#allBuildingElementOnBoard}.
     *
     * @param element the building element that should be removed.
     */
    public void removeAllBuildingElementOnBoard(BuildingElement element) {
        allBuildingElementOnBoard.remove(element);
    }

    /**
     * This method checks if there is a building element in the {@link SiedlerBoard#allBuildingElementOnBoard} list with
     * the given position and structure and returns it. If no such element is found, the method returns null.
     *
     * @param position  the desired position of the building element.
     * @param structure the desired structure of the building element.
     * @return the desired building element if found, otherwise null.
     */
    public BuildingElement getBuildingElement(Point position, Structure structure) {
        for (BuildingElement element : allBuildingElementOnBoard) {
            if (element.getPosition().equals(position) && element.getStructureType().equals(structure)) {
                return element;
            }
        }
        return null;
    }

    /**
     * Returns the fields associated with the specified dice value.
     *
     * @param dice The dice value, from 2 to 12.
     * @return The fields associated with the dice value
     */
    public List<Point> getFieldsForDiceValue(int dice) {
        int minDiceValue = 2;
        int maxDiceValue = 12;
        if (dice < minDiceValue || dice > maxDiceValue) {
            throw new IllegalArgumentException("Dice value may not be less than 2 or greater than 12! Given dice value: " + dice);
        }

        List<Point> fields = new ArrayList<>();

        for (Point point : labelMap.keySet()) {
            Label label = labelMap.get(point);

            int labelValue = Character.getNumericValue(label.getFirst()) * 10
                    + Character.getNumericValue(label.getSecond());

            if (labelValue == dice) {
                fields.add(point);
            }
        }
        return fields;
    }
}