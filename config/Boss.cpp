#include <iostream>
#include <string>


/**
 * Drop chips in the columns.
 * Connect at least 4 of your chips in any direction to win.
 **/


constexpr int NUM_ROWS = 7;
constexpr int NUM_COLS = 9;
constexpr int NUM_CHIPS_TO_BE_CONNECTED = 4;
constexpr char CHIP_FIRST_PLAYER = '0';
constexpr char CHIP_SECOND_PLAYER = '1';
constexpr char CHIP_EMPTY_CELL = '.';
constexpr int MINIMAX_DEPTH = 3;  // my play, opp play, my play
constexpr int MINIMAX_POSITIVE_INFINITE = 100000;
constexpr int MINIMAX_NEGATIVE_INFINITE = -MINIMAX_POSITIVE_INFINITE;


// #define XY_MODE_PRINT  // comment this if should be printed as row,col


constexpr int UP = 0;
constexpr int RIGHT = 1;
constexpr int LEFT = 2;
constexpr int DOWN = 3;
constexpr int UP_RIGHT = 4;
constexpr int UP_LEFT = 5;
constexpr int DOWN_RIGHT = 6;
constexpr int DOWN_LEFT = 7;
constexpr char CELL_PRINT_ROW_COL_SEP = ',';


void printInitialTabs(std::ostream& os, int tab_length) {
    int num_spaces = 4 * tab_length;
    for (int t = 0; t < num_spaces; t++) os << ' ';
}


class Cell {
    const static int additions[8][2];
    const static int opposite_directions[8];

public:
    int row;
    int col;

    Cell() {
        row = 0;
        col = 0;
    }

    Cell(int _row, int _col) {
        row = _row;
        col = _col;
    }

    Cell(const Cell &cell) {
        row = cell.row;
        col = cell.col;
    }

    void set_to(int _row, int _col) {
        row = _row;
        col = _col;
    }

    void set_to(const Cell &cell) {
        row = cell.row;
        col = cell.col;
    }

    inline void set_to_neighbor(int direction, int steps = 1) {
        row += additions[direction][0] * steps;
        col += additions[direction][1] * steps;
    }

    inline Cell get_neighbor(int direction, int steps = 1) const {
        return Cell(row + additions[direction][0] * steps, col + additions[direction][1] * steps);
    }

    int manhattan_dist(const Cell &other) const {
        return (abs(row - other.row) + abs(col - other.col));
    }

    int euclidean_dist(const Cell &other) const {
        int dy = abs(row - other.row);
        int dx = abs(col - other.col);
        return dy > dx ? dy : dx;
    }

    friend std::ostream &operator<<(std::ostream &os, const Cell &cell) {
#ifdef XY_MODE_PRINT
        os << static_cast<int>(cell.col) << CELL_PRINT_ROW_COL_SEP << static_cast<int>(cell.row);
#else
        os << static_cast<int>(cell.row) << CELL_PRINT_ROW_COL_SEP << static_cast<int>(cell.col);
#endif
        return os;
    }

    bool is_in_bounds(int height, int width) const {
        return !(row < 0 || row >= height || col < 0 || col >= width);
    }

    bool operator==(const Cell &rhs) const {
        return row == rhs.row && col == rhs.col;
    }

    bool operator!=(const Cell &rhs) const {
        return !(rhs == *this);
    }

    int get_opposite_direction(int direction) {
        return opposite_directions[direction];
    }
};


const int Cell::additions[8][2] = {
        {-1, 0}, {0, 1}, {0, -1}, {1, 0},  // UP 0; RIGHT 1; LEFT 2; DOWN 3
        {-1, 1}, {-1, -1}, {1, 1}, {1, -1}  // UP_RIGHT 4; UP_LEFT 5; DOWN_RIGHT 6; DOWN_LEFT 7
};

const int Cell::opposite_directions[8] = { DOWN, LEFT, RIGHT, UP, DOWN_LEFT, DOWN_RIGHT, UP_LEFT, UP_RIGHT };


class Utility {
public:
    int p1_score;
    int p2_score;
    Utility() {
        p1_score = 0;
        p2_score = 0;
    }
    Utility(int _p1_score, int _p2_score) {
        p1_score = _p1_score;
        p2_score = _p2_score;
    }
    int getAsInt() {
        return p1_score - p2_score;
    }
    void operator += (const Utility& other) {
        p1_score += other.p1_score;
        p2_score += other.p2_score;
    }
    void operator -= (const Utility& other) {
        p1_score -= other.p1_score;
        p2_score -= other.p2_score;
    }
    friend std::ostream &operator<<(std::ostream &os, const Utility &utility) {
        os << "U{" << utility.p1_score << ',' << utility.p2_score << '}';
        return os;
    }
};


class Connect4 {

    public:
        void set_one_board_row(int rowIndex, std::string rowString);
        int get_move(int player);

    private:
        char board[NUM_ROWS][NUM_COLS];
        Utility boardUtility;
        char winner = CHIP_EMPTY_CELL;

        void printState(std::ostream& os, int tab_length) {
            for (int r = 0; r < NUM_ROWS; r++) {
                printInitialTabs(os, tab_length);
                for (int c = 0; c < NUM_COLS; c++) os << board[r][c];
                os << '\n';
            }
            printInitialTabs(os, tab_length);
            os << boardUtility << " W:" << winner << '\n';
        }

        int minimax_max_value(int depth, int* put_best_action_col_in_this);
        int minimax_min_value(int depth, int* put_best_action_col_in_this);

        bool can_place_chip_in_col(int col_index) const { return board[0][col_index] == CHIP_EMPTY_CELL; }

        int get_resting_row_for_a_chip_placed_in_col(int col_index) const {
            for (int r = NUM_ROWS - 1; r >= 0; r--) {
                if (board[r][col_index] == CHIP_EMPTY_CELL) return r;
            }
            return -1;  // no empty cell, filled column
        }

        int get_utility();
        int num_times_get_utility_is_called = 0;  // don't forget to reset it every time before calling minimax top level

        Utility getUtilityDeltaOfTheMove(int row, int col, bool is_p1_turn);
};

void Connect4::set_one_board_row(int rowIndex, std::string rowString) {
    for (int c = 0; c < NUM_COLS; c++) {
        board[rowIndex][c] = rowString[c];
    }
}

int Connect4::get_move(int player) {
    int best_move;
    int best_utility;
    num_times_get_utility_is_called = 0;

    if (player == 0) {
        best_utility = minimax_max_value(1, &best_move);
    } else {
        best_utility = minimax_min_value(1, &best_move);
    }

    std::cerr << "Utility: " << best_utility << "; Num leaf nodes: " << num_times_get_utility_is_called << '\n';

    return best_move;
}

int Connect4::minimax_max_value(int depth, int* put_best_action_col_in_this) {
#ifdef PRINT_ALL_DEBUG
    printInitialTabs(std::cerr, depth - 1); std::cerr << "Minimax:MAX depth:" << depth << '\n';
#endif

    // if terminal test passed: return utility of the state
    if (winner == CHIP_FIRST_PLAYER) return MINIMAX_POSITIVE_INFINITE;
    else if (winner == CHIP_SECOND_PLAYER) return MINIMAX_NEGATIVE_INFINITE;

    if (depth >= MINIMAX_DEPTH) return get_utility();

#ifdef PRINT_ALL_DEBUG
    printInitialTabs(std::cerr, depth - 1); std::cerr << "Not a leaf node\n";
#endif

    int v = MINIMAX_NEGATIVE_INFINITE - 1;  // so that at least one action is chosen
    for (int c = 0; c < NUM_COLS; c++) {
        if (!can_place_chip_in_col(c)) continue;

        // play action
        int resting_row = get_resting_row_for_a_chip_placed_in_col(c);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Play col:" << c << " row:" << resting_row << '\n';
#endif

        board[resting_row][c] = CHIP_FIRST_PLAYER;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Board after the move:\n";
        printState(std::cerr, depth - 1);
#endif

        Utility uDelta = getUtilityDeltaOfTheMove(resting_row, c, true);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "utilityDelta: " << uDelta << '\n';
#endif

        boardUtility += uDelta;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "boardUtility: " << boardUtility << '\n';
#endif

        // get min value of the result
        int u = minimax_min_value(depth + 1, nullptr);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Result of calling min_value for next depth = u = " << u << '\n';
#endif

        // undo play action
        board[resting_row][c] = CHIP_EMPTY_CELL;
        boardUtility -= uDelta;
        winner = CHIP_EMPTY_CELL;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Move un done\n";
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Board after undoing:\n";
        printState(std::cerr, depth - 1);
#endif

        // process the new utility: v takes max of them
        if (v < u) {
#ifdef PRINT_ALL_DEBUG
            printInitialTabs(std::cerr, depth - 1); std::cerr << "This u is new max utility\n";
#endif
            v = u;
            if (put_best_action_col_in_this != nullptr) {
                *put_best_action_col_in_this = c;
            }
        }
    }

    return v;
}

int Connect4::minimax_min_value(int depth, int* put_best_action_col_in_this) {
#ifdef PRINT_ALL_DEBUG
    printInitialTabs(std::cerr, depth - 1); std::cerr << "Minimax:MIN depth:" << depth << '\n';
#endif

    // if terminal test passed: return utility of the state
    if (winner == CHIP_FIRST_PLAYER) return MINIMAX_POSITIVE_INFINITE;
    else if (winner == CHIP_SECOND_PLAYER) return MINIMAX_NEGATIVE_INFINITE;

    if (depth >= MINIMAX_DEPTH) return get_utility();

#ifdef PRINT_ALL_DEBUG
    printInitialTabs(std::cerr, depth - 1); std::cerr << "Not a leaf node\n";
#endif

    int v = MINIMAX_POSITIVE_INFINITE + 1;  // so that at least one action is chosen
    for (int c = 0; c < NUM_COLS; c++) {
        if (!can_place_chip_in_col(c)) continue;

        // play action
        int resting_row = get_resting_row_for_a_chip_placed_in_col(c);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Play col:" << c << " row:" << resting_row << '\n';
#endif

        board[resting_row][c] = CHIP_SECOND_PLAYER;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Board after the move:\n";
        printState(std::cerr, depth - 1);
#endif

        Utility uDelta = getUtilityDeltaOfTheMove(resting_row, c, false);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "utilityDelta: " << uDelta << '\n';
#endif

        boardUtility += uDelta;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "boardUtility: " << boardUtility << '\n';
#endif

        // get max value of the result
        int u = minimax_max_value(depth + 1, nullptr);
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Result of calling max_value for next depth = u = " << u << '\n';
#endif

        // undo play action
        board[resting_row][c] = CHIP_EMPTY_CELL;
        boardUtility -= uDelta;
        winner = CHIP_EMPTY_CELL;
#ifdef PRINT_ALL_DEBUG
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Move un done\n";
        printInitialTabs(std::cerr, depth - 1); std::cerr << "Board after undoing:\n";
        printState(std::cerr, depth - 1);
#endif

        // process the new utility: v takes min of them
        if (v > u) {
#ifdef PRINT_ALL_DEBUG
            printInitialTabs(std::cerr, depth - 1); std::cerr << "This u is new max utility\n";
#endif
            v = u;
            if (put_best_action_col_in_this != nullptr) {
                *put_best_action_col_in_this = c;
            }
        }
    }
    return v;
}

int Connect4::get_utility() {
    num_times_get_utility_is_called++;
    return boardUtility.getAsInt();
}

Utility Connect4::getUtilityDeltaOfTheMove(int row, int col, bool is_p1_turn) {
    Utility utilityDelta;
    Cell curCell(row, col);

    static const int FOUR_DIRECTIONS[] = {UP, RIGHT, UP_RIGHT, DOWN_RIGHT};
    for (int d : FOUR_DIRECTIONS) {
        for (int starting_cell_position_delta = 0; starting_cell_position_delta < 4; starting_cell_position_delta++) {
            Cell cell = curCell.get_neighbor(curCell.get_opposite_direction(d), starting_cell_position_delta);

            // check if this starting cell is of a valid line
            if (!(cell.is_in_bounds(NUM_ROWS, NUM_COLS) && cell.get_neighbor(d, 3).is_in_bounds(NUM_ROWS, NUM_COLS))) {
                continue;
            }
            
            int num_p1_chips = 0;
            int num_p2_chips = 0;
            
            for (int i = 0; i < 4; i++) {

                if (board[cell.row][cell.col] == CHIP_FIRST_PLAYER) {
                    num_p1_chips++;
                } else if (board[cell.row][cell.col] == CHIP_SECOND_PLAYER) {
                    num_p2_chips++;
                }

                cell.set_to_neighbor(d);
            }

            if (num_p1_chips == NUM_CHIPS_TO_BE_CONNECTED) {
                winner = CHIP_FIRST_PLAYER;
                utilityDelta.p1_score = utilityDelta.p2_score = 0;
                return utilityDelta;
            } else if (num_p2_chips == NUM_CHIPS_TO_BE_CONNECTED) {
                winner = CHIP_SECOND_PLAYER;
                utilityDelta.p1_score = utilityDelta.p2_score = 0;
                return utilityDelta;
            }

            // a dropped chip will
            // either add to the utility of the dropper
            // or remove the previously added utility of opponent
            // or no change

            if (is_p1_turn) {
                if (num_p2_chips == 0) {
                    // no opponent's chips => utility boost (1 more chip added to line)
                    utilityDelta.p1_score++;
                } else {  // opponent's chips already exist in this line
                    if (num_p1_chips == 1) {
                        // blocking for the first time
                        utilityDelta.p2_score -= num_p2_chips;
                    } else {
                        // already blocked this line for the opponent
                        // no change
                    }
                }
            } else { // p2's turn
                if (num_p1_chips == 0) {
                    utilityDelta.p2_score++;
                } else {
                    if (num_p2_chips == 1) {
                        utilityDelta.p1_score -= num_p1_chips;
                    } else {
                        // no change
                    }
                }
            }
        }
    }

    return utilityDelta;
}


int main()
{
    Connect4 connect4;

    // initialization input
    int myId; // 0 or 1 (Player 0 plays first)
    int oppId; // if your index is 0, this will be 1, and vice versa
    std::cin >> myId >> oppId; std::cin.ignore();

    // game loop
    while (1) {
        // input turn index
        int turnIndex; // starts from 0; As the game progresses, Player0 gets [0,2,4,...] and Player1 gets [1,3,5,...]
        std::cin >> turnIndex; std::cin.ignore();
        if (turnIndex < 0) break;  // for local run purpose

        // input board
        for (int i = 0; i < 7; i++) {
            std::string boardRow; // one row of the board (from top to bottom)
            std::cin >> boardRow; std::cin.ignore();
            connect4.set_one_board_row(i, boardRow);
        }

        // input valid actions
        int numValidActions; // number of unfilled columns in the board
        std::cin >> numValidActions; std::cin.ignore();
        for (int i = 0; i < numValidActions; i++) {
            int action; // a valid column index into which a chip can be dropped
            std::cin >> action; std::cin.ignore();
        }

        // input opponent previous action
        int oppPreviousAction; // opponent's previous chosen column index (will be -1 for Player 0 in the first turn)
        std::cin >> oppPreviousAction; std::cin.ignore();

        // Write an action using cout. DON'T FORGET THE "<< endl"
        // To debug: cerr << "Debug messages..." << endl;


        // Output a column index to drop the chip in. Append message to show in the viewer.
        std::cout << connect4.get_move(myId) << std::endl;
        std::cerr << std::endl;
    }
}
