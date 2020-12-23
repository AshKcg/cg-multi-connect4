import java.util.*;


/**
 * Drop chips in the columns.
 * Connect at least 4 of your chips in any direction to win.
 **/

class StubGeneratedBot {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myId = in.nextInt(); // 0 or 1 (Player 0 plays first)
        int oppId = in.nextInt(); // if your index is 0, this will be 1, and vice versa

        // game loop
        while (true) {
            int turnIndex = in.nextInt(); // starts from 0; As the game progresses, Player0 gets [0,2,4,...,40] and Player1 gets [1,3,5,...,41]
            for (int i = 0; i < 6; i++) {
                String boardRow = in.next(); // one row of the board (has 7 characters)
            }
            int numValidActions = in.nextInt(); // number of unfilled columns in the board
            for (int i = 0; i < numValidActions; i++) {
                int action = in.nextInt(); // a valid column index into which a chip can be dropped
            }
            int oppPreviousAction = in.nextInt(); // opponent's previous chosen column index (will be -1 for Player 0 in the first turn)

            // Write an answer using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // Output a column index to drop the chip in. Append message to show in the viewer.
            System.out.println("0");
        }
    }
}
