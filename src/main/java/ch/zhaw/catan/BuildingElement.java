package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.awt.*;

/**
 * This class defines start points of different structures.
 * This class is a superclass to Roads and Settlement.
 *
 * @author StackOverflow
 * @version 1.0
 */
public abstract class BuildingElement {
    private final Structure structureType;
    private final Point startPosition;
    private final Faction faction;

    /**
     * This method initializes a building element.
     *
     * @param position      represents the position of the element on the board.
     * @param structureType represents the structure type of the element.
     * @param faction       represents the faction of the element on the board.
     */
    public BuildingElement(Point position, Structure structureType, Faction faction) {
        this.structureType = structureType;
        this.faction = faction;
        startPosition = position;
    }

    public Point getPosition() {
        return startPosition;
    }

    public Structure getStructureType() {
        return structureType;
    }

    public Faction getFaction() {
        return faction;
    }
}