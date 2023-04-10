package ch.zhaw.catan;

import ch.zhaw.catan.Config.Land;
import ch.zhaw.hexboard.HexBoardTextView;
import ch.zhaw.hexboard.Label;

import java.awt.*;
import java.util.Map;

/**
 * This class defines the textview to our Siedler Game.
 * It is a subclass to HexBoardTextView.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class SiedlerBoardTextView extends HexBoardTextView<Land, String, String, String> {

    /**
     * This constructor initializes the text view of the {@link SiedlerBoard}.
     *
     * @param board the board of which you'd like to create the text view.
     */
    public SiedlerBoardTextView(SiedlerBoard board) {
        super(board);

        for (Map.Entry<Point, Label> e : board.getLabelMap().entrySet()) {
            this.setLowerFieldLabel(e.getKey(), e.getValue());
        }
    }
}