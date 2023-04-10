package ch.zhaw.catan;

/**
 * {@link Enum} that defines which type of action is being taken during the gameplay.
 *
 * @author StackOverflow
 * @version 1.0
 */
public enum Action {
    /**
     * Indicates that the player wants to place or upgrade a building.
     */
    BUILD,
    /**
     * Indicates that the player wants to trade with the bank.
     */
    TRADE,
    /**
     * Indicates that the player wants to print the current game state.
     */
    PRINT_BOARD,
    /**
     * Indicates that the player wants to print his current inventory.
     */
    PRINT_INVENTORY,
    /**
     * Indicates that the player wants to end his turn.
     */
    FINISH_MOVE
}