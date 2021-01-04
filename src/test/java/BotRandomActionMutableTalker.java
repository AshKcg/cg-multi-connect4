import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BotRandomActionMutableTalker {
    public static void main(String[] args) {
        Random random = new Random(0);

        boolean sendMessage = true;
        String messagaes1[] = {
                "That is what I call a pro gamer move",
                " baba yaga", "GG",
                "My moves are like art and my code is like spaghetti",
                "I meant to place my chip on another row lol"
        };
        String messagaes2[] = {
                "That's GG",
                "While you weren't looking, I replaced your chip hehehe",
                "I was programmed in HTML",
                "This game is good."
        };

        Scanner in = new Scanner(System.in);
        int myPlayerIndex = in.nextInt(); // 0 or 1 (Player 0 plays first)
        int oppPlayerIndex = in.nextInt(); // if your index is 0, this will be 1, and vice versa

        int turnIndex = myPlayerIndex - 1;
        String messagaes[] = myPlayerIndex == 0 ? messagaes1 : messagaes2;

        // game loop
        while (true) {
            turnIndex = in.nextInt();

            for (int i = 0; i < 7; i++) {
                String boardRow = in.next(); // one row of the board (has 7 characters)
            }

            List<Integer> validActions = new ArrayList<>();

            int numValidActions = in.nextInt(); // number of unfilled columns in the board
            for (int i = 0; i < numValidActions; i++) {
                int action = in.nextInt(); // a valid column index into which a chip can be dropped
                validActions.add(action);
            }

            int oppPreviousAction = in.nextInt(); // opponent's previous chosen column index (will be -1 for Player 0 in the first turn)

            // Write an answer using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // Output a column index to drop the chip in. Append message to show in the viewer.
            System.out.println(validActions.get(random.nextInt(validActions.size())) + (sendMessage ? " " + messagaes[turnIndex % messagaes.length] : ""));
        }
    }
}
