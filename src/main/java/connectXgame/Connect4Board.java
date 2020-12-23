package connectXgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Connect4Board {
    public static final int NUM_ROWS = 7;
    public static final int NUM_COLS = 9;
    public static final int NUM_PLAYERS = 2;

    private static final char P0_CELL = '0';
    private static final char P1_CELL = '1';
    private static final char EMPTY_CELL = '.';
    private static final char GAME_DRAW = 'd';

    private char board[][] = new char[NUM_ROWS][NUM_COLS];
    private int turnIndex = 0;
    private char winner = EMPTY_CELL;

    private RecentChipConnectionsDetails recentChipConnectionsDetails = new RecentChipConnectionsDetails();

    public void initializeBoard() {
        for (int i = 0; i < NUM_ROWS; i++) {
            Arrays.fill(board[i], EMPTY_CELL);
        }
    }

    public int getTurnIndex() {
        return turnIndex;
    }

    public boolean isGameOver() {
        return winner != EMPTY_CELL;
    }

    public boolean isGameDraw() {
        return winner == GAME_DRAW;
    }

    public void playAction(int col) throws InvalidAction {
        // check if the chosen column is not in bounds
        if (col < 0 || col >= NUM_COLS) throw new InvalidAction("Chosen column out of bounds", col, InvalidAction.ACTION_NOT_INTEGER_OR_OUT_OF_BOUNDS);

        // check if the chosen column is already filled
        int bottom_most_row_with_empty_cell_in_this_column = getBottomMostRowWithEmptyCellFor(col);
        if (bottom_most_row_with_empty_cell_in_this_column < 0) throw new InvalidAction("Chosen column is already filled", col, InvalidAction.ACTION_FILLED_COLUMN);

        // update grid
        char cell_fill_value;
        if (turnIndex % NUM_PLAYERS == 0) cell_fill_value = P0_CELL;
        else cell_fill_value = P1_CELL;
        board[bottom_most_row_with_empty_cell_in_this_column][col] = cell_fill_value;

        // check for winner
        setWinnerIfRecentCellConnectedRequiredNumberOfCells(new Cell(bottom_most_row_with_empty_cell_in_this_column, col));

        // increment turn index
        turnIndex += 1;
        if (turnIndex >= NUM_ROWS * NUM_COLS) {
            winner = GAME_DRAW;
        }
    }

    private int getBottomMostRowWithEmptyCellFor(int column) {
        for (int r = 0; r < NUM_ROWS; r++) {
            if (board[r][column] != EMPTY_CELL) {
                return r - 1;  // the previous row is empty: note, it can be -1 if called for a filled column
            }
        }
        return NUM_ROWS - 1;  // the bottom row itself
    }

    private void setWinnerIfRecentCellConnectedRequiredNumberOfCells(Cell recentCell) {  // sets winner if game ended
        recentChipConnectionsDetails.setRecentCell(recentCell);

        char startingCell = board[recentCell.row][recentCell.col];  // assumes recentCell is one of P0 or P1 (i.e. not EMPTY)

        for (int directionIndex = 0;
             directionIndex < RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS.length;
             directionIndex++) {

            for (int subDirectionIndex = 0;
                 subDirectionIndex < RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS[directionIndex].length;
                 subDirectionIndex++) {

                int directionToLookIn = RecentChipConnectionsDetails.DIRECTIONS_FOR_CHECKING_CONNECTIONS[directionIndex][subDirectionIndex];

                int steps = 0;

                while (true) {
                    // if the next step in the direction to look in is a valid connected cell, increment steps
                    Cell neighbor = recentCell.getNeighbor(directionToLookIn, steps + 1);  // initially '1' will be sent for steps
                    if (!neighbor.isInBounds(NUM_ROWS, NUM_COLS)) break;
                    if (board[neighbor.row][neighbor.col] != startingCell) break;
                    steps += 1;
                }

                recentChipConnectionsDetails.setNumConnectedCells(directionIndex, subDirectionIndex, steps);
            }
        }

        if (recentChipConnectionsDetails.hasConnectedRequiredNumberOfChips()) {
            winner = startingCell;
        }
    }

    public RecentChipConnectionsDetails getRecentChipConnectionsDetails() {
        return recentChipConnectionsDetails;
    }

    public String getInputString() {
        StringBuilder inputStringBuilder = new StringBuilder();

        // send player indices in initial rounds
        if (turnIndex == 0) {  // initial input to player 0
            inputStringBuilder.append(String.format("%d %d\n", 0, 1));
        } else if (turnIndex == 1) {  // initial input to player 1
            inputStringBuilder.append(String.format("%d %d\n", 1, 0));
        }

        // send turn index
        inputStringBuilder.append(turnIndex);
        inputStringBuilder.append('\n');

        // send board string
        inputStringBuilder.append(getBoardString());

        // send num valid actions
        List<Integer> validActions = getValidActions();
        inputStringBuilder.append(validActions.size());
        // send those actions
        for (Integer validAction : validActions) {
            inputStringBuilder.append('\n');
            inputStringBuilder.append(validAction);
        }
        inputStringBuilder.append('\n');

        // send opponent's last action
        if (turnIndex == 0) inputStringBuilder.append(-1);
        else inputStringBuilder.append(recentChipConnectionsDetails.getRecentCell().getCol());
        inputStringBuilder.append('\n');

        return inputStringBuilder.toString();
    }

    private String getBoardString() {
        StringBuilder boardString = new StringBuilder();
        for (int i = 0; i < NUM_ROWS; i++) {
            boardString.append(new String(board[i]));
            boardString.append("\n");
        }
        return boardString.toString();
    }

    private List<Integer> getValidActions() {
        List<Integer> validActions = new ArrayList<>();
        if (!isGameOver()) {
            for (int c = 0; c < NUM_COLS; c++) {
                if (board[0][c] == EMPTY_CELL) {  // if top row is empty cell for that column
                    validActions.add(c);
                }
            }
        }
        return validActions;
    }
}
