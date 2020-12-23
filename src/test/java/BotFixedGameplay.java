import java.util.Scanner;

public class BotFixedGameplay {


    private static final int p0_last_horizontal_line[] = {0, 0, 1, 1, 2, 2, 4, 4, 5, 5, 6, 6, 3};
    private static final int p0_middle_vertical_line[] = {3, 1, 3, 2, 3, 1, 3};
    private static final int p0_v_shape[] = {2, 1, 2, 1, 1, 5, 4, 5, 4, 4, 5, 6, 6, 6, 6, 0, 0, 0, 0, 5, 3};
    private static final int p0_full_diag[] = {0, 1, 1, 0, 2, 2, 2, 1, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 3, 6, 3, 6, 6, 3, 0, 3};
    private static final int p1_stops_p0_full_diag_and_wins[] = {0, 1, 1, 0, 2, 2, 2, 1, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 3, 6, 3, 0, 3};
    private static final int p1_t_shape[] = {0, 0, 0, 0, 1, 1, 1, 1, 2, 3, 2, 3, 2, 2, 4, 3, 4, 5, 4, 4, 6, 6, 6, 6, 5, 6, 5, 5, 5, 3};
    private static final int p0_7_shape[] = {5, 5, 5, 4, 4, 4, 3, 3, 2, 3, 3, 2, 4, 6, 6, 6, 5, 2, 6};
    private static final int p1_x_shape[] = {0, 0, 0, 1, 1, 1, 2, 2, 2, 2, 6, 6, 6, 5, 5, 5, 4, 4, 4, 4, 3, 3, 0, 3, 6, 3, 6, 3};
    private static final int p1_up_arrow_shape[] = {0, 0, 1, 2, 1, 1, 3, 3, 6, 6, 2, 3, 2, 2, 0, 3, 6, 5, 5, 5, 4, 6, 4, 6, 4, 4, 0, 3};
    private static final int draw[] = {0, 1, 2, 3, 4, 5, 6, 0, 1, 2, 3, 4, 5, 6, 3, 6, 5, 0, 1, 2, 0, 4, 1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6};
    private static final int p0_timeout_in_its_second_play[] = {0, 1, -1, 0};
    private static final int p1_timeout_in_its_second_play[] = {0, 1, 0, -1, 0};
    private static final int p0_filled_column[] = {0, 0, 0, 0, 0, 0, 0, 0};
    private static final int p1_filled_column[] = {0, 1, 1, 1, 1, 1, 1, 1};
    private static final int p0_bad_output_in_its_third_play[] = {0, 0, 0, 0, -2, 0};  // - sign will be there in the output
    private static final int p1_bad_output_in_its_third_play[] = {0, 0, 0, 0, 0, -2, 0};
    private static final int p0_out_of_bounds[] = {0, 0, 0, 0, 7, 0};
    private static final int p1_out_of_bounds[] = {0, 0, 0, 0, 0, 8};
    private static final int starting_replay[] = {0, 6, 1, 5, 2, 4, 3};

    private static final int ACTIONS[] = starting_replay;


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int myPlayerIndex = in.nextInt(); // 0 or 1 (Player 0 plays first)
        int oppPlayerIndex = in.nextInt(); // if your index is 0, this will be 1, and vice versa
        int turnIndex = myPlayerIndex - 2;

        // game loop
        while (true) {
            turnIndex = in.nextInt();

            for (int i = 0; i < 7; i++) {
                String boardRow = in.next(); // one row of the board (has 7 characters)
            }

            int firstValidAction = -1;

            int numValidActions = in.nextInt(); // number of unfilled columns in the board
            for (int i = 0; i < numValidActions; i++) {
                int action = in.nextInt(); // a valid column index into which a chip can be dropped
                if (firstValidAction == -1) {
                    firstValidAction = action;
                }
            }

            int oppPreviousAction = in.nextInt(); // opponent's previous chosen column index (will be -1 for Player 0 in the first turn)

            // Write an answer using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // Output a column index to drop the chip in. Append message to show in the viewer.
            if (ACTIONS[turnIndex] == -1) continue;  // -1 => output nothing => timeout
            System.out.println(ACTIONS[turnIndex]);
        }
    }
}
