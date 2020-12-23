package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

import connectXgame.InvalidAction;


// Uncomment the line below and comment the line under it to create a Solo Game
// public class Player extends AbstractSoloPlayer {
public class Player extends AbstractMultiplayerPlayer {
    @Override
    public int getExpectedOutputLines() {
        // Returns the number of expected lines of outputs for a player

        // TODO: Replace the returned value with a valid number. Most of the time the value is 1. 
        return 1;
    }

    private static final int NUM_MAX_CHARS_IN_VIEWER_MESSAGE_PER_LINE = 15;  // a new line will be inserted after each this many chars

    public Action getAction() throws TimeoutException, InvalidAction {
        String output = getOutputs().get(0);

        String playerChosenColumnIndex = "";
        try {
            playerChosenColumnIndex = output.substring(0, 1);
            int chosenAction = Integer.parseInt(playerChosenColumnIndex);

            String viewerMessage = output.substring(1).trim();
            if (viewerMessage.length() > NUM_MAX_CHARS_IN_VIEWER_MESSAGE_PER_LINE) {
                viewerMessage = insertPeriodically(viewerMessage, "\n", NUM_MAX_CHARS_IN_VIEWER_MESSAGE_PER_LINE);
            }

            return new Action(this, chosenAction, viewerMessage);
        } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
            throw new InvalidAction(
                    "Wrong output!",
                    playerChosenColumnIndex,
                    InvalidAction.ACTION_NOT_INTEGER_OR_OUT_OF_BOUNDS);
        }
    }

    // the following function is copy pasted from stackoverflow
    // https://stackoverflow.com/questions/537174/putting-char-into-a-java-string-for-each-n-characters
    // answered by Jon Skeet
    public static String insertPeriodically(String text, String insert, int period)
    {
        StringBuilder builder = new StringBuilder(
                text.length() + insert.length() * (text.length()/period)+1);

        int index = 0;
        String prefix = "";
        while (index < text.length())
        {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }
}
