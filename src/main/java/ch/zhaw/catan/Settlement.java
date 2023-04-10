package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.awt.*;

/**
 * This class defines the settlements of the game.
 * It is a subclass to BuildingElement.
 * It is a superclass to City.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class Settlement extends BuildingElement {

    /**
     * This constructor initializes a settlement and its superclass {@link BuildingElement}.
     *
     * @param position  the position of the settlement.
     * @param structure the structure of the settlement.
     * @param faction   the faction of the settlement.
     */
    public Settlement(Point position, Structure structure, Faction faction) {
        super(position, structure, faction);
    }
}