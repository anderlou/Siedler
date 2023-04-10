package ch.zhaw.catan;

import ch.zhaw.catan.Config.*;

import java.awt.*;

/**
 * This class defines the cities of the game.
 * It is a subclass to Settlement.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class City extends Settlement {

    /**
     * This constructor initializes a city.
     *
     * @param position the position where the city is placed.
     * @param faction  the faction of the city.
     */
    public City(Point position, Faction faction) {
        super(position, Structure.CITY, faction);
    }
}