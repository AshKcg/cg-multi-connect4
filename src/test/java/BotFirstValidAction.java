import java.util.Scanner;

public class BotFirstValidAction {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int myPlayerIndex = in.nextInt(); // 0 or 1 (Player 0 plays first)
        int oppPlayerIndex = in.nextInt(); // if your index is 0, this will be 1, and vice versa

        // game loop
        while (true) {

            int turnIndex = in.nextInt();  // read turn index

            for (int i = 0; i < 6; i++) {
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
            System.out.println(firstValidAction);
        }
    }
}
