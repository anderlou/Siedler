package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.awt.*;

/**
 * This class defines the roads of the game.
 * It is a subclass to BuildingElement.
 *
 * @author StackOverflow
 * @version 1.0
 */

public class Road extends BuildingElement {

    /**
     * This constructor initializes a road and its superclass {@link BuildingElement}.
     *
     * @param startPosition the starting position of a road.
     * @param faction       the faction of the road.
     */
    public Road(Point startPosition, Faction faction) {
        super(startPosition, Structure.ROAD, faction);
    }
}