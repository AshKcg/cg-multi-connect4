package connectXgame;

public class Cell {
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int WEST = 2;
    public static final int SOUTH = 3;
    public static final int NORTH_EAST = 4;
    public static final int NORTH_WEST = 5;
    public static final int SOUTH_EAST = 6;
    public static final int SOUTH_WEST = 7;

    private static final int ADDITIONS[][] = {  // dimensions: [8][2]
            {-1, 0}, {0, 1}, {0, -1}, {1, 0},  // NORTH 0; EAST 1; WEST 2; SOUTH 3
            {-1, 1}, {-1, -1}, {1, 1}, {1, -1}  // NORTH_EAST 4; NORTH_WEST 5; SOUTH_EAST 6; SOUTH_WEST 7
    };

    private static final int OPPOSITE_DIRECTIONS[] = { SOUTH, WEST, EAST, NORTH, SOUTH_WEST, SOUTH_EAST, NORTH_WEST, NORTH_EAST };

    int row, col;

    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    void set(int row, int col) {
        this.row = row;
        this.col = col;
    }

    void set(Cell cell) {
        row = cell.row;
        col = cell.col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Cell getNeighbor(int direction, int steps) {
        return new Cell(row + ADDITIONS[direction][0] * steps, col + ADDITIONS[direction][1] * steps);
    }

    public Cell getNeighbor(int direction) {  // single step
        return getNeighbor(direction, 1);
    }

    public boolean isInBounds(int MAX_ROWS, int MAX_COLS) {
        return row >= 0 && row < MAX_ROWS && col >= 0 && col < MAX_COLS;
    }

    static public int getOppositeDirection(int direction) {
        return OPPOSITE_DIRECTIONS[direction];
    }
}
