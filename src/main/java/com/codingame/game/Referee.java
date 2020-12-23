package com.codingame.game;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.endscreen.EndScreenModule;

import com.google.inject.Inject;

import connectXgame.Connect4Board;
import connectXgame.InvalidAction;
import connectXgame.RecentChipConnectionsDetails;

import connectXviewer.Viewer;


public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    @Inject private EndScreenModule endScreenModule;

    private static final int SCORE_WIN = 10;
    private static final int SCORE_LOSS = 0;
    private static final int SCORE_DRAW = 5;

    private Connect4Board connect4Board = new Connect4Board();
    private Viewer viewer = new Viewer();

    @Override
    public void init() {
        // Initialize your game here.

        gameManager.setMaxTurns(Connect4Board.NUM_ROWS * Connect4Board.NUM_COLS);  // num cells in the board

        // todo set max turn time (first and other turns) default = 50 ms
        gameManager.setFirstTurnMaxTime(1000);
        gameManager.setTurnMaxTime(100);

        // call initializer of connect 4 board
        connect4Board.initializeBoard();

        // draw initial entities on viewer
        viewer.setGraphicEntityModule(graphicEntityModule);  // attach the injected graphicEntityModule instance to the viewer instance
        Player p0 = gameManager.getPlayer(0);
        Player p1 = gameManager.getPlayer(1);
        viewer.drawInitialEntities(
                p0.getNicknameToken(), p0.getAvatarToken(), p0.getColorToken(),
                p1.getNicknameToken(), p1.getAvatarToken(), p1.getColorToken());
    }

    @Override
    public void gameTurn(int turn) {
        // note: turn is starting at 1. 200 turns => [1, 200]

        int curPlayerIndex = connect4Board.getTurnIndex() % Connect4Board.NUM_PLAYERS;
        int opponentIndex = curPlayerIndex == 0 ? 1 : 0;

        Player player = gameManager.getPlayer(curPlayerIndex);
        player.sendInputLine(connect4Board.getInputString());
        player.execute();

        // update turn index on viewer
        viewer.updateTurnIndex(connect4Board.getTurnIndex());

        try {
            final Action action = player.getAction();
            gameManager.addToGameSummary(String.format("Player %d(%s) chose columnIndex %d", curPlayerIndex, action.player.getNicknameToken(), action.col));

            connect4Board.playAction(action.col);

            // if reached this point => it was a valid action and is executed

            // show player's viewer message, and other player's text must disappear
            viewer.showPlayerViewerMessage(curPlayerIndex, action.player_given_viewer_message);

            // put player's chip and animate it
            int settlingRow = connect4Board.getRecentChipConnectionsDetails().getRecentCell().getRow();  // the bottom row in the chosen column in which the chip fell and settled
            viewer.putPlayerChipAndAnimateIt(player.getColorToken(), settlingRow, action.col);

            // if win, draw lines
            if (connect4Board.isGameOver()) {

                if (connect4Board.isGameDraw()) {
                    endTheGame("The game is a draw", 0xf9b700, true, true);  // if both won or lost, will be considered as draw by the endTheGame function

                } else {

                    // set tooltip
                    gameManager.addTooltip(player, player.getNicknameToken() + " Won!");

                    // the player won, draw connections
                    RecentChipConnectionsDetails chipConnectionsDetails = connect4Board.getRecentChipConnectionsDetails();  // note: only read from the var
                    viewer.drawWinConnectionLines(chipConnectionsDetails);

                    endTheGame(
                            String.format("Player-%d (%s) has won the game", curPlayerIndex, player.getNicknameToken()),
                            player.getColorToken(),
                            curPlayerIndex == 0,
                            curPlayerIndex == 1);
                }
            }

        } catch (TimeoutException e) {

            viewer.hideTheDownArrowForCurMove();

            gameManager.addToGameSummary(GameManager.formatErrorMessage(String.format("Player %d (%s): Timeout!", curPlayerIndex, player.getNicknameToken())));
            player.deactivate(player.getNicknameToken() + " timeout!");

            endTheGame(
                    String.format("Player-%d (%s) timeout", curPlayerIndex, player.getNicknameToken()),
                    player.getColorToken(),
                    opponentIndex == 0,
                    opponentIndex == 1);  // opponent wins the game

        } catch (InvalidAction invalidAction) {

            viewer.hideTheDownArrowForCurMove();

            String gameResultString;

            if (invalidAction.getActionType() == InvalidAction.ACTION_NOT_INTEGER_OR_OUT_OF_BOUNDS) {
                gameResultString = String.format("Player-%d (%s) wrong action \"%s\" (Require integer in range [0, %d])", curPlayerIndex, player.getNicknameToken(), invalidAction.getPlayerAction(), Connect4Board.NUM_COLS - 1);
            } else {  // filled column
                gameResultString = String.format("Player-%d (%s) wrong action \"%s\" (Already filled column)", curPlayerIndex, player.getNicknameToken(), invalidAction.getPlayerAction());
            }

            gameManager.addToGameSummary(GameManager.formatErrorMessage(String.format("Player %d (%s): Invalid action - %s", curPlayerIndex, player.getNicknameToken(), invalidAction.getMessage())));
            player.deactivate(invalidAction.getMessage());

            endTheGame(
                    gameResultString,
                    player.getColorToken(),
                    opponentIndex == 0,
                    opponentIndex == 1);  // opponent wins the game

        }
    }

    private void endTheGame(
            String gameResultViewerString, int gameResultStringColor,
            boolean p0Won, boolean p1Won) {

        viewer.setGameResultString(gameResultViewerString, gameResultStringColor);

        // set scores
        int p0Score, p1Score;
        if (p0Won && p1Won) {  // draw
            p0Score = SCORE_DRAW;
            p1Score = SCORE_DRAW;
        } else if (p0Won) {
            p0Score = SCORE_WIN;
            p1Score = SCORE_LOSS;
        } else if (p1Won) {
            p0Score = SCORE_LOSS;
            p1Score = SCORE_WIN;
        } else {  // also consider as a draw (not used though)
            p0Score = SCORE_DRAW;
            p1Score = SCORE_DRAW;
        }
        gameManager.getPlayer(0).setScore(p0Score);
        gameManager.getPlayer(1).setScore(p1Score);

        // call gameManager's endGame function
        gameManager.endGame();
    }

    @Override
    public void onEnd() {
        super.onEnd();
        int[] scores = {gameManager.getPlayer(0).getScore(), gameManager.getPlayer(1).getScore()};
        // String[] texts = {};
        endScreenModule.setScores(scores);
    }
}
