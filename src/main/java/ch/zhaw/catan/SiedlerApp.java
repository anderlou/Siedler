package ch.zhaw.catan;

import ch.zhaw.utils.Helper;
import ch.zhaw.catan.Config.*;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class acts the App of the whole Game, it contains the main method and with it, is the class in which the whole gameplay happens.
 *
 * @author StackOverflow
 * @version 1.0
 */
public class SiedlerApp {

    private final UserInterface ui;
    private final SiedlerGame game;
    private final SiedlerBoardTextView view;
    private final int numberOfPlayers;
    private static final int WIN_POINTS = 7;

    /**
     * Creates a new siedler game and runs it.
     *
     * @param args an array of command-line arguments for the application.
     */
    public static void main(String[] args) {
        SiedlerApp app = new SiedlerApp();
        app.run();
    }

    /**
     * This constructor initializes the {@link SiedlerApp}.
     */
    public SiedlerApp() {
        ui = new UserInterface();
        ui.printWelcomeText();
        numberOfPlayers = ui.getNumberOfPlayers();
        game = new SiedlerGame(WIN_POINTS, numberOfPlayers);
        view = new SiedlerBoardTextView(game.getBoard());
    }

    /**
     * Starts the Siedler game.
     */
    private void run() {
        ui.printBoard(view);

        for (int i = 0; i < numberOfPlayers; i++) {
            placeInitialStructures(false);
            game.switchToNextPlayer();
        }

        for (int i = numberOfPlayers - 1; i >= 0; i--) {
            game.switchToPreviousPlayer();
            placeInitialStructures(true);
        }

        while (true) {
            ui.announceCurrentPlayer(game.getCurrentPlayerFaction());
            int dice = Helper.generateDiceThrow();
            ui.announceDice(dice);

            Map<Faction, List<Resource>> result = game.throwDice(dice);

            if (dice == SiedlerGame.THIEF_DICE_THROW) {
                ui.announceThiefStoleResources(result);

                Point thiefPoint = ui.chooseThiefPlacement();
                while (!game.placeThiefAndStealCard(thiefPoint)) {
                    ui.announceThiefPlacementFailed();
                    thiefPoint = ui.chooseThiefPlacement();
                }

                if (game.stolenResource == null) {
                    ui.announceNothingGotStolen();
                } else {
                    ui.announcePlacedThiefStoleResources(game.victim.getFaction(), game.stolenResource.toString());
                }
            } else {
                ui.announceResourcesGained(result);
            }

            boolean isMoveOver = false;

            while (!isMoveOver) {
                Action action = ui.getAction();

                switch (action) {
                    case BUILD -> buildStructure();
                    case TRADE -> {
                        Resource toTrade = ui.chooseResourceToTrade();
                        Resource toGet = ui.chooseResourceToGet();
                        if (!game.tradeWithBankFourToOne(toTrade, toGet)) {
                            ui.announceTradingFailed();
                        }
                    }
                    case PRINT_BOARD -> ui.printBoard(view);
                    case PRINT_INVENTORY -> {
                        Map<Resource, Integer> inventory = new HashMap<>();
                        for (Resource resource : Resource.values()) {
                            int count = game.getCurrentPlayerResourceStock(resource);
                            inventory.put(resource, count);
                        }
                        ui.printInfo(inventory);
                    }
                    case FINISH_MOVE -> isMoveOver = true;
                }
            }

            if (game.getWinner() != null) {
                ui.declareWinner(game.getWinner());
                if (ui.exitRequest()) {
                    ui.endProgram();
                }
                break;
            }

            game.switchToNextPlayer();
        }
    }

    private void placeInitialStructures(boolean secondPhase) {
        ui.announceCurrentPlayer(game.getCurrentPlayerFaction());

        boolean successful = false;

        while (!successful) {
            Point settlementPoint = ui.chooseSettlementPlacement();
            successful = game.placeInitialSettlement(settlementPoint, secondPhase);
            if (!successful) {
                ui.announceBuildingFailed(Structure.SETTLEMENT);
            }
        }

        successful = false;

        while (!successful) {
            Point[] roadPoints = ui.chooseRoadPlacement();
            successful = game.placeInitialRoad(roadPoints[0], roadPoints[1]);
            if (!successful) {
                ui.announceBuildingFailed(Structure.ROAD);
            }
        }
    }

    private void buildStructure() {
        Structure structure = ui.chooseStructure();

        switch (structure) {
            case ROAD -> {
                Point[] points = ui.chooseRoadPlacement();
                if (!game.buildRoad(points[0], points[1])) {
                    ui.announceBuildingFailed(Structure.ROAD);
                }
            }
            case SETTLEMENT -> {
                if (!game.buildSettlement(ui.chooseSettlementPlacement())) {
                    ui.announceBuildingFailed(Structure.SETTLEMENT);
                }
            }
            case CITY -> {
                if (!game.buildCity(ui.chooseSettlementToUpgrade())) {
                    ui.announceBuildingFailed(Structure.CITY);
                }
            }
        }
    }
}