package connectXgame;

public class RecentChipConnectionsDetails {

    private static final int NUM_CELLS_TO_BE_CONNECTED = 4;  // minimum number of chips to be connected

    public static final int DIRECTIONS_FOR_CHECKING_CONNECTIONS[][] = {  // dimensions: [4][2]
            // first dimension: 4 (one each for the four directions: horizontal, vertical, / and \
            // second dimension: 2 (each going one of the two ways from the recent cell (Eg: for horizontal "<- and ->"
            {Cell.EAST, Cell.WEST},  // horizontal (forward and backward respectively)
            {Cell.NORTH, Cell.SOUTH},  // vertical
            {Cell.NORTH_EAST, Cell.SOUTH_WEST},  // forward slash diagonal '/'
            {Cell.NORTH_WEST, Cell.SOUTH_EAST}  // backward slash diagonal '\'
    };

    private Cell recentCell = new Cell(0, 0);
    private int numConnectionsInEachDirection[][] = new int[4][2];  // for each of the above mentioned


    void setRecentCell(Cell cell) {
        recentCell.set(cell);
    }

    public boolean hasConnectedRequiredNumberOfChips() {
        for (int directionIndex = 0; directionIndex < DIRECTIONS_FOR_CHECKING_CONNECTIONS.length; directionIndex++) {
            if (hasConnectedRequiredNumberOfChips(directionIndex)) return true;
        }
        return false;
    }

    public boolean hasConnectedRequiredNumberOfChips(int directionIndex) {
        return numConnectionsInEachDirection[directionIndex][0] + numConnectionsInEachDirection[directionIndex][1] >= NUM_CELLS_TO_BE_CONNECTED - 1;
        // note: -1 because one cell is recentCell which is not added in the array
    }

    void setNumConnectedCells(int directionIndex, int subDirectionIndex, int numConnectedCells) {
        // directionIndex must be in [0, 4)
        // subDirectionIndex must be in [0, 2)
        numConnectionsInEachDirection[directionIndex][subDirectionIndex] = numConnectedCells;
    }

    public int getNumConnectedCells(int directionIndex, int subDirectionIndex) {
        return numConnectionsInEachDirection[directionIndex][subDirectionIndex];
    }

    public Cell getRecentCell() {
        return recentCell;
    }
}
